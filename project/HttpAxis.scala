import sbt.*

case class HttpAxis(value: String) extends VirtualAxis.WeakAxis {
  override def idSuffix: String = s"-$value"
  override def directorySuffix: String = value
}

object HttpAxis {
  val akka: HttpAxis = HttpAxis("akka")
  val pekko: HttpAxis = HttpAxis("pekko")
}
