package service

import java.util.UUID

import cats.effect.kernel.Async

import db.Doobie
import doobie.implicits._
import doobie.util.transactor.Transactor
import model.CreateEmployee
import model.Employee

abstract class DoobieService[F[_]: Async] private (xa: Transactor[F]) extends EmployeeService[F] {

  def addEmployee(employee: CreateEmployee) =
    sql"INSERT INTO employees(firstName,lastName,email) VALUES(${employee
        .firstName},${employee.lastName},${employee.email})"
      .update
      .withUniqueGeneratedKeys[Employee](
        "employeeId",
        "firstName",
        "lastName",
        "email"
      )
      .transact(xa)

  def deleteEmployee(employeeId: Int): F[Int] =
    sql"Delete from employees where employees.employeeId=$employeeId".update.run.transact(xa)

  def getAllEmployees(): F[List[Employee]] =
    sql"Select * from employees".query[Employee].to[List].transact(xa)

  def findEmployeeByID(employeeId: Int): F[Employee] =
    sql"select * from employees where employees.employeeId=$employeeId"
      .query[Employee]
      .unique
      .transact(xa)

  def updateEmployee(employeeId: Int, employee: Employee): F[Employee] =
    sql"Update employees set employeeId=$employeeId ,firstName=${employee.firstName},firstName=${employee
        .firstName},email=${employee.email} where employees.employeeId=$employeeId"
      .update
      .withUniqueGeneratedKeys[Employee](
        "employeeId",
        "firstName",
        "lastName",
        "email"
      )
      .transact(xa)

}

object DoobieService {

  def service[F[_]: Async]() =
    Doobie.hikariTransactor[F].map(pool => new DoobieService[F](pool) {})

}
