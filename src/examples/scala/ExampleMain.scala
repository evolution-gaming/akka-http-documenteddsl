import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._

import akka.http.documenteddsl.documentation._
import akka.http.documenteddsl.documentation.DocumentationJson._
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport._
import akka.stream.ActorMaterializer

import scala.concurrent.Future

object ExampleMain extends App {
  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  val routes = {
    val apiRoutes = ExampleRoutes.route
    val documentation = apiRoutes.describe(Documentation())
    val documentationRoute = (get & path("doc")) {complete(documentation)}

    apiRoutes ~ documentationRoute
  }

  for {
    port  <- Http().bindAndHandle(routes, "localhost", 8080)
    _     <- Future {
      println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
      io.StdIn.readLine()
    }
    _     <- port.unbind()
  } { system.terminate() }

}