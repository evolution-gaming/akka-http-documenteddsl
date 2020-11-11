package akka.http.documenteddsl

import java.time.LocalDate

import DDirectives._
import akka.http.documenteddsl.documentation.{InDocumentation, JsonSchema, RouteDocumentation}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.MustMatchers._
import org.scalatest.WordSpec
import play.api.libs.json._

class MarshallingDDirectivesSpec extends WordSpec with DDirectivesSpec with ScalatestRouteTest {
  import MarshallingDDirectivesSpec._
  import akka.http.scaladsl.marshallers.playjson.PlayJsonSupport._

  "In" must {
    val now = LocalDate.now()

    "be applied to route documentation" in {
      In[TestIn].describe(RouteDocumentation()).in mustBe Some(InDocumentation(
        contentType = "application/json",
        schema = JsonSchema.resolveSchema[TestIn],
        example = None))
    }
    "be applied to route documentation (with example)" in {
      In(TestIn("in", Some("name"), now)).describe(RouteDocumentation()).in mustBe Some(InDocumentation(
        contentType = "application/json",
        schema = JsonSchema.resolveSchema[TestIn],
        example = Some(Json toJson TestIn("in", Some("name"), now))))
    }
    "be counted during request processing" in {
      val route = In[TestIn] apply {x => complete(x)}
      Post("/", TestIn("id", Some("name"), now)) ~> route ~> check {
        handled mustBe true
        responseAs[TestIn] mustBe TestIn("id", Some("name"), now)
      }
    }
    "be preprocessed" in {
      implicit val sanitize = new Preprocess[JsValue] {
        def transform(json: JsValue): JsValue = json match {
          case JsString(x)    => JsString(11.toString + x)
          case JsNumber(x)    => JsNumber(11 + x)
          case arr: JsArray   => JsArray(arr.value map transform)
          case obj: JsObject  => JsObject(obj.fields map {case (k, v) => (k, transform(v))})
          case x              => x
        }

        override def apply(x: JsValue): JsValue = transform(x)
      }

      val route = In[AnotherTestIn] apply {x => complete(x)}
      Post("/", AnotherTestIn("id", Some("name"), 16)) ~> route ~> check {
        handled mustBe true
        responseAs[AnotherTestIn] mustBe AnotherTestIn("11id", Some("11name"), 27)
      }
    }
  }

}

object MarshallingDDirectivesSpec {
  case class TestIn(id: String, name: Option[String], createdAt: LocalDate)
  case class AnotherTestIn(id: String, name: Option[String], num: Int)
  implicit val testInFormat: Format[TestIn] = Json.format[TestIn]
  implicit val anotherTestInFormat: Format[AnotherTestIn] = Json.format[AnotherTestIn]

}