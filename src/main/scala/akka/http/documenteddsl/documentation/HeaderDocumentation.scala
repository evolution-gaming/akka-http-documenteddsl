package akka.http.documenteddsl.documentation

case class HeaderDocumentation(name: String, required: Boolean, constraints: Option[Set[String]])

