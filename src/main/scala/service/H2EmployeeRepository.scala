package service

import java.util.UUID

import io.getquill.CamelCase
import io.getquill.H2JdbcContext
import model.Employee

object H2EmployeeRepository extends EmployeeRepository {

  private object ctx extends H2JdbcContext(CamelCase, "database")
  import ctx._

  private val employees = List(
    Employee(1, "Paul", "Peter", "paulpeter@gmail.com"),
    Employee(2, "Peter", "Doe", "peterdoe@gmail.com"),
    Employee(3, "Mason", "Kerry", "kerry@gmail.com"),
    Employee(4, "John", "Max", "johnmax@gmail.com")
  )

  ctx.run {
    quote {
      liftQuery(employees).foreach { employee =>
        query[Employee].insert(employee)
      }

    }
  }

  override def getAllEmployees(): List[Employee] = {
    val q = quote {
      query[Employee]
    }
    ctx.run(q).toList
  }

  def addEmployee(employee: Employee): Int = {
    val q = quote {
      query[Employee].insert(lift(employee))
    }
    ctx.run(q).toInt
  }

  def findEmployeeByID(employeeId: Int): Option[Employee] = {

    val q = quote {
      query[Employee].filter(_.employeeId == lift(employeeId))
    }
    ctx.run(q).headOption
  }

  def updateEmployee(employeeId: Int, employee: Employee): Int = {
    val q = quote {
      query[Employee].filter(_.employeeId == lift(employeeId)).update(lift(employee))
    }
    ctx.run(q).toInt

  }

  def deleteEmployee(employeeId: Int): Int = {
    val q = quote {
      query[Employee].filter(_.employeeId == lift(employeeId)).delete
    }
    ctx.run(q).toInt
  }

}
