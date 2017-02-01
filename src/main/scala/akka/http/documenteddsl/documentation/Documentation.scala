package akka.http.documenteddsl.documentation

import play.api.libs.json._

import scala.collection.mutable.ListBuffer

case class Documentation(routes: List[RouteDocumentation] = List.empty) {
  import Documentation._
  import DocumentationJson._

  def withRoute(f: RouteDocumentation => RouteDocumentation): Documentation = {
    copy(routes = f(RouteDocumentation()) +: routes)
  }

  lazy val toc: JsValue = Json toJson computeToc(routes)

  private lazy val routeIndex = routes.map(r => r.uid -> r).toMap

  def route(uid: String): Option[RouteDocumentation] = routeIndex get uid
}

object Documentation {
  sealed trait Node extends Product {
    def label: String
    def children: Seq[Node]
    def dump(i: Int = 0): String = {
      val sb = new StringBuilder
      sb.append(" " * i).append(label).append(" ").append(productPrefix).append("\n")
      sb.append(children map {_ dump i + 2} mkString "")
      sb.toString()
    }
  }
  case class TopicNode(label: String, children: ListBuffer[Node] = ListBuffer.empty) extends Node {
    def :+(n: Node): TopicNode = copy(children = children += n)
  }
  case class RouteNode(label: String, uid: String) extends Node {
    val children = Nil
  }

  private case class RoutePointer(category: List[String], uid: String, title: String) {
    private def findOrCreateSubTopic(node: TopicNode, label: String)(f: TopicNode => Unit): TopicNode = {
      val subOpt = node.children collectFirst {
        case x: TopicNode if x.label == label => x
      }

      val sub = subOpt getOrElse {
        val t = TopicNode(label)
        node :+ t
        t
      }

      f(sub)

      node
    }
    def merge(toc: TopicNode): TopicNode = category match {
      case head :: Nil  => findOrCreateSubTopic(toc, head) {_ :+ RouteNode(title, uid)}
      case head :: tail => findOrCreateSubTopic(toc, head) {RoutePointer(tail, uid, title) merge _}
      case Nil          => toc
    }
  }

  private object RoutePointer {
    def apply(route: RouteDocumentation): RoutePointer = RoutePointer(
      category  = route.category getOrElse List.empty,
      uid       = route.uid,
      title     = route.title getOrElse "Untitled")
  }
  private def computeToc(routes: List[RouteDocumentation]): Node = {
    val pointers = routes map RoutePointer.apply
    pointers.foldRight(TopicNode("__")) {_ merge _}
  }

}