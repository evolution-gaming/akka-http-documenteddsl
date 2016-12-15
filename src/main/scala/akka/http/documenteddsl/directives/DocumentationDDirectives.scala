package akka.http.documenteddsl.directives

import akka.http.documenteddsl.documentation.RouteDocumentation
import akka.http.scaladsl.server.{Directive, _}
import org.coursera.autoschema.AutoSchema

trait DocumentationDDirectives {

  case class Category(category: String*) extends DDirective0 {
    override def describe(w: RouteDocumentation)(implicit as: AutoSchema): RouteDocumentation = w.category(category.toList)
    override def delegate: Directive0 = Directive.Empty
  }

  case class Title(m: String) extends DDirective0 {
    override def describe(w: RouteDocumentation)(implicit as: AutoSchema): RouteDocumentation = w.title(m)
    override def delegate: Directive0 = Directive.Empty
  }

  case class Description(m: String) extends DDirective0 {
    override def describe(w: RouteDocumentation)(implicit as: AutoSchema): RouteDocumentation = w.description(m)
    override def delegate: Directive0 = Directive.Empty
  }

}

object DocumentationDDirectives extends DocumentationDDirectives