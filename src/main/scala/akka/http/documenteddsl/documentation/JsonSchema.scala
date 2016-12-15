package akka.http.documenteddsl.documentation

import org.coursera.autoschema.{AutoSchema, TypeMappings}
import play.api.libs.json.{JsObject, Json}

import scala.reflect.runtime.{universe => ru}
import scala.util.control.NonFatal

object JsonSchema {

  def resolveSchema[T](implicit t: ru.TypeTag[T], as: AutoSchema): JsObject = {
    try as.createSchema[T] catch {
      case NonFatal(err) =>
        def errPath(ex: Throwable): String = ex.getCause match {
          case null => ex.getMessage
          case x    => "[" + ex.getClass.getSimpleName + "]" + ex.getMessage + ". Caused by: " + errPath(x)
        }
        Json.obj("error" -> s"$t: ${errPath(err)}")
    }
  }

  val string: JsObject    = Json.obj("type" -> "string")
  val numeric: JsObject   = Json.obj("type" -> "number", "format" -> "number")
  val boolean: JsObject   = Json.obj("type" -> "boolean")

}

trait DocumentedTypeMappings extends TypeMappings {
  import JsonSchema._

  override def schemaTypeForScala(typeName: String): Option[JsObject] = {
    schemaTypes.get(typeName)
  }

  private val schemaTypes = Map(
    "org.joda.time.DateTime"  -> Json.obj("type" -> "string", "format" -> "date"),
    "java.time.ZonedDateTime" -> Json.obj("type" -> "string", "format" -> "date"),
    "java.time.LocalDate"     -> Json.obj("type" -> "string", "format" -> "date", "pattern" -> localDatePattern),
    "java.util.Date"          -> Json.obj("type" -> "string", "format" -> "date"),
    "java.lang.String"        -> string,
    "scala.Boolean"           -> boolean,
    "scala.Int"               -> numeric,
    "scala.Long"              -> numeric,
    "scala.Double"            -> numeric,
    "scala.math.BigInt"       -> numeric,
    "scala.math.BigDecimal"   -> numeric,
    "java.util.UUID"          -> Json.obj("type" -> "string", "pattern" -> "^[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}$")
  )

  lazy val localDatePattern = "^[0-9]{4}-[0-9]{2}-[0-9]{2}$"

}

object DocumentedTypeMappings extends DocumentedTypeMappings