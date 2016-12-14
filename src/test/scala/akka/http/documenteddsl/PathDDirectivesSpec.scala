package akka.http.documenteddsl

import java.time.LocalDate

import akka.http.documenteddsl.directives.DDirectives._
import akka.http.documenteddsl.documentation._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.MustMatchers._
import org.scalatest.WordSpec
import play.api.libs.json.Json

class PathDDirectivesSpec extends WordSpec with DDirectivesSpec with ScalatestRouteTest {

  "Path" must {
    "be applied for 1 segment" in {
      Path("xxx").describe(RouteDocumentation()).path mustBe Some("xxx")
    }
    "be applied for N segments" in {
      Path("a" / "b" / "c").describe(RouteDocumentation()).path mustBe Some("a/b/c")
    }
    "be applied for N segments with string variables" in {
      val doc = Path("a" / Segment[String]("user id") / "c").describe(RouteDocumentation())
      doc.path mustBe Some("a/{string}/c")
      doc.parameters mustBe Some(List(ParamDocumentation("user id", Json.obj("type" -> "string"), required = true, ParamDocumentation.Origin.Path)))
    }
    "be applied for N segments with number variables" in {
      val doc = Path("a" / Segment[Int]("user id") / "c").describe(RouteDocumentation())
      doc.path mustBe Some("a/{number}/c")
      doc.parameters mustBe Some(List(ParamDocumentation("user id", Json.obj("type" -> "number", "format" -> "number"), required = true, ParamDocumentation.Origin.Path)))
    }
    "be applied for N segments with date variables" in {
      val doc = Path("a" / Segment[LocalDate]("creation date") / "c").describe(RouteDocumentation())
      doc.path mustBe Some("a/{date}/c")
      doc.parameters mustBe Some(List(ParamDocumentation("creation date", Json.obj("type" -> "string", "format" -> "date", "pattern" -> "^[0-9]{4}-[0-9]{2}-[0-9]{2}$"), required = true, ParamDocumentation.Origin.Path)))
    }
    "be applied for N segments with RegExp variables" in {
      val doc = Path("a" / Segment.re("^[a-z0-9_\\.-]+@[\\da-z\\.-]+\\.[a-z\\.]{2,6}$".r, "email") / "c").describe(RouteDocumentation())
      doc.path mustBe Some("a/{string}/c")
      doc.parameters mustBe Some(List(ParamDocumentation("email", Json.obj("type" -> "string", "format" -> "regexp", "pattern" -> "^[a-z0-9_\\.-]+@[\\da-z\\.-]+\\.[a-z\\.]{2,6}$"), required = true, ParamDocumentation.Origin.Path)))
    }
    "be translated to akka (1 segment)" in {
      val route = Path("xxx") apply {complete("")}
      Get("/xxx") ~> route ~> check { handled mustBe true }
      Get("/yyy") ~> route ~> check { handled mustBe false }
    }
    "be translated to akka (2 segments)" in {
      val route = Path("a" / "b" / "c") apply {complete("")}
      Get("/a/b/c") ~> route ~> check {handled mustBe true}
      Get("/x/x") ~> route ~> check {handled mustBe false}
    }
    "be translated to akka (N segments with string variables)" in {
      val route = Path("a" / Segment[String]("user id") / "c") apply {x => complete(x)}
      Get("/a/b/c") ~> route ~> check {handled mustBe true; responseAs[String] mustBe "b"}
      Get("/x/x") ~> route ~> check {handled mustBe false}
    }
    "be translated to akka (N segments with int variables)" in {
      val route = Path("a" / Segment[Int]("user id") / "c") apply {x => complete(s"$x")}
      Get("/a/77/c") ~> route ~> check {handled mustBe true; responseAs[String] mustBe "77"}
      Get("/a/b/c") ~> route ~> check {handled mustBe false}
      Get("/x/x") ~> route ~> check {handled mustBe false}
    }
    "be translated to akka (N segments with date variables)" in {
      val route = Path("a" / Segment[LocalDate]("creation date") / "c") apply {case (x: LocalDate) => complete(s"$x")}
      Get("/a/2000-01-01/c") ~> route ~> check {handled mustBe true; responseAs[String] mustBe "2000-01-01"}
      Get("/a/foo-bar/c") ~> route ~> check {handled mustBe false}
      Get("/x/x") ~> route ~> check {handled mustBe false}
    }
    "be translated to akka (N segments with RegExp variables)" in {
      val route = Path("a" / Segment.re("^[a-z0-9_\\.-]+@[\\da-z\\.-]+\\.[a-z\\.]{2,6}$".r) / "c") apply {x => complete(s"$x")}
      Get("/a/foo@bar.baz/c") ~> route ~> check {handled mustBe true; responseAs[String] mustBe "foo@bar.baz"}
      Get("/a/foo-bar/c") ~> route ~> check {handled mustBe false}
      Get("/x/x") ~> route ~> check {handled mustBe false}
    }
  }
}
