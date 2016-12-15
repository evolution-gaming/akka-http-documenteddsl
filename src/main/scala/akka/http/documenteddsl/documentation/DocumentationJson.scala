package akka.http.documenteddsl.documentation

import akka.http.documenteddsl.documentation.ParamDocumentation._
import akka.http.documenteddsl.documentation.OutDocumentation.Payload._
import akka.http.documenteddsl.documentation.OutDocumentation._
import play.api.libs.json._

trait DocumentationJson {

  implicit val originFormat = new Format[Origin]() {
    override def reads(json: JsValue): JsResult[Origin] = json.validate[String] flatMap Origin.validate
    override def writes(o: Origin): JsValue = JsString(o.productPrefix.toLowerCase)
  }

  implicit val sessionFormat: Format[SessionDocumentation]        = Json.format[SessionDocumentation]
  implicit val statusFormat: Format[Status]                       = Json.format[Status]
  implicit val successFormat: Format[Success]                     = Json.format[Success]
  implicit val errorFormat: Format[Failure]                       = Json.format[Failure]
  implicit val outFormat: Format[OutDocumentation]                = Json.format[OutDocumentation]
  implicit val inFormat: Format[InDocumentation]                  = Json.format[InDocumentation]
  implicit val paramDocFormat: Format[ParamDocumentation]         = Json.format[ParamDocumentation]
  implicit val routeDocFormat: Format[RouteDocumentation]         = Json.format[RouteDocumentation]
  implicit val docFormat: Format[Documentation]                   = Json.format[Documentation]
}

object DocumentationJson extends DocumentationJson