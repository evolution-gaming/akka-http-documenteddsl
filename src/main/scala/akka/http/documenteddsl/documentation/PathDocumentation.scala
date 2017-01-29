package akka.http.documenteddsl.documentation

sealed trait PathDocumentation {
  def withPrefix(path: String): PathDocumentation
  def withSuffix(path: String): PathDocumentation
  def withPath(path: String): PathDocumentation
  def render(): String
}

object PathDocumentation {
  case object Empty extends PathDocumentation {
    def withPrefix(prefix: String): PathDocumentation = NonEmpty(prefix = Some(prefix))
    def withPath(path: String): PathDocumentation = NonEmpty(path = Some(path))
    def withSuffix(suffix: String): PathDocumentation = NonEmpty(suffix = Some(suffix))
    def render(): String = ""
  }
  case class NonEmpty(
    prefix: Option[String] = None,
    path: Option[String] = None,
    suffix: Option[String] = None) extends PathDocumentation {

    def withPrefix(prefix: String): PathDocumentation = copy(prefix = Some(prefix))
    def withPath(path: String): PathDocumentation = copy(path = Some(path))
    def withSuffix(suffix: String): PathDocumentation = copy(suffix = Some(suffix))

    def render(): String = List(prefix, path, suffix).flatten mkString "/"
  }

}
