package akka.http.documenteddsl.directives

import akka.http.documenteddsl.documentation._
import akka.http.javadsl.server.AuthorizationFailedRejection
import akka.http.scaladsl.model.{StatusCodes, Uri}
import akka.http.scaladsl.server.Directives.{cookie, provide, reject}
import akka.http.scaladsl.server.directives.RouteDirectives.{redirect => akkaRedirect}
import akka.http.scaladsl.server.{Directive, Directive1}
import org.coursera.autoschema.AutoSchema

trait SessionDDirectives {
  import SessionDDirectives._

  case class Session[T, P](permissions: P*)(implicit
    sp: SessionProvider[T, P],
    permissionCodeExtractor: PermissionCodeExtractor[P] = (x: P) => x.toString) extends DDirective1[T] {
    def describe(w: RouteDocumentation)(implicit as: AutoSchema): RouteDocumentation = w.authorized(
      sessionType = sp.sessionType,
      redirectUri = None,
      permissions = permissions.toSet[P] map permissionCodeExtractor)

    def delegate: Directive1[T] = sp.obtain(permissions.toSet[P])

    def &>(uri: Uri)(implicit as: AutoSchema): DDirective1[T] = {
      val redir: Directive1[T] = akkaRedirect(uri, StatusCodes.Found)
      new DDirectiveDelegate(delegate | redir, doc => describe(doc).ifNoSessionRedirectTo(uri))
    }

    def flatten(implicit as: AutoSchema): DDirective0 = new DDirectiveDelegate(delegate flatMap {_ => Directive.Empty}, describe)
  }

  object Session {
    def apply[T, P : PermissionCodeExtractor](implicit sp: SessionProvider[T, P]): Session[T, P] = Session[T, P]()
  }

}

object SessionDDirectives extends SessionDDirectives {
  val genericSessionCookieName: String = "session-id"
  val genericSessionType: String = "generic"

  type PermissionCodeExtractor[P] = P => String

  trait SessionProvider[T, P] {
    def obtain(permissions: Set[P]): Directive1[T]
    def sessionType: String
  }

  class GenericSessionProvider[T, P](cookieName: String, backend: String => Set[P] => Option[T]) extends SessionProvider[T, P] {
    override def obtain(permissions: Set[P]): Directive1[T] = {
      cookie(cookieName) flatMap { cp =>
        val f = backend(cp.value)
        val r = f(permissions)
        r.fold[Directive1[T]](reject(AuthorizationFailedRejection.get))(provide)
      }
    }

    override def sessionType: String = genericSessionType
  }

  implicit def asGenericSessionProvider[T, P](f: String => Set[P] => Option[T]): SessionProvider[T, P] = {
    new GenericSessionProvider(genericSessionCookieName, f)
  }
}