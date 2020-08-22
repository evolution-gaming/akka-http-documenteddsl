package akka.http.documenteddsl

import akka.http.scaladsl.server.directives.MethodDirectives
import akka.http.scaladsl.server.Directive0
import DDirectives.{DDirective0, Title}
import org.scalatest.wordspec.AnyWordSpec


class DConjunctionMagnetSpec extends AnyWordSpec with DDirectivesSpec {

  "DConjunctionMagnet" must {
    "be able to be made of Directive and DDirective conjunction" in {
      val ddirective: DDirective0 = Title("some")
      val directive:   Directive0 = MethodDirectives.get

      val _:          DDirective0 = ddirective & directive
    }
  }
}
