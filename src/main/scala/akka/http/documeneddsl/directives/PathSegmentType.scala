package akka.http.documeneddsl.directives

import java.time.LocalDate
import java.util.UUID

import akka.http.documeneddsl.documentation.DocumentedTypeMappings
import akka.http.scaladsl.server.{PathMatcher, PathMatcher1, PathMatchers}

import scala.util.Try
import scala.util.matching.Regex

sealed trait PathSegmentType[T] {
  def fromString: PathMatcher1[T]
}

object PathSegmentType {

  implicit object StringSegment extends PathSegmentType[String] {
    override def fromString: PathMatcher1[String] = PathMatchers.Segment
  }

  implicit object IntSegment extends PathSegmentType[Int] {
    override def fromString: PathMatcher1[Int] = PathMatchers.IntNumber
  }

  implicit object LongSegment extends PathSegmentType[Long] {
    override def fromString: PathMatcher1[Long] = PathMatchers.LongNumber
  }

  implicit object DoubleSegment extends PathSegmentType[Double] {
    override def fromString: PathMatcher1[Double] = PathMatchers.DoubleNumber
  }

  implicit object JavaUUIDSegment extends PathSegmentType[UUID] {
    override def fromString: PathMatcher1[UUID] = PathMatchers.JavaUUID
  }

  implicit object BooleanSegment extends PathSegmentType[Boolean] {
    private val values = Map(
      "true"  -> true,
      "false" -> false,
      "TRUE"  -> true,
      "FALSE" -> false,
      "yes"   -> true,
      "no"    -> false,
      "YES"   -> true,
      "NO"    -> false,
      "1"     -> true,
      "0"     -> false
    )
    override def fromString: PathMatcher1[Boolean] = PathMatchers.Segment flatMap values.get
  }

  implicit object LocalDateSegment extends PathSegmentType[LocalDate] {
    override def fromString: PathMatcher1[LocalDate] = PathMatcher(DocumentedTypeMappings.localDatePattern.r) flatMap { str =>
      Try(LocalDate.parse(str)).toOption
    }
  }

  case class RegexSegment(re: Regex) extends PathSegmentType[String] {
    override def fromString: PathMatcher1[String] = re
  }

}
