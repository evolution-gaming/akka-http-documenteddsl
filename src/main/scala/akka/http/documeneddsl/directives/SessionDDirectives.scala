package akka.http.documeneddsl.directives

import akka.http.documeneddsl.documentation._
import akka.http.javadsl.server.AuthorizationFailedRejection
import akka.http.scaladsl.model.{StatusCodes, Uri}
import akka.http.scaladsl.server.Directives.{cookie, provide, reject}
import akka.http.scaladsl.server.directives.RouteDirectives.{redirect => akkaRedirect}
import akka.http.scaladsl.server.{Directive, Directive1}
import org.coursera.autoschema.AutoSchema

import scala.language.implicitConversions

trait SessionDDirectives {
  import SessionDDirectives._

  case class Session[T](permissions: Permission*)(implicit sp: SessionProvider[T]) extends DDirective1[T] {
    def describe(w: RouteDocumentation)(implicit as: AutoSchema): RouteDocumentation = w.authorized(
      sessionType = sp.sessionType,
      redirectUri = None,
      permissions = permissions.toSet[Permission] map {_.code})

    def delegate: Directive1[T] = sp.obtain(permissions.toSet[Permission])

    def &>(uri: Uri)(implicit as: AutoSchema): DDirective1[T] = {
      val redir: Directive1[T] = akkaRedirect(uri, StatusCodes.Found)
      new DDirectiveDelegate(delegate | redir, doc => describe(doc).ifNoSessionRedirectTo(uri))
    }

    def flatten(implicit as: AutoSchema): DDirective0 = new DDirectiveDelegate(delegate flatMap {_ => Directive.Empty}, describe)
  }

  object Session {
    def apply[T : SessionProvider]: Session[T] = Session[T]()
  }

}

object SessionDDirectives extends SessionDDirectives {
  val genericSessionCookieName: String = "session-id"
  val genericSessionType: String = "generic"

  trait Permission { def code: String }

  trait SessionProvider[T] {
    def obtain(permissions: Set[Permission]): Directive1[T]
    def sessionType: String
  }

  class GenericSessionProvider[T](cookieName: String, backend: String => Set[Permission] => Option[T]) extends SessionProvider[T] {
    override def obtain(permissions: Set[Permission]): Directive1[T] = {
      cookie(cookieName) flatMap { cp =>
        val f = backend(cp.value)
        val r = f(permissions)
        r.fold[Directive1[T]](reject(AuthorizationFailedRejection.get))(provide)
      }
    }
    override def sessionType: String = genericSessionType
  }

  implicit def asGenericSessionProvider[T](f: String => Set[Permission] => Option[T]): SessionProvider[T] = {
    new GenericSessionProvider(genericSessionCookieName, f)
  }
}