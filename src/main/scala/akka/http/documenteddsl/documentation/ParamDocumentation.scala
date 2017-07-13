package akka.http.documenteddsl.documentation

import play.api.libs.json._

case class ParamDocumentation(name: String, schema: JsObject, required: Boolean, origin: ParamDocumentation.Origin)

object ParamDocumentation {
  sealed trait Origin extends Product
  object Origin {
    case object Form extends Origin
    case object Query extends Origin
    case object Path extends Origin
    def validate(code: String): JsResult[Origin] = code.toLowerCase match {
      case "form" => JsSuccess(Form)
      case "query" => JsSuccess(Query)
      case "path" => JsSuccess(Path)
      case _ => JsError(JsonValidationError(s"unknown: $code"))
    }
  }
}