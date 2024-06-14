package model

import java.util.UUID

import cats.effect.IO

import io.circe.generic.semiauto.deriveCodec
import io.circe.Codec
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.EntityDecoder
import org.http4s.EntityEncoder

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
