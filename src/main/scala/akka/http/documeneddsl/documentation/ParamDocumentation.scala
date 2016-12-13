package akka.http.documeneddsl.documentation

import play.api.data.validation.ValidationError
import play.api.libs.json.{JsError, JsObject, JsResult, JsSuccess}

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
      case _ => JsError(ValidationError(s"unknown: $code"))
    }
  }
}