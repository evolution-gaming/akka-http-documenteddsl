package akka.http.documenteddsl

import akka.http.documenteddsl.directives._


trait DDirectives
  extends DRouteConcatenation
    with RouteDDirectives
    with DocumentationDDirectives
    with MethodDDirectives
    with PathDDirectives
    with HeaderDDirectives
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