package akka.http.documenteddsl.documentation

import play.api.libs.json.{JsObject, JsValue}

case class InDocumentation(contentType: String, schema: JsObject, example: Option[JsValue])
