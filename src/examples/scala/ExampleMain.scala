import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._

import akka.http.documenteddsl.documentation._
import akka.stream.ActorMaterializer

import scala.concurrent.Future

object ExampleMain extends App {
  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  val routes = {
    val apiRoutes = ExampleRoutes.route
    val documentation = apiRoutes.describe(Documentation())
    val documentationRoute = pathPrefix("api.json") {
      DocumentationRoutes(documentation)
    }

    apiRoutes ~ documentationRoute
  }

  for {
    port  <- Http().bindAndHandle(routes, "localhost", 8080)
    _     <- Future {
      println(
        s"""Example App is available at http://localhost:8080
           |
           |You can try ExampleResource at
           |  GET     http://localhost:8080/resources
           |  GET     http://localhost:8080/resources/x
           |  POST    http://localhost:8080/resources   {"name": "new resource"}
           |  PUT     http://localhost:8080/resources/x {"name": "updated name"}
           |  DELETE  http://localhost:8080/resources/x
           |
           |Api Documentation (Json) is available at
           |  OPTIONS http://localhost:8080/api.json   // api toc
           |  GET     http://localhost:8080/api.json   // full api
           |  GET     http://localhost:8080/api.json/x // specified api route
           |
           |Press RETURN to stop...
         """.stripMargin)
      io.StdIn.readLine()
    }
    _     <- port.unbind()
  } { system.terminate() }

}