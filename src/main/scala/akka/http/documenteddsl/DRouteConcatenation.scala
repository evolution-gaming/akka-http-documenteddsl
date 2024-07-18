package akka.http.documenteddsl

import akka.http.documenteddsl.directives.RouteDDirectives
import akka.http.documenteddsl.documentation.Documentation
import akka.http.scaladsl.server.RouteConcatenation

trait DRouteConcatenation {

  implicit def _enhanceRouteWithConcatenation(route: DRoute): DRouteConcatenation.DRouteWithConcatenation =
    new DRouteConcatenation.DRouteWithConcatenation(route: DRoute)

  def concat(routes: DRoute*): DRoute = routes.foldLeft[DRoute](RouteDDirectives.reject)(_ |~| _)

}

object DRouteConcatenation {

  class DRouteWithConcatenation(left: DRoute) {
    def |~|(right: DRoute): DRoute = {
      val concatenated = new RouteConcatenation.RouteWithConcatenation(left) ~ right

      new DRoute(concatenated) {
        override def selfDescribe(initial: Documentation): Documentation = {
          val withRight = right selfDescribe Documentation()
          val withLeft  = left  selfDescribe Documentation()

          Documentation(initial.routes ++ withLeft.routes ++ withRight.routes)
        }
      }
    }
  }

}