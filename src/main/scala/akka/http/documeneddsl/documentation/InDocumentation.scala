package akka.http.documeneddsl.documentation

import play.api.libs.json.{JsObject, JsValue}

case class InDocumentation(contentType: String, schema: JsObject, example: Option[JsValue])
