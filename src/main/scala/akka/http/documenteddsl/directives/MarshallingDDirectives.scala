package akka.http.documenteddsl.directives

import akka.http.documenteddsl.documentation.RouteDocumentation
import akka.http.scaladsl.server.Directive1
import akka.http.scaladsl.unmarshalling._
import org.coursera.autoschema.AutoSchema
import play.api.libs.json.{Reads, Writes}

import scala.reflect.runtime.{universe => ru}

trait MarshallingDDirectives {

  final class In[T](example: Option[T] = None)(implicit um: FromRequestUnmarshaller[T], ev: ru.TypeTag[T], writes: Writes[T], reads: Reads[T]) extends DDirective1[T] {
    import akka.http.scaladsl.server.directives.MarshallingDirectives._

    def describe(w: RouteDocumentation)(implicit as: AutoSchema): RouteDocumentation = w.in[T](example map writes.writes)
    def delegate: Directive1[T] = entity(as[T])
  }

  object In {
    def apply[T](implicit um: FromRequestUnmarshaller[T], ev: ru.TypeTag[T], writes: Writes[T], reads: Reads[T]): In[T] = new In()
    def apply[T](example: T)(implicit um: FromRequestUnmarshaller[T], ev: ru.TypeTag[T], writes: Writes[T], reads: Reads[T]): In[T] = new In(Some(example))
  }

}

object MarshallingDDirectives extends MarshallingDDirectives