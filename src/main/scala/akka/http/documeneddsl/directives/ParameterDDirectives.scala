package akka.http.documeneddsl.directives

import akka.http.documeneddsl.documentation.{ParamDocumentation, RouteDocumentation}
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.directives.ParameterDirectives
import akka.http.scaladsl.unmarshalling._
import org.coursera.autoschema.AutoSchema

import scala.reflect.runtime.{universe => ru}

trait ParameterDDirectives {

  case class Param[T : ru.TypeTag](name: String)(implicit fsd: FromStringUnmarshaller[T]) extends DDirective1[T] {
    def describe(w: RouteDocumentation)(implicit as: AutoSchema): RouteDocumentation = w.parameter[T](name, required = true, origin = ParamDocumentation.Origin.Query)
    def delegate: Directive1[T] = {
      import ParameterDirectives._
      parameter(name.as[T])
    }
  }

  case class OptParam[T : ru.TypeTag](name: String)(implicit fsd: FromStringUnmarshaller[T]) extends DDirective1[Option[T]] {
    def describe(w: RouteDocumentation)(implicit as: AutoSchema): RouteDocumentation = w.parameter[T](name, required = false, origin = ParamDocumentation.Origin.Query)
    def delegate: Directive1[Option[T]] = {
      import ParameterDirectives._
      parameter(name.as(fsd).?)
    }
  }

}

object ParameterDDirectives extends ParameterDDirectives