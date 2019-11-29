package akka.http.documenteddsl

import DDirectives._
import akka.http.documenteddsl.documentation.RouteDocumentation
import akka.http.scaladsl.testkit.ScalatestRouteTest
import matchers.must.Matchers._
import org.scalatest.matchers
import org.scalatest.wordspec.AnyWordSpec

class MethodDDirectivesSpec extends AnyWordSpec with DDirectivesSpec with ScalatestRouteTest {

  private def check(m: MethodDDirective): Unit = m.toString must {
    "be applied to documentation" in {
      m.describe(RouteDocumentation()).method mustBe Some(m.toString)
    }
    "be counted during request handling" in {
      val route = m {complete("ok")}
      Get()     ~> route ~> check {handled must be (m == GET)}
      Post()    ~> route ~> check {handled must be (m == POST)}
      Delete()  ~> route ~> check {handled must be (m == DELETE)}
      Put()     ~> route ~> check {handled must be (m == PUT)}
      Head()    ~> route ~> check {handled must be (m == HEAD)}
      Options() ~> route ~> check {handled must be (m == OPTIONS)}
      Patch()   ~> route ~> check {handled must be (m == PATCH)}
    }
  }

  check(GET)
  check(POST)
  check(DELETE)
  check(PUT)
  check(HEAD)
  check(OPTIONS)
  check(PATCH)

}
