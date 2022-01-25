package routes

import org.http4s.dsl.Http4sDsl
import cats.effect.IO
import org.http4s.HttpRoutes
import db.H2EmployeeRepository
import model._
import org.http4s.circe.{jsonOf,jsonEncoderOf}
import io.circe.Json
import java.util.UUID


object EmployeeRoutes extends Http4sDsl[IO] {
   implicit val  decoder=jsonOf[IO,Json]
   implicit val  encoder=jsonEncoderOf[IO,Json]

    val httpRoutes= HttpRoutes.of[IO]{
        case GET-> Root/"employees" =>Ok(H2EmployeeRepository.getAllEmployees())

        case request@POST -> Root/"addEmployee" => request.as[Json].map{ data=>
                                               Employee(UUID.randomUUID(),data.\\("firstName").head.toString().replaceAll("\"",""),
                                               data.\\("lastName").head.toString().replaceAll("\"",""),
                                               data.\\("email").head.toString().replaceAll("\"",""))
                                               }.flatMap{employee=>
                                                  if(H2EmployeeRepository.addEmployee(employee)==1) 
                                            Created()
                                                  else
                                            BadRequest(" Failed to created employee")
                                                }
        
case GET->Root /"employee"/UUIDVar(employeeId)=>H2EmployeeRepository.findEmployeeByID(employeeId).fold(NotFound())(employee=>Ok(employee))


case request@PUT-> Root/"employee"/UUIDVar(employeeId)=>  request.as[Employee].flatMap{employee=>
    if(employeeId==employee.employeeId) {
if(H2EmployeeRepository.updateEmployee(employeeId,employee)>0) Created(employee) else BadRequest()
    } else  Forbidden()


}

  case req@DELETE->Root /"employee"/UUIDVar(employeeId)  => if(H2EmployeeRepository.deleteEmployee(employeeId)>0) Ok(EmployeeStatus(true)) else NotFound(s"Employee with ${employeeId} does not exists")     
    
  
}
}
