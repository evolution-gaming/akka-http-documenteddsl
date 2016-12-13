package akka.http.documeneddsl.documentation

final case class SessionDocumentation(
  `type`: String,
  redirectUri: Option[String] = None,
  permissions: Set[String] = Set.empty)