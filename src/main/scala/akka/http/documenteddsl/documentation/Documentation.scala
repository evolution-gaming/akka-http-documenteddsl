package akka.http.documenteddsl.documentation

case class Documentation(routes: List[RouteDocumentation] = List.empty) {
  def apply(f: RouteDocumentation => RouteDocumentation): Documentation = {
    copy(routes = f(RouteDocumentation()) +: routes)
  }
}
