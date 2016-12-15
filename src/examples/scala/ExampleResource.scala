import scala.collection.concurrent.TrieMap
case class ExampleResource(id: String, name: String, description: Option[String])

object ExampleResource {
  val collection: TrieMap[String, ExampleResource] = TrieMap(
    "x" -> ExampleResource("x", "foo", None),
    "y" -> ExampleResource("y", "bar", None))
}
