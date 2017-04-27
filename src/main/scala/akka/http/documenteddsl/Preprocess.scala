package akka.http.documenteddsl

import akka.http.scaladsl.marshallers.playjson.PlayJsonSupport.PlayJsonError
import akka.http.scaladsl.model.MediaTypes.`application/json`
import akka.http.scaladsl.server.{RejectionError, ValidationRejection}
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, FromStringUnmarshaller, Unmarshaller}
import akka.util.ByteString
import play.api.libs.json.{JsError, JsValue, Json, Reads}

class PreprocessedFromStringUnmarshaller[T](sanitize: Preprocess[String], _fsu: FromStringUnmarshaller[T]) {
  implicit val fsu: FromStringUnmarshaller[T] = Unmarshaller withMaterializer {
    implicit ec =>
      implicit mat =>
        string =>
          _fsu(sanitize(string))
  }
}

object PreprocessedFromStringUnmarshaller {
  implicit def unmarshaller[T](implicit sanitize: Preprocess[String] = Preprocess.identity, fsu: FromStringUnmarshaller[T]): PreprocessedFromStringUnmarshaller[T] = {
    new PreprocessedFromStringUnmarshaller(sanitize, fsu)
  }
}

class PreprocessedFromEntityUnmarshaller[T](sanitize: Preprocess[JsValue], reads: Reads[T]) {
  private val jsonStringUnmarshaller =
    Unmarshaller.byteStringUnmarshaller
      .forContentTypes(`application/json`)
      .mapWithCharset {
        case (ByteString.empty, _) => throw Unmarshaller.NoContentException
        case (data, charset)       => data.decodeString(charset.nioCharset.name)
      }

  implicit val fsu: FromEntityUnmarshaller[T] = jsonStringUnmarshaller map { data =>
    val json = sanitize(Json parse data)

    reads reads json recoverTotal { error =>
      throw RejectionError(ValidationRejection(JsError.toJson(error).toString, Some(PlayJsonError(error))))
    }
  }
}

object PreprocessedFromEntityUnmarshaller {
  implicit def unmarshaller[T](implicit sanitize: Preprocess[JsValue] = Preprocess.identity, reads: Reads[T]): PreprocessedFromEntityUnmarshaller[T] = {
    new PreprocessedFromEntityUnmarshaller(sanitize, reads)
  }
}

trait Preprocess[T] {
  def apply(x: T): T
}
object Preprocess {
  def identity[T]: Preprocess[T] = new Preprocess[T] {
    override def apply(x: T): T = x
  }
}