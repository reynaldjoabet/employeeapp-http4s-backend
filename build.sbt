// The simplest possible sbt build file is just one line:

scalaVersion := "2.13.13"

version := "1.0"

val postgresVersion                   = "42.5.6"
val cirisVersion                      = "3.6.0"
val Http4s                            = "0.23.18"
val Circe                             = "0.14.1"
val doobieVersion                     = "1.0.0-RC5"
val logbackVersion                    = "1.4.14"

val doobie_hikari                     = "org.tpolecat"        %% "doobie-hikari"   % doobieVersion
val flywayVersion                     = "9.22.3"
val flyway                            = "org.flywaydb"         % "flyway-core"     % flywayVersion
val doobie                            = "org.tpolecat"        %% "doobie-core"     % doobieVersion
val logback                           = "ch.qos.logback"       % "logback-classic" % logbackVersion
val ciris_hocon                       = "lt.dvim.ciris-hocon" %% "ciris-hocon"     % "1.2.0"
val postgres                          = "org.postgresql"       % "postgresql"      % postgresVersion
def ciris(artifact: String): ModuleID = "is.cir"              %% artifact          % cirisVersion

val doobie_postgres =
  "org.tpolecat" %% "doobie-postgres" % doobieVersion

val cirisCore = ciris("ciris")

lazy val root = (project in file(".")).settings(
  name := "EmployeeApp",
  libraryDependencies ++= Seq(
    "org.http4s"    %% "http4s-dsl"          % "0.23.27",
    "org.http4s"    %% "http4s-circe"        % "0.23.27",
    "org.http4s"    %% "http4s-core"         % "0.23.27",
    "org.http4s"    %% "http4s-ember-server" % "0.23.27",
    "io.circe"      %% "circe-generic"       % Circe,
    "io.getquill"   %% "quill-jdbc"          % "3.19.0",
    "com.h2database" % "h2"                  % "2.0.206",
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
