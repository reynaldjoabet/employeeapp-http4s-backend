package db

import scala.concurrent.ExecutionContext

import cats.effect.kernel.{Async, Resource}
import cats.effect.IO

import doobie._
import doobie.hikari.HikariTransactor
import doobie.hikari.HikariTransactor.newHikariTransactor
import doobie.implicits._
import doobie.util.transactor
import doobie.util.transactor.Transactor.Aux

object Doobie {

  private val ec = ExecutionContext.global

//HikariTransactor
  def hikariTransactor[F[_]: Async]: Resource[F, HikariTransactor[F]] =
    newHikariTransactor[F](
      "org.postgresql.Driver",
      "jdbc:postgresql:employee_db",
      "postgres",
      "",
      ec
    )

  // A transactor that gets connections from java.sql.DriverManager and executes blocking operations
  val xa1: Aux[IO, Unit] = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",     // driver classname
    "jdbc:postgresql:social_db", // connect URL (driver-specific)
    "postgres",                  // user
    ""                           // password
  )

}
