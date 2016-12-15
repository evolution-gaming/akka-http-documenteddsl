package akka.http.documenteddsl.documentation

import play.api.libs.json._

case class Documentation(routes: List[RouteDocumentation] = List.empty) {
  def apply(f: RouteDocumentation => RouteDocumentation): Documentation = {
    copy(routes = f(RouteDocumentation()) +: routes)
  }

  lazy val toc: JsValue = Documentation.computeToc(routes)

  private lazy val routeIndex = routes.map(r => r.uid -> r).toMap
  def route(uid: String): Option[RouteDocumentation] = routeIndex get uid

}

object Documentation {
  private case class RoutePointer(category: List[String], uid: String, title: String) {
    def add(json: JsObject, name: String)(f: JsObject => JsObject): JsObject = {
      val sub = (json \ name).asOpt[JsObject] getOrElse Json.obj()
      json ++ Json.obj(name -> f(sub))
    }
    def apply(toc: JsObject): JsObject = category match {
      case head :: Nil  => add(toc, head) { sub =>
        add(sub, "uid")(_ ++ Json.obj(uid -> title))
      }
      case head :: tail => add(toc, head)(sub => RoutePointer(tail, uid, title).apply(sub))
      case Nil          => toc
    }
  }
  private object RoutePointer {
    def apply(route: RouteDocumentation): RoutePointer = RoutePointer(
      category  = route.category getOrElse List.empty,
      uid       = route.uid,
      title     = route.title getOrElse "Untitled")
  }
  private def computeToc(routes: List[RouteDocumentation]): JsObject = routes.map(RoutePointer.apply).foldRight(Json.obj())(_ apply _)
}