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
import org.http4s.server.middleware.CSRF
import org.http4s.server.middleware.HSTS
import org.http4s.server.middleware.ResponseTiming
import org.http4s.server.middleware.GZip
import cats.effect
import cats.data.Kleisli
import org.http4s.{Request, Response}
import org.http4s.Http
import org.http4s.Header
import org.http4s.headers.Authorization
import org.http4s.Credentials
import org.http4s.AuthScheme
import effect.std.Supervisor
import org.http4s.HttpApp
import org.http4s.server.middleware.RequestLogger
import org.http4s.server.middleware.ResponseLogger
import org.http4s.websocket.WebSocket



object ServerApp  extends IOApp{

     val httpRoutes=EmployeeRoutes.httpRoutes

     val routes= Router(
         "/api/v1"->httpRoutes
     )


/*
val cookieName = "csrf-token"
  val defaultOriginCheck: Request[IO] => Boolean =CSRF.defaultOriginCheck[IO](_, "localhost", Uri.Scheme.http, None)

  val csrfBuilder = for{
        key  <- CSRF.generateSigningKey[IO]
 
          }yield CSRF[IO,IO](key, defaultOriginCheck)

  
val csrf = csrfBuilder.map(
  _.withCookieName(cookieName)
  .withCookieDomain(Some("localhost"))
  .withCookiePath(Some("/"))
  .withCookieHttpOnly(false)
  .build
)
*/


def auth(http:HttpRoutes[IO])=Kleisli{ request:Request[IO]=>

    request.headers.get[Authorization].collect{
         case Authorization(Credentials.Token(AuthScheme.Bearer,token)) => token
    }
http(request)
}


     def CSP(service:HttpRoutes[IO])=Kleisli{ request:Request[IO]=>
        service(request).map(_.putHeaders(Header("Content-Security-Policy","default-src http://localhost:3000 ; form-action 'self'; script-src 'self'")))
     }

val config = CORSConfig.default
  .withAnyOrigin(false)
  .withAllowCredentials(false)
  .withAllowedMethods(Some(Set(Method.POST,Method.PUT,Method.GET,Method.OPTIONS,Method.DELETE)))
  .withAllowedOrigins(Set("http://localhost:3000"))
  

val corsApp: Http[IO,IO] = CORS(CSP(routes).orNotFound, config)
val hstsApp: Kleisli[IO,Request[IO],Response[IO]]=HSTS(corsApp)

private val loggers: HttpApp[IO] => HttpApp[IO] = {
    { http: HttpApp[IO] =>
      RequestLogger.httpApp(true, true)(http)
    } andThen { http: HttpApp[IO] =>
      ResponseLogger.httpApp(true, true)(http)
    }
  }
/*
val corsApp=CORS.policy
                             .withAllowOriginHost(Set(Origin.Host(Uri.Scheme.http, Uri.RegName("localhost"),Some(3000))))
                             //.withAllowOriginAll
                             .withAllowMethodsIn(Set(Method.POST,Method.PUT,Method.GET,Method.DELETE))
                             .withAllowCredentials(false) (routes).orNotFound
*/




val server=BlazeServerBuilder[IO]
           .bindHttp(8080,"localhost")
          .withHttpApp(ResponseTiming(hstsApp))
          .resource
                                        
                                                                   /*
                                                                                     server.useForever
                                                                                     .as(ExitCode.Success)
                                                                  */
  
            override def run(args: List[String]): IO[ExitCode] =   server.use(_=>IO.never)
                                                                          .as(ExitCode.Success)


}
