package akka.http.documenteddsl.directives

import akka.http.documenteddsl.PreprocessedFromStringUnmarshaller
import akka.http.documenteddsl.documentation.{ParamDocumentation, RouteDocumentation}
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.directives.FormFieldDirectives
import org.coursera.autoschema.AutoSchema

import scala.reflect.runtime.{universe => ru}

trait FormFieldDDirectives {

  case class FormField[T : ru.TypeTag](name: String)(implicit su: PreprocessedFromStringUnmarshaller[T]) extends DDirective1[T] {
    def describe(w: RouteDocumentation)(implicit as: AutoSchema): RouteDocumentation = w.parameter[T](name, required = true, origin = ParamDocumentation.Origin.Form)
    def delegate: Directive1[T] = {
      import FormFieldDirectives._
      import su.fsu
      formField(name.as[T])
    }
  }

  case class OptFormField[T : ru.TypeTag](name: String)(implicit su: PreprocessedFromStringUnmarshaller[T]) extends DDirective1[Option[T]] {
    def describe(w: RouteDocumentation)(implicit as: AutoSchema): RouteDocumentation = w.parameter[T](name, required = false, origin = ParamDocumentation.Origin.Form)
    def delegate: Directive1[Option[T]] = {
      import FormFieldDirectives._
      formField(name.as(su.fsu).?)
    }
  }

  case class DefaultFormField[T : ru.TypeTag](name: String, default: T)(implicit su: PreprocessedFromStringUnmarshaller[T]) extends DDirective1[T] {
    def describe(w: RouteDocumentation)(implicit as: AutoSchema): RouteDocumentation = w.parameter[T](name, required = false, origin = ParamDocumentation.Origin.Form)
    def delegate: Directive1[T] = {
      import FormFieldDirectives._
      formField(name.as(su.fsu).?(default))
    }
  }
}

object FormFieldDDirectives extends FormFieldDDirectives
