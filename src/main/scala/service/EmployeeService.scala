package service

import model.Employee

import model.CreateEmployee

trait EmployeeService[F[_]] {
  def getAllEmployees(): F[List[Employee]]
  def addEmployee(employee: CreateEmployee): F[Employee]
  def findEmployeeByID(employeeId: Int): F[Employee]
  def updateEmployee(employeeId: Int, employee: Employee): F[Employee]
  def deleteEmployee(employeeId: Int): F[Int]
}
