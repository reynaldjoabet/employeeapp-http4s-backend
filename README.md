# employeeapp-http4s-backend

 A simple CRUD application built using Http4s library
 ## Stack
 - Http4s
 - Quill
 - Circe
## 

- On the terminal, enter the`sbt` shell by typing `sbt`
- Then run the application by typing `run` on the sbt shell
- Use `curl` or `postman` to interact with the running application
### GET All
`curl http://localhost:8080/api/v1/employees`

### GET Employee

`curl http://localhost:8080/api/v1/employee/sampleEmployeeId`

### POST 
`curl -X POST -H "Content-Type: application/json" http://localhost:8080/api/v1/addEmployee -d '{   
  "firstName":"Cristino" ,
  "lastName":"Ronaldo",
  "email":"roynaldo@gmail.com"
  }' 

  ### DELETE
`curl -X DELETE http://localhost:8080/api/v1/employee/sampleEmployeeId`

  ### PUT

`curl -X PUT -H "Content-Type: application/json" http://localhost:8080/api/v1/employee/sampleEmployeeId -d '{
  "employeeId" : sampleEmployeeid,   
  "firstName":"Cristino" ,
  "lastName":"Ronaldo",
  "email":"roynaldo@gmail.com"
  }' 