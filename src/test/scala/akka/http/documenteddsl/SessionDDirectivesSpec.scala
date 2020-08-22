package akka.http.documenteddsl

import DDirectives._
import akka.http.documenteddsl.documentation._
import akka.http.javadsl.server.AuthorizationFailedRejection
import akka.http.scaladsl.model.headers.Cookie
import akka.http.scaladsl.server.Directive1
import akka.http.scaladsl.testkit.ScalatestRouteTest
import matchers.must.Matchers._
import org.scalatest.matchers
import org.scalatest.wordspec.AnyWordSpec

class SessionDDirectivesSpec extends AnyWordSpec with DDirectivesSpec with ScalatestRouteTest {
  import SessionDDirectivesSpec._

  "Session" must {
    "be applied to route documentation" in new CustomScope {
      Session[UserSession, Perm].describe(RouteDocumentation()).session mustBe Some(SessionDocumentation(
        `type` = "user",
        redirectUri = None,
        permissions = Set.empty))
    }
    "be applied to route documentation (with permissions)" in new CustomScope {
      Session[UserSession, Perm](Read, Write).describe(RouteDocumentation()).session mustBe Some(SessionDocumentation(
        `type` = "user",
        redirectUri = None,
        permissions = Set("read", "write")))
    }
    "be applied to route documentation (with redirection)" in new CustomScope {
      (Session[UserSession, Perm] &> "/admin").describe(RouteDocumentation()).session mustBe Some(SessionDocumentation(
        `type` = "user",
        redirectUri = Some("/admin"),
        permissions = Set.empty))
    }

    "be handled. no cookie - no session" in new NoCookie with CustomScope
    "be handled. wrong cookie - no session" in new WrongCookie with CustomScope
    "be handled. correct cookie but lack of permissions - no session" in new CorrectCookieLackOfPerms with CustomScope
    "be handled. correct cookie - correct session" in new CorrectCookie with CustomScope
    "be handled. correct cookie and proper permissions - correct session" in new CorrectCookieProperPerms with CustomScope

    "be handled (generic impl). no cookie - no session" in new NoCookie with GenericScope
    "be handled (generic impl). wrong cookie - no session" in new WrongCookie with GenericScope
    "be handled (generic impl). correct cookie but lack of permissions - no session" in new CorrectCookieLackOfPerms with GenericScope
    "be handled (generic impl). correct cookie - correct session" in new CorrectCookie with GenericScope
    "be handled (generic impl). correct cookie and proper permissions - correct session" in new CorrectCookieProperPerms with GenericScope

    trait NoCookie { this: Scope =>
      val route = Session[UserSession, Perm] apply {session => complete(session.id)}
      Get("/") ~> route ~> check {
        handled mustBe false
        rejection mustNot be (null)
      }
    }

    trait WrongCookie { this: Scope =>
      val route = Session[UserSession, Perm] apply {session => complete(session.id)}
      Get("/") ~> addHeader(Cookie("session-id", "wrong")) ~> route ~> check {
        handled mustBe false
        rejection mustNot be (null)
      }
    }

    trait CorrectCookie { this: Scope =>
      val route = Session[UserSession, Perm] apply {session => complete(s"${session.id}, ${session.userId}")}
      Get("/") ~> addHeader(Cookie("session-id", "my-session-id")) ~> route ~> check {
        handled mustBe true
        responseAs[String] mustBe "my-session-id, user-id"
      }
    }

    trait CorrectCookieLackOfPerms { this: Scope =>
      val route = Session[UserSession, Perm](Write) apply {session => complete(s"${session.id}, ${session.userId}")}
      Get("/") ~> addHeader(Cookie("session-id", "my-session-id")) ~> route ~> check {
        handled mustBe false
        rejection mustBe AuthorizationFailedRejection.get
      }
    }

    trait CorrectCookieProperPerms { this: Scope =>
      val route = Session[UserSession, Perm](Read, Write) apply {session => complete(s"${session.id}, ${session.userId}")}
      Get("/") ~> addHeader(Cookie("session-id", "my-session-id")) ~> route ~> check {
        handled mustBe true
        responseAs[String] mustBe "my-session-id, user-id"
      }
    }
  }

}

object SessionDDirectivesSpec {
  import akka.http.documenteddsl.directives.SessionDDirectives._

  sealed trait Perm extends Product
  case object Read extends Perm
  case object Write extends Perm

  implicit val permCodeExtractor: PermissionCodeExtractor[Perm] = _.productPrefix.toLowerCase

  case class Role(permissions: Set[Perm])
  case class UserSession(id: String, userId: String, role: Role)

  // pseudo session store
  val sessionStore: Map[String, UserSession] = Map(
    "my-session-id" -> UserSession("my-session-id", "user-id", Role(Set(Read)))
  )

  sealed trait Scope {
    implicit def sessionProvider: SessionProvider[UserSession, Perm]
  }

  trait GenericScope extends Scope {
    def authorize(sessionId: String): Set[Perm] => Option[UserSession] = {
      (permissions) => sessionStore get sessionId filter (session => permissions.isEmpty || permissions.intersect(session.role.permissions).nonEmpty)
    }

    implicit lazy val sessionProvider: SessionProvider[UserSession, Perm] = asGenericSessionProvider(authorize)
  }

  trait CustomScope extends Scope {
    implicit lazy val sessionProvider: SessionProvider[UserSession, Perm] = new SessionProvider[UserSession, Perm] {
      import akka.http.scaladsl.server.Directives._

      override def obtain(permissions: Set[Perm]): Directive1[UserSession] = {
        cookie("session-id") flatMap { cp =>
          (sessionStore get cp.value).fold[Directive1[UserSession]](reject(AuthorizationFailedRejection.get)) { session =>
            if (permissions.isEmpty || permissions.intersect(session.role.permissions).nonEmpty) {
              provide(session)
            } else {
              reject(AuthorizationFailedRejection.get)
            }
          }
        }
      }

      override def sessionType: String = "user"
    }
  }

}