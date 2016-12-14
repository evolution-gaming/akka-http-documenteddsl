package akka.http.documenteddsl

import akka.http.documenteddsl.documentation.DocumentedTypeMappings
import org.coursera.autoschema.AutoSchema
import org.scalatest.TestSuite

trait DDirectivesSpec { self: TestSuite =>

  implicit object autoSchema extends AutoSchema with DocumentedTypeMappings

}
