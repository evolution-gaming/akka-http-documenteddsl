package akka.http.documenteddsl.documentation

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.documenteddsl.documentation.DocumentationJson._
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport._

object DocumentationRoutes {
  def apply(documentation: Documentation): Route = {
    (options & pathEnd)   {complete(documentation.toc)} ~
    (get & pathEnd)       {complete(documentation)} ~
    (get & path(Segment)) {uid => rejectEmptyResponse {complete(documentation.route(uid))}}
  }
}
