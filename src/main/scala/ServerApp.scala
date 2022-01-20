import cats.effect.IOApp
import cats.effect.{ExitCode, IO}
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.HttpRoutes
import org.http4s.server.Router
import routes._
import cats.effect.Resource
import org.http4s.server.Server
import org.http4s.server.middleware.CORS
import org.http4s.server.middleware.CORSConfig
import org.http4s.server.middleware.Logger
import org.http4s.headers.Origin
import org.http4s.Uri
import org.http4s.Method



object ServerApp  extends IOApp{
    
     val httpRoutes=EmployeeRoutes.httpRoutes

     val routes= Router(
         "/api/v1"->httpRoutes
     )




val config = CORSConfig.default
  .withAnyOrigin(false)
  .withAllowCredentials(false)
  .withAllowedMethods(Some(Set(Method.POST,Method.PUT,Method.GET,Method.DELETE)))
  .withAllowedOrigins(Set("http://localhost:3000"))
  

val corsApp = CORS(routes.orNotFound, config)
/*
val corsApp=CORS.policy
                             .withAllowOriginHost(Set(Origin.Host(Uri.Scheme.http, Uri.RegName("localhost"),Some(3000))))
                             //.withAllowOriginAll
                             .withAllowMethodsIn(Set(Method.POST,Method.PUT,Method.GET,Method.DELETE))
                             .withAllowCredentials(false) (routes).orNotFound
*/
     val server: Resource[IO,Server]= BlazeServerBuilder[IO]
                                        .bindHttp(8080,"localhost")
                                        .withHttpApp(corsApp)
                                        .resource
    override def run(args: List[String]): IO[ExitCode] = server.use(_=>IO.never)
                                                         .as(ExitCode.Success)
                                                         
}
