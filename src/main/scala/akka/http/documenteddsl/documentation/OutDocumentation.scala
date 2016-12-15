package akka.http.documenteddsl.documentation

import akka.http.documenteddsl.documentation.OutDocumentation.Payload._
import akka.http.documenteddsl.documentation.OutDocumentation._
import akka.http.scaladsl.model.StatusCode
import play.api.libs.json.{JsObject, JsValue}

case class OutDocumentation(
  success: List[Success] = Nil,
  failure: List[Failure] = Nil) {
  def :+(r: Payload): OutDocumentation = r match {
    case r: Success => copy(success = success :+ r)
    case r: Failure => copy(failure = failure :+ r)
  }
}

object OutDocumentation {
  case class Status(code: Int, detail: String)
  object Status {
    def apply(statusCode: StatusCode): Status = Status(
      statusCode.intValue,
      statusCode.reason)
  }

  sealed trait Payload
  object Payload {
    case class Success(status: Status, contentType: String, schema: JsObject, example: Option[JsValue]) extends Payload
    case class Failure(status: Status, contentType: Option[String], description: Option[String]) extends Payload
  }
}

