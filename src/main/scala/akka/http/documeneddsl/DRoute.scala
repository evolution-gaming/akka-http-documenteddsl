package akka.http.documeneddsl

import akka.http.documeneddsl.documentation.Documentation
import akka.http.scaladsl.server.{RequestContext, _}

import scala.concurrent.Future

class DRoute(
  underlying: Route,
  writer: Documentation => Documentation = identity) extends Route {

  def describe(doc: Documentation = Documentation()): Documentation = writer apply doc
  override def apply(ctx: RequestContext): Future[RouteResult] = underlying apply ctx
  override def toString = s"DocumentedRoute()"
}

object DRoute {
  def apply(r: Route) = new DRoute(r)
  def maybe(r: Route): DRoute = {
    r match {
      case r: DRoute => r
      case _ => new DRoute(r)
    }
  }
  def copyDocumentation(from: DRoute, to: Route): DRoute = new DRoute(to, from.describe)
}