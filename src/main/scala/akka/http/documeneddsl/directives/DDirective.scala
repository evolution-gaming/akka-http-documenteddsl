package akka.http.documeneddsl.directives

import akka.NotUsed
import akka.http.documeneddsl._
import akka.http.documeneddsl.documentation._
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.util.{ApplyConverter, TupleOps}
import akka.http.scaladsl.settings.{ParserSettings, RoutingSettings}
import akka.stream.Materializer
import akka.stream.scaladsl.Flow
import org.coursera.autoschema.AutoSchema

import scala.concurrent.ExecutionContext
import scala.language.implicitConversions

trait DDirective[L] { self =>
  def describe(w: RouteDocumentation)(implicit as: AutoSchema): RouteDocumentation
  def &(magnet: DConjunctionMagnet[L]): magnet.Out = magnet(this)
  def delegate: Directive[L]
}

class DDirectiveDelegate[L](dir: Directive[L], writer: RouteDocumentation => RouteDocumentation = identity) extends DDirective[L] {
  override def describe(doc: RouteDocumentation)(implicit as: AutoSchema): RouteDocumentation = writer(doc)
  override def delegate: Directive[L] = dir
}

object DDirective {

  /**
    * Adds `apply` to all Directives with 1 or more extractions,
    * which allows specifying an n-ary function to receive the extractions instead of a Function1[TupleX, Route].
    */
  implicit def addDirectiveApply[L](d: DDirective[L])(implicit hac: ApplyConverter[L], as: AutoSchema): hac.In => DRoute =
    f => new DRoute(
      underlying  = d.delegate.tapply(hac(f)),
      writer      = doc => doc(rd => d.describe(rd)))

  /**
    * Adds `apply` to Directive0. Note: The `apply` parameter is call-by-name to ensure consistent execution behavior
    * with the directives producing extractions.
    */
  implicit def addByNameNullaryApply(d: DDirective0)(implicit as: AutoSchema): (=> DRoute) => DRoute =
    r => new DRoute(
      underlying  = d.delegate.tapply(_ => r),
      writer      = doc => doc(rd => d.describe(rd)))

  implicit def documentedRoute2HandlerFlow(route: DRoute)(implicit
    routingSettings:  RoutingSettings,
    parserSettings:   ParserSettings,
    materializer:     Materializer,
    routingLog:       RoutingLog,
    executionContext: ExecutionContext = null,
    rejectionHandler: RejectionHandler = RejectionHandler.default,
    exceptionHandler: ExceptionHandler = null): Flow[HttpRequest, HttpResponse, NotUsed] = Route.handlerFlow(route)

}

trait DConjunctionMagnet[L] {
  type Out
  def apply(underlying: DDirective[L]): Out
}

object DConjunctionMagnet {

  implicit def fromDirective[L, R]
    (other: DDirective[R])
    (implicit join: TupleOps.Join[L, R]): DConjunctionMagnet[L] { type Out = DDirective[join.Out] } = {

    new DConjunctionMagnet[L] {
      type Out = DDirective[join.Out]

      def apply(underlying: DDirective[L]) =
        new DDirective[join.Out]() {
          def describe(w: RouteDocumentation)(implicit as: AutoSchema): RouteDocumentation = underlying.describe(other.describe(w))
          override def delegate: Directive[join.Out] = (underlying.delegate & other.delegate).asInstanceOf[Directive[join.Out]]
        }
    }
  }

  implicit def fromStandardRoute[L]
    (route: StandardRoute): DConjunctionMagnet[L] { type Out = StandardRoute } = {

    new DConjunctionMagnet[L] {
      type Out = StandardRoute

      def apply(underlying: DDirective[L]) = StandardRoute(underlying.delegate.tapply(_ => route))
    }
  }

  implicit def fromRouteGenerator[T, R <: Route]
    (generator: T ⇒ R): DConjunctionMagnet[Unit] { type Out = RouteGenerator[T] } = {

    new DConjunctionMagnet[Unit] {
      type Out = RouteGenerator[T]

      def apply(underlying: DDirective0) = value => underlying.delegate.tapply(_ => generator(value))
    }
  }
}