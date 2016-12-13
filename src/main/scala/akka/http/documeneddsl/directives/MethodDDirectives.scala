package akka.http.documeneddsl.directives

import akka.http.documeneddsl.documentation.RouteDocumentation
import akka.http.scaladsl.server.Directive0
import akka.http.scaladsl.server.directives.MethodDirectives
import org.coursera.autoschema.AutoSchema


trait MethodDDirectives {

  sealed trait MethodDDirective extends DDirective0 with Product { self =>
    def describe(w: RouteDocumentation)(implicit as: AutoSchema): RouteDocumentation = w.method(self.productPrefix)
  }

  case object GET     extends MethodDDirective { val delegate: Directive0 = MethodDirectives.get }
  case object POST    extends MethodDDirective { val delegate: Directive0 = MethodDirectives.post }
  case object PUT     extends MethodDDirective { val delegate: Directive0 = MethodDirectives.put }
  case object DELETE  extends MethodDDirective { val delegate: Directive0 = MethodDirectives.delete }
  case object HEAD    extends MethodDDirective { val delegate: Directive0 = MethodDirectives.head }
  case object OPTIONS extends MethodDDirective { val delegate: Directive0 = MethodDirectives.options }
  case object PATCH   extends MethodDDirective { val delegate: Directive0 = MethodDirectives.patch }

}

object MethodDDirectives extends MethodDDirectives