package akka.http.documenteddsl.documentation

import akka.http.documenteddsl.documentation.ParamDocumentation._
import akka.http.documenteddsl.documentation.OutDocumentation.Payload._
import akka.http.documenteddsl.documentation.OutDocumentation._
import play.api.libs.json._

trait DocumentationJson {
  import Documentation._

  implicit val nodeWrites: Writes[Node] = new Writes[Node] {
    override def writes(n: Node): JsValue = n match {
      case n: RouteNode => Json.obj("label" -> n.label, "uid" -> n.uid)
      case n: TopicNode => Json.obj("label" -> n.label, "children" -> n.children.map(writes))
    }
  }

  implicit val originWrites = new Writes[Origin]() {
    override def writes(o: Origin): JsValue = JsString(o.productPrefix.toLowerCase)
  }
  
  implicit def pathWrites = new Writes[PathDocumentation]() {
    override def writes(o: PathDocumentation): JsValue = JsString(o.render())
  }

  implicit val sessionWrites: Writes[SessionDocumentation]        = Json.writes[SessionDocumentation]
  implicit val statusWrites: Writes[Status]                       = Json.writes[Status]
  implicit val successWrites: Writes[Success]                     = Json.writes[Success]
  implicit val errorWrites: Writes[Failure]                       = Json.writes[Failure]
  implicit val outWrites: Writes[OutDocumentation]                = Json.writes[OutDocumentation]
  implicit val inWrites: Writes[InDocumentation]                  = Json.writes[InDocumentation]
  implicit val paramDocWrites: Writes[ParamDocumentation]         = Json.writes[ParamDocumentation]
  implicit val headerDocWrites: Writes[HeaderDocumentation]       = Json.writes[HeaderDocumentation]
  implicit val routeDocWrites: Writes[RouteDocumentation]         = Json.writes[RouteDocumentation]
  implicit val docWrites: Writes[Documentation]                   = Json.writes[Documentation]
}

object DocumentationJson extends DocumentationJson