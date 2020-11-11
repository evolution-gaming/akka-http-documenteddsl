package akka.http.documenteddsl.directives

import akka.http.scaladsl.server.RequestContext
import akka.http.scaladsl.server.util.Tuple

trait BasicDDirectives {
  def pass: DDirective0 = DDirective.Empty
  def provide[T](value: T): DDirective1[T] = tprovide(Tuple1(value))
  def tprovide[L: Tuple](values: L): DDirective[L] = DDirective { _(values) }
  def extract[T](f: RequestContext => T): DDirective1[T] = textract(ctx => Tuple1(f(ctx)))
  def textract[L: Tuple](f: RequestContext => L): DDirective[L] = DDirective { inner => ctx => inner(f(ctx))(ctx) }
}

object BasicDDirectives extends BasicDDirectives