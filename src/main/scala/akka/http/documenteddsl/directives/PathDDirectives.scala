package akka.http.documenteddsl.directives

import akka.http.documenteddsl.documentation._
import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.server.directives.PathDirectives
import akka.http.scaladsl.server.util.TupleOps.Join
import akka.http.scaladsl.server.{Directive, PathMatcher, PathMatcher1}
import org.coursera.autoschema.AutoSchema
import play.api.libs.json.{JsObject, Json}

import scala.language.implicitConversions
import scala.reflect.runtime.{universe => ru}
import scala.util.matching.Regex

trait PathDDirectives {

  case class PathPrefix[L](m: PathM[L]) extends DDirective[L] {
    def describe(w: RouteDocumentation)(implicit as: AutoSchema): RouteDocumentation = w.pathPrefix(m.render).parameters(m.parameters)
    def delegate: Directive[L] = PathDirectives.pathPrefix(m.asAkka)
    def /[R](s: PathM[R])(implicit join: Join[L, R]): PathPrefix[join.Out] = PathPrefix(m / s).asInstanceOf[PathPrefix[join.Out]]
  }

  case class PathSuffix[L](m: PathM[L]) extends DDirective[L] {
    def describe(w: RouteDocumentation)(implicit as: AutoSchema): RouteDocumentation = w.pathSuffix(m.render).parameters(m.parameters)
    def delegate: Directive[L] = PathDirectives.pathSuffix(m.asAkka)
    def /[R](s: PathM[R])(implicit join: Join[L, R]): PathSuffix[join.Out] = PathSuffix(m / s).asInstanceOf[PathSuffix[join.Out]]
  }

  case class Path[L](m: PathM[L]) extends DDirective[L] {
    def describe(w: RouteDocumentation)(implicit as: AutoSchema): RouteDocumentation = w.path(m.render).parameters(m.parameters)
    def delegate: Directive[L] = PathDirectives.path(m.asAkka)
    def /[R](s: PathM[R])(implicit join: Join[L, R]): Path[join.Out] = Path(m / s).asInstanceOf[Path[join.Out]]
  }

  sealed trait PathM[L] { self =>
    def render: String
    def parameters: List[ParamDocumentation]
    def asAkka: PathMatcher[L]
    def / : PathM[L] = this
    def /[R](s: PathM[R])(implicit join: Join[L, R]): PathM[join.Out] = new PathM[join.Out]() {
      def render: String = s"${self.render}/${s.render}"
      def parameters: List[ParamDocumentation] = self.parameters ++ s.parameters
      override def asAkka: PathMatcher[join.Out] = (self.asAkka / s.asAkka).asInstanceOf[PathMatcher[join.Out]]
    }
  }

  type PathM1[T] = PathM[Tuple1[T]]

  case class Literal(value: String) extends PathM[Unit] {
    def render: String = value
    def parameters = Nil
    def asAkka = PathMatcher(Uri.Path(value), ())
  }

  case class Segment[L : ru.TypeTag](akka: PathMatcher1[L], name: Option[String] = None)(schemaMod: JsObject => JsObject = identity)(implicit as: AutoSchema) extends PathM1[L] {
    private lazy val schema: JsObject = JsonSchema.resolveSchema[L]
    private def tpe: String = (schema \ "format").asOpt[String] getOrElse (schema \ "type").as[String]
    def render: String = "{" + tpe + "}"
    def parameter = ParamDocumentation(
      name = name getOrElse tpe,
      schema = schemaMod(schema),
      required = true,
      origin = ParamDocumentation.Origin.Path)
    def parameters = List(parameter)
    def asAkka: PathMatcher1[L] = akka

    def map[R : ru.TypeTag](f: L => R): Segment[R] = Segment(name = name, akka = akka map f)(schemaMod)
    def flatMap[R : ru.TypeTag](f: L => Option[R]): Segment[R] = Segment(name = name, akka = akka flatMap f)(schemaMod)
  }

  object Segment {
    def apply[L : ru.TypeTag](implicit st: PathSegmentType[L], as: AutoSchema): Segment[L] = Segment[L](st.fromString)()
    def apply[L : ru.TypeTag](name: String)(implicit st: PathSegmentType[L], as: AutoSchema): Segment[L] = Segment[L](st.fromString, Some(name))()
    def re(re: Regex)(implicit as: AutoSchema): Segment[String] = Segment[String](PathSegmentType.RegexSegment(re).fromString)(_ ++ Json.obj("pattern" -> re.regex, "format" -> "regexp"))
    def re(re: Regex, name: String)(implicit as: AutoSchema): Segment[String] = Segment[String](PathSegmentType.RegexSegment(re).fromString, Some(name))(_ ++ Json.obj("pattern" -> re.regex, "format" -> "regexp"))
  }

  implicit def stringAsLiteral(s: String): PathM[Unit] = Literal(s)

}

object PathDDirectives extends PathDDirectives