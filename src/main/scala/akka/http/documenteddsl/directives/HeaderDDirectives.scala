package akka.http.documenteddsl.directives

import akka.http.documenteddsl.documentation.{ParamDocumentation, RouteDocumentation}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.directives._
import akka.http.scaladsl.unmarshalling._
import org.coursera.autoschema.AutoSchema

import scala.reflect.ClassTag
import scala.reflect.runtime.{universe => ru}

trait HeaderDDirectives {
  sealed trait Header[T] extends DDirective1[T]

  case class HeaderByName(name: String, acceptedValues: String*) extends Header[String] {
    def describe(w: RouteDocumentation)(implicit as: AutoSchema): RouteDocumentation = {
      w.header(name, required = true, constraints = if (acceptedValues.isEmpty) None else Some(acceptedValues.toSet))
    }
    def delegate: Directive1[String] = {
      if (acceptedValues.isEmpty) headerValueByName(name) else headerValueByName(name) filter acceptedValues.contains
    }
  }

  case class HeaderByType[T <: HttpHeader](implicit ct: ClassTag[T]) extends Header[T] {
    def describe(w: RouteDocumentation)(implicit as: AutoSchema): RouteDocumentation = {
      w.header(ModeledCompanion.nameFromClass(ct.runtimeClass), required = true, constraints = None)
    }
    def delegate: Directive1[T] = {
      headerValueByType[T](HeaderMagnet.fromClassTagNormalHeader[T](ct))
    }
  }

  object Header {
    def apply(name: String, acceptedValues: String*): Header[String]  = HeaderByName(name, acceptedValues:_*)
    def apply(s: Symbol, acceptedValues: String*): Header[String]     = HeaderByName(s.name, acceptedValues:_*)
    def apply[T <: HttpHeader: ClassTag]: Header[T] = HeaderByType[T]()
  }

  case class OptHeader(name: String, acceptedValues: String*) extends DDirective1[Option[String]] {
    def describe(w: RouteDocumentation)(implicit as: AutoSchema): RouteDocumentation = {
      w.header(name, required = false, constraints = if (acceptedValues.isEmpty) None else Some(acceptedValues.toSet))
    }
    def delegate: Directive1[Option[String]] = {
      if (acceptedValues.isEmpty) optionalHeaderValueByName(name) else {
        def accepted(headerValue: Option[String]): Boolean = headerValue match {
          case None => true
          case Some(headerValue) => acceptedValues contains headerValue
        }
        optionalHeaderValueByName(name) filter accepted
      }
    }
  }

  object OptHeader {
    def apply(s: Symbol, acceptedValues: String*): OptHeader = OptHeader(s.name, acceptedValues:_*)
    def apply[T](s: ModeledCompanion[T], acceptedValues: String*): OptHeader = OptHeader(s.name, acceptedValues:_*)
  }

}

object HeaderDDirectives extends HeaderDDirectives