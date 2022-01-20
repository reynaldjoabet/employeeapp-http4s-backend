package db
import io.getquill.H2JdbcContext
import io.getquill.CamelCase
import model.Employee
import java.util.UUID
object H2EmployeeRepository extends EmployeeRepository{

 
  private object ctx  extends H2JdbcContext(CamelCase,"database")
  import ctx._



private val employees=List(Employee(UUID.randomUUID(),"Paul","Peter","paulpeter@gmail.com"),
Employee(UUID.randomUUID(),"Peter","Doe","peterdoe@gmail.com"),
Employee(UUID.randomUUID(),"Mason","Kerry","kerry@gmail.com"),
Employee(UUID.randomUUID(),"John","Max","johnmax@gmail.com")

)


ctx.run{
    quote{
    liftQuery(employees).foreach{employee=>
query[Employee].insert(employee)
    }
    
}
}


override def getAllEmployees(): List[Employee] = {
    val q=quote{
        query[Employee]
    }
    ctx.run(q).toList
}


def addEmployee(employee:Employee):Int={
    val q=quote{
        query[Employee].insert(lift(employee))
    }
    ctx.run(q).toInt
}


def findEmployeeByID(employeeId:UUID):Option[Employee]={
   

val q=quote{
    query[Employee].filter(_.employeeId==lift(employeeId))
}
ctx.run(q).headOption
}


  def updateEmployee(employeeId:UUID,employee:Employee):Int={
  val q=quote{
    query[Employee].filter(_.employeeId==lift(employeeId)).update(lift(employee))
        }
ctx.run(q).toInt
    
  }
}
