package akka.http.documenteddsl.directives

import akka.http.documenteddsl.DRouteConcatenation
import akka.http.scaladsl.server.directives.RouteDirectives.{redirect => akkaRedirect}

trait DDirectives
  extends DRouteConcatenation
    with RouteDDirectives
    with DocumentationDDirectives
    with MethodDDirectives
    with PathDDirectives
    with MarshallingDDirectives
    with UnmarshallingDDirectives
    with ParameterDDirectives
    with FormFieldDDirectives
    with SessionDDirectives

object DDirectives extends DDirectives {

  type DDirective[T]  = akka.http.documenteddsl.directives.DDirective[T]
  type DDirective0    = akka.http.documenteddsl.directives.DDirective0
  type DDirective1[T] = akka.http.documenteddsl.directives.DDirective1[T]

  type DRoute         = akka.http.documenteddsl.DRoute

}