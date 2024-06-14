package routes

import java.util.UUID

import scala.util.Random

import cats.effect
import cats.effect.kernel.Async
import cats.effect.kernel.Resource
import cats.effect.IO

import io.circe.Json
import model._
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import org.http4s.websocket.WebSocket
import org.http4s.HttpRoutes
import service.DoobieService
import service.EmployeeService
import service.H2EmployeeRepository

case class EmployeeRoutes private (service: EmployeeService[IO]) extends Http4sDsl[IO] {

  private val httpRoutes = HttpRoutes.of[IO] {
    case GET -> Root / "employees" => service.getAllEmployees().flatMap(Ok(_))

    case request @ POST -> Root / "addEmployee" =>
      request
        .as[CreateEmployee]
        .flatMap { createEmployee =>
          service.addEmployee(createEmployee).flatMap(_ => Created())

        }
        .handleErrorWith(_ => BadRequest(" Failed to created employee"))

    case GET -> Root / "employee" / IntVar(employeeId) =>
      service
        .findEmployeeByID(employeeId)
        .flatMap(employee => Ok(employee))
        .handleErrorWith(_ => NotFound())

    case request @ PUT -> Root / "employee" / IntVar(employeeId) =>
      request
        .as[Employee]
        .flatMap { employee =>
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
        .handleErrorWith(_ => NotFound(s"Employee with $employeeId does not exists"))

  }

  val routes: HttpRoutes[IO] = Router("/api/v1" -> httpRoutes)

}

object EmployeeRoutes {

  val routes: Resource[IO, HttpRoutes[IO]] =
    DoobieService.service[IO].map(service => EmployeeRoutes(service).routes)

}
