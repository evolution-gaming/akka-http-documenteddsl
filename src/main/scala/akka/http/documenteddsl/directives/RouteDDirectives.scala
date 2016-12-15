package akka.http.documenteddsl.directives

import akka.http.documenteddsl._
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes.Redirection
import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.server.Rejection
import akka.http.scaladsl.server.directives.RouteDirectives

trait RouteDDirectives {
  def reject: DRoute = RouteDDirectives._reject
  def reject(rejections: Rejection*): DRoute = DRoute(_.reject(rejections: _*))
  def complete(m: => ToResponseMarshallable): DRoute = DRoute(_.complete(m))
  def failWith(error: Throwable): DRoute = DRoute(_.fail(error))
  def redirect(uri: Uri, redirectionType: Redirection): DRoute = DRoute(_.redirect(uri, redirectionType))
}

object RouteDDirectives extends RouteDDirectives {
  private val _reject = DRoute.maybe(RouteDirectives.reject)
}