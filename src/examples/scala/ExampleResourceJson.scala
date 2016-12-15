import play.api.libs.json._

object ExampleResourceJson {
  case class CreateResource(name: String, description: Option[String])
  val CreateExample = CreateResource("some name", Some("some description"))

  case class UpdateResource(name: Option[String], description: Option[Option[String]])
  val UpdateExample = UpdateResource(Some("some name"), Some(Some("some description")))

  implicit val resourceFormat: Format[ExampleResource] = Json.format[ExampleResource]
  implicit val createResourceFormat: Format[CreateResource] = Json.format[CreateResource]
  implicit val updateResourceFormat: Format[UpdateResource] = new Format[UpdateResource] {
    override def reads(json: JsValue): JsResult[UpdateResource] = {
      for {
        name <- (json \ "name").validateOpt[String]
        description <- (json \ "description").asOpt[JsValue] match {
          case Some(JsString(x))  => JsSuccess(Some(Some(x)))
          case Some(JsNull)       => JsSuccess(Some(None))
          case None               => JsSuccess(None)
          case Some(_)            => JsError()
        }
      } yield UpdateResource(name, description)
    }
    override def writes(o: UpdateResource): JsValue = {

      val _name = o.name map (name => Json.obj("name" -> name)) getOrElse Json.obj()
      val _description = o.description match {
        case Some(Some(x))  => Json.obj("description" -> x)
        case Some(None)     => Json.obj("description" -> JsNull)
        case None           => Json.obj()
      }

      Json.obj() ++ _name ++ _description
    }
  }
}
