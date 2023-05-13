package service

import model.Employee

trait EmployeeRepository {
  def getAllEmployees(): List[Employee]
  def addEmployee(employee: Employee): Int
  def findEmployeeByID(employeeId: Int): Option[Employee]
  def updateEmployee(employeeId: Int, employee: Employee): Int
  def deleteEmployee(employeeId: Int): Int

}
