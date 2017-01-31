package akka.http.documenteddsl

import akka.http.documenteddsl.documentation.Documentation
import akka.http.scaladsl.server.{RequestContext, _}

import scala.concurrent.Future

class DRoute(
  underlying: Route,
  writer: Documentation => Documentation = identity) extends Route {

  def selfDescribe(doc: Documentation): Documentation = writer apply doc
  override def apply(ctx: RequestContext): Future[RouteResult] = underlying apply ctx
  override def toString = "DocumentedRoute()"
}

object DRoute {
  def apply(r: Route) = new DRoute(r)
  def maybe(r: Route): DRoute = {
    r match {
      case r: DRoute => r
      case _ => new DRoute(r)
    }
  }
  def copyDocumentation(from: DRoute, to: Route): DRoute = new DRoute(to, from.selfDescribe)
}