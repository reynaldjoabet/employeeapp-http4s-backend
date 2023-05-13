package routes

import org.http4s.dsl.Http4sDsl
import cats.effect.IO
import org.http4s.HttpRoutes
import service.H2EmployeeRepository
import model._
import org.http4s.circe.{jsonOf, jsonEncoderOf}
import io.circe.Json
import java.util.UUID
import scala.util.Random
import org.http4s.websocket.WebSocket
import service.DoobieService
import cats.effect.kernel.Async
import service.EmployeeService
import cats.effect.kernel.Resource
import org.http4s.server.Router
import cats.effect
case class EmployeeRoutes private (service: EmployeeService[IO])
    extends Http4sDsl[IO] {

  private val httpRoutes = HttpRoutes.of[IO] {
    case GET -> Root / "employees" => service.getAllEmployees().flatMap(Ok(_))

    case request @ POST -> Root / "addEmployee" =>
      request
        .as[CreateEmployee]
        .flatMap { createEmployee =>
          service
            .addEmployee(createEmployee)
            .flatMap(_ => Created())

        }
        .handleErrorWith(_ => BadRequest(" Failed to created employee"))

    case GET -> Root / "employee" / IntVar(employeeId) =>
      service
        .findEmployeeByID(employeeId)
        .flatMap(employee => Ok(employee))
        .handleErrorWith(_ => NotFound())

    case request @ PUT -> Root / "employee" / IntVar(employeeId) =>
      request.as[Employee].flatMap { employee =>
        if (employeeId == employee.employeeId) {
          service
            .updateEmployee(employeeId, employee)
            .flatMap(employee => Created(employee))
            .handleErrorWith(_ => BadRequest())
        } else Forbidden()

      }

    case req @ DELETE -> Root / "employee" / IntVar(employeeId) =>
      service
        .deleteEmployee(employeeId)
        .flatMap(_ => Ok(EmployeeStatus(true)))
        .handleErrorWith(_ =>
          NotFound(s"Employee with ${employeeId} does not exists")
        )

  }
  val routes: HttpRoutes[IO] = Router("/api/v1" -> httpRoutes)
}

object EmployeeRoutes {
  val routes: Resource[IO, HttpRoutes[IO]] =
    DoobieService.service[IO].map(service => EmployeeRoutes(service).routes)
}
