package akka.http.documenteddsl.directives

import akka.http.documenteddsl.documentation.RouteDocumentation
import akka.http.scaladsl.model.{ContentType, StatusCode, StatusCodes}
import akka.http.scaladsl.server.Directive
import org.coursera.autoschema.AutoSchema
import play.api.libs.json.{JsValue, Writes}

import scala.reflect.runtime.{universe => ru}

trait UnmarshallingDDirectives {

  final class SuccessfulOut[T : ru.TypeTag](status: StatusCode, example: Option[JsValue]) extends DDirective0 {
    def describe(w: RouteDocumentation)(implicit as: AutoSchema): RouteDocumentation = w.outSuccess[T](status.intValue, example)
    def delegate = Directive.Empty
  }

  final class ErrorOut(status: StatusCode, contentType: Option[String], description: Option[String]) extends DDirective0 {
    def describe(w: RouteDocumentation)(implicit as: AutoSchema): RouteDocumentation = w.outError(status, contentType, description)
    def delegate = Directive.Empty
  }

  object Out {
    def apply[T : ru.TypeTag]: SuccessfulOut[T] = new SuccessfulOut(StatusCodes.OK, None)
    def apply[T : ru.TypeTag](status: StatusCode): SuccessfulOut[T] = new SuccessfulOut(status, None)
    def apply[T : ru.TypeTag](example: T)(implicit writes: Writes[T]): SuccessfulOut[T] = new SuccessfulOut(StatusCodes.OK, Some(writes writes example))
    def apply[T : ru.TypeTag](status: StatusCode, example: T)(implicit writes: Writes[T]): SuccessfulOut[T] = new SuccessfulOut(status, Some(writes writes example))
    def success(status: StatusCode): SuccessfulOut[Nothing] = new SuccessfulOut(status, None)
    def error(status: StatusCode): ErrorOut = new ErrorOut(status, None, None)
    def apply(status: StatusCode, description: String): ErrorOut = new ErrorOut(status, None, Some(description))
    def apply(status: StatusCode, contentType: ContentType, description: String): ErrorOut = new ErrorOut(status, Some(contentType.toString()), Some(description))
  }

}

object UnmarshallingDDirectives extends UnmarshallingDDirectives