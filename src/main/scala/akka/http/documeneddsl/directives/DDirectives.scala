package akka.http.documeneddsl.directives

import akka.http.documeneddsl.DRouteConcatenation
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

  type DDirective[T]  = akka.http.documeneddsl.directives.DDirective[T]
  type DDirective0    = akka.http.documeneddsl.directives.DDirective0
  type DDirective1[T] = akka.http.documeneddsl.directives.DDirective1[T]

  type DRoute         = akka.http.documeneddsl.DRoute

}