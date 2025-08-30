inThisBuild(
  List(
    organization := "com.acme",
    homepage := Some(url("https://zio.github.io/myurlshortner-consumer/")),
    licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    developers := List(),
    scalaVersion := "3.7.2"
  )
)

addCommandAlias("fmt", "all scalafmtSbt scalafmt test:scalafmt")
addCommandAlias("fix", "; all compile:scalafix test:scalafix; all scalafmtSbt scalafmtAll")
addCommandAlias("check", "; scalafmtSbtCheck; scalafmtCheckAll; compile:scalafix --check; test:scalafix --check")


lazy val root = project
  .in(file(""))
  .enablePlugins(BuildInfoPlugin)
  .settings(
    publish / skip := true,
    run / fork := true
  )

val zioVersion = "2.1.20"
val zioKafkaVersion = "3.0.0"

lazy val userEventsConsumer = project
  .in(file("myurlshortner-consumer"))
  .settings(
    libraryDependencies ++= Seq(
      "dev.zio"     %% "zio"               % zioVersion,
      "dev.zio"     %% "zio-streams"       % zioVersion,
      "dev.zio"     %% "zio-kafka"         % zioKafkaVersion,
      "io.apicurio" % "apicurio-registry-serdes-avro-serde" % "2.6.13.Final" % Test,
      "dev.zio"     %% "zio-kafka-testkit" % zioKafkaVersion   % Test,
      "dev.zio"     %% "zio-test"          % zioVersion        % Test,
      "dev.zio"     %% "zio-test-sbt"      % zioVersion        % Test
    )
  )
  .settings(
    excludeDependencies += "org.scala-lang.modules" % "scala-collection-compat_2.13"
  )
  .settings(testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"))

