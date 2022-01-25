package db

import model.Employee
import java.util.UUID

private[db] trait EmployeeRepository {
  def getAllEmployees():List[Employee]
  def addEmployee(employee:Employee):Int
  def findEmployeeByID(employeeId:UUID):Option[Employee]
  def updateEmployee(employeeId:UUID,employee:Employee):Int
  def deleteEmployee(employeeId:UUID):Int

}
