package akka.http.documenteddsl

import java.time.LocalDate

import DDirectives._
import akka.http.documenteddsl.documentation.{InDocumentation, JsonSchema, RouteDocumentation}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.MustMatchers._
import org.scalatest.WordSpec
import play.api.libs.json.{Format, Json}

class MarshallingDDirectivesSpec extends WordSpec with DDirectivesSpec with ScalatestRouteTest {
  import MarshallingDDirectivesSpec._
  import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport._


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
  }

}

object MarshallingDDirectivesSpec {
  case class TestIn(id: String, name: Option[String], createdAt: LocalDate)
  implicit val testInFormat: Format[TestIn] = Json.format[TestIn]
}