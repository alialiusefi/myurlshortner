package com.acme.myurlshortner.consumer.application.config

import zio._

object ConnectionPoolConfig {

  def loadDbConfig(): ZIO[Any, SecurityException, DbConfig] = for {
    url      <- System.env("POSTGRES_URL")
    username <- System.env("POSTGRES_USERNAME")
    password <- System.env("POSTGRES_PASSWORD")
  } yield (DbConfig(url.get, username.get, password.get))

  def transactor(dbConfig: DbConfig): ZIO[Scope, Throwable, Transactor[[Task] =>> RIO[Scope, Task]]{type A = HikariDataSource}] = for {
    runtime    <- ZIO.runtime[Any]
    executor   <- ZIO.blockingExecutor
    dispatcher  = Dispatcher.sequential
    transactor <- HikariTransactor
                    .newHikariTransactor(
                      "org.postgresql.Driver",
                      dbConfig.url,
                      dbConfig.username,
                      dbConfig.passowrd,
                      executor.asExecutionContext,
                      None
                    ).toManaged
  } yield (transactor)

  type DBTransactor = Transactor[[Task] =>> RIO[Scope, Task]]

  def layer: ZLayer[Scope, Throwable, DBTransactor] = ZLayer.fromZIO(
    for {
      config <- loadDbConfig()
      transactor <- transactor(config)
    } yield (transactor)
  )

  
}

case class DbConfig(
  val url: String,
  val username: String,
  val passowrd: String
)
