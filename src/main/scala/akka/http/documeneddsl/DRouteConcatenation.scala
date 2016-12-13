package akka.http.documeneddsl

import akka.http.documeneddsl.directives.RouteDDirectives
import akka.http.documeneddsl.documentation.Documentation
import akka.http.scaladsl.server.{Route, RouteConcatenation}

import scala.language.implicitConversions

trait DRouteConcatenation {

  implicit def _enhanceRouteWithConcatenation(route: DRoute): DRouteConcatenation.DRouteWithConcatenation =
    new DRouteConcatenation.DRouteWithConcatenation(route: DRoute)

  def concat(routes: DRoute*): DRoute = routes.foldLeft[DRoute](RouteDDirectives.reject)(_ |~| _)

}

object DRouteConcatenation {

  class DRouteWithConcatenation(route: DRoute) {
    def |~|(other: DRoute): DRoute = {
      val concatenated = new RouteConcatenation.RouteWithConcatenation(route).~(other)
      new DRoute(concatenated) {
        override def describe(rw: Documentation): Documentation = {
          def document(route: Route, doc: Documentation): Documentation = route match {
            case route: DRoute => route.describe(doc)
            case _ => doc
          }

          document(route, document(other, rw))
        }
      }
    }
  }

}