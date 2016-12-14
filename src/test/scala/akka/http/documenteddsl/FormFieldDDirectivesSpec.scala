package akka.http.documenteddsl

import akka.http.documenteddsl.directives.DDirectives._
import akka.http.documenteddsl.documentation.{JsonSchema, ParamDocumentation, RouteDocumentation}
import akka.http.scaladsl.model.FormData
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.MustMatchers._
import org.scalatest.WordSpec

class FormFieldDDirectivesSpec extends WordSpec with DDirectivesSpec with ScalatestRouteTest {

  "FormField" must {
    "be applied to route documentation" in {
      FormField[String]("xxx").describe(RouteDocumentation()).parameters mustBe Some(List(ParamDocumentation(
        name = "xxx",
        schema = JsonSchema.string,
        required = true,
        origin = ParamDocumentation.Origin.Form)))
    }
    "be counted during request processing" in {
      val route = FormField[String]("xxx") apply {x => complete(s"$x")}
      val formData = FormData("xxx" -> "zzz")
      Post("/", formData) ~> route ~> check {handled mustBe true; responseAs[String] mustBe "zzz"}
    }
  }

  "OptFormField" must {
    "be applied to route documentation" in {
      OptFormField[String]("xxx").describe(RouteDocumentation()).parameters mustBe Some(List(ParamDocumentation(
        name = "xxx",
        schema = JsonSchema.string,
        required = false,
        origin = ParamDocumentation.Origin.Form)))
    }
    "be counted during request processing" in {
      val route = OptFormField[String]("xxx") apply {x => complete(s"$x")}
      Post("/", FormData("xxx" -> "zzz")) ~> route ~> check {handled mustBe true; responseAs[String] mustBe "Some(zzz)"}
      Post("/", FormData()) ~> route ~> check {handled mustBe true; responseAs[String] mustBe "None"}
    }
  }

}
