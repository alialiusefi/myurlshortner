inThisBuild(
  List(
    organization      := "com.acme",
    homepage          := Some(url("https://zio.github.io/myurlshortner-consumer/")),
    licenses          := List(),
    developers        := List(),
    scalaVersion      := "3.7.2",
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision
  )
)

addCommandAlias("fmt", "all scalafmtSbt scalafmt test:scalafmt")
addCommandAlias("fix", "; all compile:scalafix test:scalafix; all scalafmtSbt scalafmtAll")
addCommandAlias("check", "; scalafmtSbtCheck; scalafmtCheckAll; compile:scalafix --check; test:scalafix --check")

lazy val root             = project
  .in(file(""))
  .enablePlugins(BuildInfoPlugin)
  .settings(
    publish / skip := true,
    run / fork     := true
  )
val zioVersion            = "2.1.20"
val zioKafkaVersion       = "3.0.0"
val apiCurioSerdesVersion = "2.6.13.Final"
val doobieVersion         = "1.0.0-RC10"

lazy val userEventsConsumer = project
  .in(file("myurlshortner-consumer"))
  .enablePlugins(SbtAvro)
  .settings(
    libraryDependencies ++= Seq(
      "dev.zio"      %% "zio"                                 % zioVersion,
      "dev.zio"      %% "zio-streams"                         % zioVersion,
      "dev.zio"      %% "zio-kafka"                           % zioKafkaVersion,
      "io.apicurio"   % "apicurio-registry-serdes-avro-serde" % apiCurioSerdesVersion,
      "dev.zio"      %% "zio-kafka-testkit"                   % zioKafkaVersion % Test,
      "dev.zio"      %% "zio-test"                            % zioVersion      % Test,
      "dev.zio"      %% "zio-test-sbt"                        % zioVersion      % Test
    )
  )
  .settings(
    excludeDependencies += "org.scala-lang.modules" % "scala-collection-compat_2.13"
  )
  .settings(testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"))
  .settings(
    scalacOptions += "-Wunused:imports" // required by OrganizeImports
  )
