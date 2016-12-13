package akka.http.documeneddsl.documentation

import java.util.UUID

import akka.http.documeneddsl.documentation.OutDocumentation._
import akka.http.scaladsl.model.{ContentTypes, StatusCode, Uri}
import org.coursera.autoschema.AutoSchema
import play.api.libs.json.JsValue

import scala.reflect.runtime.{universe => ru}

case class RouteDocumentation(
  uuid: UUID = UUID.randomUUID(),
  method: Option[String] = None,
  path: Option[String] = None,
  session: Option[SessionDocumentation] = None,
  parameters: Option[List[ParamDocumentation]] = None,
  in: Option[InDocumentation] = None,
  out: Option[OutDocumentation] = None,
  title: Option[String] = None,
  description: Option[String] = None,
  category: Option[List[String]] = None) {

  def category(category: List[String]): RouteDocumentation = copy(category = Some(category))

  def title(m: String): RouteDocumentation = copy(title = Some(m))

  def description(m: String): RouteDocumentation = copy(description = Some(m))

  def method(m: String): RouteDocumentation = copy(method = Some(m))

  def path(p: String): RouteDocumentation = copy(path = Some(p))

  def authorized(
    sessionType: String,
    redirectUri: Option[String] = None,
    permissions: Set[String] = Set.empty): RouteDocumentation = {

    copy(session = Some(SessionDocumentation(
      sessionType,
      redirectUri,
      permissions)))
  }

  def ifNoSessionRedirectTo(uri: Uri): RouteDocumentation = copy(
    session = session map { session => session.copy(redirectUri = Some(uri.toString)) })

  def in[T : ru.TypeTag](example: Option[JsValue] = None)(implicit as: AutoSchema): RouteDocumentation = copy(
    in = Some(InDocumentation(
      contentType = ContentTypes.`application/json`.toString(),
      schema      = JsonSchema.resolveSchema[T],
      example     = example)))

  def outSuccess[T : ru.TypeTag](status: StatusCode, example: Option[JsValue] = None)(implicit as: AutoSchema): RouteDocumentation = {
    val response  = Payload.Success(
      status      = Status(status),
      contentType = ContentTypes.`application/json`.toString(),
      schema      = JsonSchema.resolveSchema[T],
      example     = example)
    val updated   = out getOrElse OutDocumentation()

    copy(out = Some(updated :+ response))
  }

  def outError(status: StatusCode, contentType: Option[String], description: Option[String]): RouteDocumentation = {
    val response  = Payload.Failure(
      status      = Status(status),
      contentType = contentType,
      description = description)
    val updated   = out getOrElse OutDocumentation()

    copy(out = Some(updated :+ response))
  }

  def parameter[T : ru.TypeTag](name: String, required: Boolean, origin: ParamDocumentation.Origin)(implicit as: AutoSchema): RouteDocumentation = {
    val param = ParamDocumentation(name, JsonSchema.resolveSchema[T], required = required, origin = origin)
    parameters(param :: Nil)
  }

  def parameters(params: List[ParamDocumentation])(implicit as: AutoSchema): RouteDocumentation = {
    val joined = (parameters, params) match {
      case (None   , Nil)  => None
      case (None   , y)    => Some(y)
      case (Some(x), Nil)  => Some(x)
      case (Some(x), y)    => Some(x ++ y)
    }

    copy(parameters = joined)
  }

}
