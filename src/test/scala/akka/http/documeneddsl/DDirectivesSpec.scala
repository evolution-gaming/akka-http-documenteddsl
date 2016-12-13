package akka.http.documeneddsl

import akka.http.documeneddsl.documentation.DocumentedTypeMappings
import org.coursera.autoschema.AutoSchema
import org.scalatest.TestSuite

trait DDirectivesSpec { self: TestSuite =>

  implicit object autoSchema extends AutoSchema with DocumentedTypeMappings

}
