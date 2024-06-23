// The simplest possible sbt build file is just one line:

scalaVersion := "2.13.10"

version := "1.0"
val postgresVersion                   = "42.5.4"
val cirisVersion                      = "2.4.0"
val Http4s                            = "0.23.18"
val Circe                             = "0.14.1"
val Logback                           = "1.2.10"
val doobieVersion                     = "1.0.0-RC1"
val logbackVersion                    = "1.4.5"
val doobie_hikari                     = "org.tpolecat"        %% "doobie-hikari"   % doobieVersion
val flywayVersion                     = "9.8.3"
val flyway                            = "org.flywaydb"         % "flyway-core"     % flywayVersion
val doobie                            = "org.tpolecat"        %% "doobie-core"     % doobieVersion
val logback                           = "ch.qos.logback"       % "logback-classic" % logbackVersion
val ciris_hocon                       = "lt.dvim.ciris-hocon" %% "ciris-hocon"     % "1.0.1"
val postgres                          = "org.postgresql"       % "postgresql"      % postgresVersion
def ciris(artifact: String): ModuleID = "is.cir"              %% artifact          % cirisVersion

val doobie_postgres =
  "org.tpolecat" %% "doobie-postgres" % doobieVersion

val cirisCore = ciris("ciris")

lazy val root = (project in file(".")).settings(
  name := "EmployeeApp",
  libraryDependencies ++= Seq(
    "org.http4s"    %% "http4s-dsl"          % "0.23.18",
    "org.http4s"    %% "http4s-circe"        % "0.23.18",
    "org.http4s"    %% "http4s-core"         % "0.23.18",
    "org.http4s"    %% "http4s-ember-server" % "0.23.18",
    "ch.qos.logback" % "logback-classic"     % Logback,
    "io.circe"      %% "circe-generic"       % Circe,
    "com.h2database" % "h2"                  % "2.0.206",
    "io.getquill"   %% "quill-jdbc"          % "3.12.0",
    doobie,
    doobie_hikari,
    flyway,
    ciris_hocon,
    cirisCore,
    logback,
    postgres,
    doobie_postgres
  )
)
