package model

import io.circe.generic.semiauto.deriveCodec
import io.circe.Codec
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import cats.effect.IO
import org.http4s.EntityEncoder
import org.http4s.EntityDecoder
import java.util.UUID

final case class CreateEmployee(
    firstName: String,
    lastName: String,
    email: String
)

object CreateEmployee {
  implicit val codec: Codec[CreateEmployee] = deriveCodec[CreateEmployee]

  implicit val entityDecoder: EntityDecoder[IO, CreateEmployee] =
    jsonOf[IO, CreateEmployee]

}
