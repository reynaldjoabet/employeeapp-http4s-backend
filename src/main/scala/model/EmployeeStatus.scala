package model

import io.circe.Codec
import org.http4s.EntityEncoder
import cats.effect.IO
import org.http4s.circe.{jsonEncoderOf}
import io.circe.generic.semiauto.deriveCodec

final case class EmployeeStatus(deleted: Boolean)

object EmployeeStatus {
  implicit val statusCodec: Codec[EmployeeStatus] = deriveCodec[EmployeeStatus]

  implicit val statusEntityEncoder: EntityEncoder[IO, EmployeeStatus] =
    jsonEncoderOf[IO, EmployeeStatus]
}
