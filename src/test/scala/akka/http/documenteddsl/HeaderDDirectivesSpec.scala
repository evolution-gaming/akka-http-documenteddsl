package akka.http.documenteddsl

import akka.http.documenteddsl.DDirectives._
import akka.http.documenteddsl.documentation._
import akka.http.scaladsl.model.ContentTypes
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.MustMatchers._
import org.scalatest.WordSpec

class HeaderDDirectivesSpec extends WordSpec with DDirectivesSpec with ScalatestRouteTest {

  "Header" must {
    "be applied to route documentation" in {
      Header("-x-custom-header").describe(RouteDocumentation()).headers mustBe Some(List(HeaderDocumentation(
        name = "-x-custom-header",
        required = true,
        constraints = None)))
      Header("-x-custom-header", "A", "B").describe(RouteDocumentation()).headers mustBe Some(List(HeaderDocumentation(
        name = "-x-custom-header",
        required = true,
        constraints = Some(Set("A", "B")))))

      Header(Symbol("CustomHeader")).describe(RouteDocumentation()).headers mustBe Some(List(HeaderDocumentation(
        name = "CustomHeader",
        required = true,
        constraints = None)))

      Header(Symbol("CustomHeader"), "A", "B").describe(RouteDocumentation()).headers mustBe Some(List(HeaderDocumentation(
        name = "CustomHeader",
        required = true,
        constraints = Some(Set("A", "B")))))

      Header[`Content-Type`].describe(RouteDocumentation()).headers mustBe Some(List(HeaderDocumentation(
        name = "Content-Type",
        required = true,
        constraints = None)))
    }
    "be counted during request processing (positive)" in {
      import ContentTypes._

      val route0 = Header[`Content-Type`] apply {x => complete(s"${x.value()}")}
      Get("/") ~> addHeader(`Content-Type`(`application/json`)) ~> route0 ~> check {
        handled mustBe true
        responseAs[String] mustBe "application/json"}

      val route1 = Header("-x-custom", "foo", "bar") apply {x => complete(s"$x")}
      Get("/") ~> addHeader("-x-custom", "foo") ~> route1 ~> check {
        handled mustBe true
        responseAs[String] mustBe "foo"}
    }
    "be counted during request processing (negative)" in {
      val route = Header[`Content-Type`] apply {x => complete(s"$x")}
      Get("/") ~> route ~> check {
        handled mustBe false
      }

      val route1 = Header("-x-custom", "foo", "bar") apply {x => complete(s"$x")}
      Get("/") ~> addHeader("-x-custom", "baz") ~> route1 ~> check {
        handled mustBe false
      }
    }
  }


  "OptHeader" must {
    "be applied to route documentation" in {
      OptHeader("-x-custom-header").describe(RouteDocumentation()).headers mustBe Some(List(HeaderDocumentation(
        name = "-x-custom-header",
        required = false,
        constraints = None)))
      OptHeader("-x-custom-header", "A", "B").describe(RouteDocumentation()).headers mustBe Some(List(HeaderDocumentation(
        name = "-x-custom-header",
        required = false,
        constraints = Some(Set("A", "B")))))

      OptHeader(Symbol("CustomHeader")).describe(RouteDocumentation()).headers mustBe Some(List(HeaderDocumentation(
        name = "CustomHeader",
        required = false,
        constraints = None)))

      OptHeader(Symbol("CustomHeader"), "A", "B").describe(RouteDocumentation()).headers mustBe Some(List(HeaderDocumentation(
        name = "CustomHeader",
        required = false,
        constraints = Some(Set("A", "B")))))

      OptHeader(`Content-Type`).describe(RouteDocumentation()).headers mustBe Some(List(HeaderDocumentation(
        name = "Content-Type",
        required = false,
        constraints = None)))

      OptHeader(`Content-Type`, "A", "B").describe(RouteDocumentation()).headers mustBe Some(List(HeaderDocumentation(
        name = "Content-Type",
        required = false,
        constraints = Some(Set("A", "B")))))
    }
    "be counted during request processing (positive)" in {
      import ContentTypes._

      val route0 = OptHeader(`Content-Type`) apply {x => complete(s"$x")}
      Get("/") ~> addHeader(`Content-Type`(`application/json`)) ~> route0 ~> check {
        handled mustBe true
        responseAs[String] mustBe "Some(application/json)"}

      val route1 = OptHeader("-x-custom", "foo", "bar") apply {x => complete(s"$x")}
      Get("/") ~> addHeader("-x-custom", "foo") ~> route1 ~> check {
        handled mustBe true
        responseAs[String] mustBe "Some(foo)"}
    }
    "be counted during request processing (negative)" in {
      val route = OptHeader(`Content-Type`) apply {x => complete(s"$x")}
      Get("/") ~> route ~> check {
        handled mustBe true
        responseAs[String] mustBe "None"
      }

      val route1 = OptHeader("-x-custom", "foo", "bar") apply {x => complete(s"$x")}
      Get("/") ~> addHeader("-x-custom", "baz") ~> route1 ~> check {
        handled mustBe false
      }
    }
  }

}
