import sbt.*

object PekkoPort {

  private val akka = """(?<![\w.])akka\.""".r

  def port(base: File, from: File, to: File): Seq[File] = {
    val ported = for {
      source <- (from ** "*.scala").get()
      relative <- source.relativeTo(from)
    } yield {
      val path = relative.getPath.replaceFirst("^akka/", "org/apache/pekko/")
      val origin = IO.relativize(base, source).getOrElse(source.getPath)
      val header = s"/* GENERATED FILE! Edit $origin instead. */ "
      val content = header + akka.replaceAllIn(IO.read(source), "org.apache.pekko.")
      val target = to / path
      if (!target.exists() || IO.read(target) != content) {
        IO.createDirectory(target.getParentFile)
        IO.write(target, content)
      }
      target
    }
    IO.delete((to ** "*.scala").get().filterNot(ported.toSet))
    ported
  }
}
