package akka.http.documenteddsl.directives

import akka.http.documenteddsl.PreprocessedFromStringUnmarshaller
import akka.http.documenteddsl.documentation.{ParamDocumentation, RouteDocumentation}
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.directives.ParameterDirectives
import org.coursera.autoschema.AutoSchema

import scala.reflect.runtime.{universe => ru}

trait ParameterDDirectives {

  case class Param[T : ru.TypeTag](name: String)(implicit su: PreprocessedFromStringUnmarshaller[T]) extends DDirective1[T] {
    def describe(w: RouteDocumentation)(implicit as: AutoSchema): RouteDocumentation = w.parameter[T](name, required = true, origin = ParamDocumentation.Origin.Query)
    def delegate: Directive1[T] = {
      import ParameterDirectives._
      import su.fsu
      parameter(name.as[T])
    }
  }

  case class OptParam[T : ru.TypeTag](name: String)(implicit su: PreprocessedFromStringUnmarshaller[T]) extends DDirective1[Option[T]] {
    def describe(w: RouteDocumentation)(implicit as: AutoSchema): RouteDocumentation = w.parameter[T](name, required = false, origin = ParamDocumentation.Origin.Query)
    def delegate: Directive1[Option[T]] = {
      import ParameterDirectives._
      parameter(name.as(su.fsu).?)
    }
  }
}

object ParameterDDirectives extends ParameterDDirectives