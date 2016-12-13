package akka.http.documeneddsl

import akka.http.documeneddsl.directives.DDirectives._
import akka.http.documeneddsl.documentation.{JsonSchema, ParamDocumentation, RouteDocumentation}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.MustMatchers._
import org.scalatest.WordSpec

class ParameterDDirectivesSpec extends WordSpec with DDirectivesSpec with ScalatestRouteTest {

  "Param" must {
    "be applied to route documentation" in {
      Param[String]("xxx").describe(RouteDocumentation()).parameters mustBe Some(List(ParamDocumentation(
        name = "xxx",
        schema = JsonSchema.string,
        required = true,
        origin = ParamDocumentation.Origin.Query)))
    }
    "be counted during request processing" in {
      val route = Param[String]("xxx") apply {x => complete(s"$x")}
      Get("/?xxx=zzz") ~> route ~> check {handled mustBe true; responseAs[String] mustBe "zzz"}
    }
  }

  "OptParam" must {
    "be applied to route documentation" in {
      OptParam[String]("xxx").describe(RouteDocumentation()).parameters mustBe Some(List(ParamDocumentation(
        name = "xxx",
        schema = JsonSchema.string,
        required = false,
        origin = ParamDocumentation.Origin.Query)))
    }
    "be counted during request processing" in {
      val route = OptParam[String]("xxx") apply {x => complete(s"$x")}
      Get("/?xxx=zzz") ~> route ~> check {handled mustBe true; responseAs[String] mustBe "Some(zzz)"}
      Get("/") ~> route ~> check {handled mustBe true; responseAs[String] mustBe "None"}
    }
  }

}
