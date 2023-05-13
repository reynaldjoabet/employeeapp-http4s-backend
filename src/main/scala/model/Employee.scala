package model

import io.circe.generic.semiauto.deriveCodec
import io.circe.Codec
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import cats.effect.IO
import org.http4s.EntityEncoder
import org.http4s.EntityDecoder

final case class Employee(
    employeeId: Int,
    firstName: String,
    lastName: String,
    email: String
)

object Employee {
  implicit val codec: Codec[Employee] = deriveCodec[Employee]
  implicit val listEntityEncoder: EntityEncoder[IO, List[Employee]] =
    jsonEncoderOf[IO, List[Employee]]
  implicit val entityEncoder: EntityEncoder[IO, Employee] =
    jsonEncoderOf[IO, Employee]
  implicit val entityDecoder: EntityDecoder[IO, Employee] = jsonOf[IO, Employee]

}
