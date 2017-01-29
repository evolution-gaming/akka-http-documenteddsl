import java.util.UUID

import akka.http.documenteddsl.DDirectives._
import akka.http.documenteddsl.documentation.DocumentedTypeMappings
import akka.http.scaladsl.model.{ContentTypes, StatusCodes}
import akka.http.scaladsl.marshallers.playjson.PlayJsonSupport._
import org.coursera.autoschema.AutoSchema
import play.api.libs.json.Json
import ExampleResource._
import ExampleResourceJson._

object ExampleRoutes {
  implicit object autoSchema extends AutoSchema with DocumentedTypeMappings

  private val FindAll = Category("Api", "Resource") & Title("Find All") & Description("Returns all resource entries") &
                        GET &
                        Out[Set[ExampleResource]]
  
  private val Find    = Category("Api", "Resource") & Title("Find") & Description("Returns specified resource entrie") &
                        Path(Segment[String]) & GET &
                        Out[ExampleResource] & Out(StatusCodes.NotFound, "Resource not found")
  
  private val Create  = Category("Api", "Resource") & Title("Create") & Description("Creates a new resource entry") &
                        POST &
                        In(CreateExample) & Out[ExampleResource]
  
  private val Update  = Category("Api", "Resource") & Title("Update") & Description("Updates specified resource entry") &
                        Path(Segment[String]) & PUT &
                        In(UpdateExample) & Out[ExampleResource] & Out(StatusCodes.NotFound, "Resource not found")
  
  private val Delete  = Category("Api", "Resource") & Title("Delete") & Description("Deletes specified resource entry") &
                        Path(Segment[String]) & DELETE &
                        Out[ExampleResource] & Out(StatusCodes.NotFound, "Resource not found")

  lazy val route: DRoute = PathPrefix("resources") {
    FindAll {complete(collection)} |~|
    Find    {find} |~|
    Create  {create} |~|
    Update  {update} |~|
    Delete  {delete}
  }

  import akka.http.scaladsl.server.Route
  import akka.http.scaladsl.server.Directives._

  private def find(id: String): Route = rejectEmptyResponse {complete(collection get id)}
  private def create(payload: CreateResource): Route = {
    val resource = new ExampleResource(
      id = UUID.randomUUID().toString,
      name = payload.name,
      description = payload.description)
    collection += resource.id -> resource
    complete(StatusCodes.Created -> resource)
  }
  private def update(id: String, payload: UpdateResource): Route = rejectEmptyResponse {
    val resource = (collection get id) map { resource =>
      val _name = payload.name getOrElse resource.name
      val _description = payload.description getOrElse resource.description
      val updated = resource.copy(
        name = _name,
        description = _description)
      collection += id -> updated
      updated
    }
    complete(resource)
  }
  private def delete(id: String): Route = {
    collection get id match {
      case Some(resource) => complete(resource)
      case None           => complete(StatusCodes.NotFound)
    }
  }

}
