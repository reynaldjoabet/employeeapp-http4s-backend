import cats.effect.IOApp
import cats.effect.{ExitCode, IO}
import org.http4s.HttpRoutes
import org.http4s.server.Router
import routes.EmployeeRoutes
import cats.effect.Resource
import org.http4s.server.Server
import org.http4s.server.middleware.CORS
import org.http4s.server.middleware.CORSConfig
import org.http4s.server.middleware.Logger
import org.http4s.headers.Origin
import org.http4s.Uri
import org.http4s.Method._
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
import org.typelevel.ci._
import org.http4s.ember.server.EmberServerBuilder
import com.comcast.ip4s._
import org.http4s.headers.Origin
import org.http4s.headers
import org.http4s.headers
import service.DoobieService
import db._
import org.http4s.Status
import config.AppConfiguration
object ServerApp extends IOApp {

  def auth(http: HttpRoutes[IO]) = Kleisli { request: Request[IO] =>
    request.headers.get[Authorization].collect {
      case Authorization(Credentials.Token(AuthScheme.Bearer, token)) => token
    }
    http(request)
  }

  def CSPService(service: HttpRoutes[IO]) = Kleisli { request: Request[IO] =>
    service(request).map(
      _.putHeaders(
        Header(
          "Content-Security-Policy",
          "default-src http://localhost:4000 ; form-action 'self'; script-src 'self'"
        )
      )
    )
  }

  private val corsService = CORS.policy
    .withAllowOriginHost(
      Set(Origin.Host(Uri.Scheme.http, Uri.RegName("localhost"), Some(3000)))
    )
    .withAllowMethodsIn(Set(POST, PUT, GET, DELETE, OPTIONS))
    .withAllowCredentials(
      true
    ) // set to true for csrf// The default behavior of cross-origin resource requests is for
    // requests to be passed without credentials like cookies and the Authorization header
    .withAllowHeadersIn(Set(ci"X-Csrf-Token", ci"Content-Type"))

  // val corsApp: Http[IO, IO] = CORS(CSP(routes).orNotFound, config)
  // val hstsApp: Kleisli[IO, Request[IO], Response[IO]] = HSTS(corsApp)

  val csrfService = CSRF
    .withGeneratedKey[IO, IO](request =>
      CSRF.defaultOriginCheck(request, "localhost", Uri.Scheme.http, Some(3000))
    )
    .map(builder =>
      builder
        .withCookieName("csrf-token")
        .withCookieHttpOnly(false)
        .withCookieDomain(Some("localhost"))
        .withCookiePath(Some("/"))
        // .withCookieSecure(true)
        .build
        .validate()
    )
  private val loggers: HttpApp[IO] => HttpApp[IO] = {
    { http: HttpApp[IO] =>
      RequestLogger.httpApp(true, true, _ => false)(http)
    } andThen { http: HttpApp[IO] =>
      ResponseLogger.httpApp(true, true, _ => false)(http)
    }
  }

  val server: Resource[IO, Server] =
    Resource
      .eval(csrfService)
      .flatMap(service =>
        EmployeeRoutes.routes.map(httpRoutes => (httpRoutes, service))
      )
      .flatMap { case (httpRoutes, service) =>
        EmberServerBuilder
          .default[IO]
          .withHost(host"localhost")
          .withPort(port"8084")
          .withHttpApp(loggers(service(corsService(httpRoutes.orNotFound))))
          .build
      }

  override def run(args: List[String]): IO[ExitCode] =
             DBMigration
             .migrate() *>server.useForever
          .race(
            IO.println("Press Any Key to stop the  server") *> IO.readLine
              .handleErrorWith(e =>
                IO.println(s"There was an error! ${e.getMessage}")
              ) *> IO.println(
              "Stopping Server"
            ) 
          )*> DBMigration.reset()
          .as(ExitCode.Success)

}
