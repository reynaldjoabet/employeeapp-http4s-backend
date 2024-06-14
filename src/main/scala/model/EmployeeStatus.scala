package model

import cats.effect.IO

import io.circe.generic.semiauto.deriveCodec
import io.circe.Codec
import org.http4s.circe.jsonEncoderOf
import org.http4s.EntityEncoder

final case class EmployeeStatus(deleted: Boolean)

object EmployeeStatus {

  implicit val statusCodec: Codec[EmployeeStatus] = deriveCodec[EmployeeStatus]

  implicit val statusEntityEncoder: EntityEncoder[IO, EmployeeStatus] =
    jsonEncoderOf[IO, EmployeeStatus]

}
