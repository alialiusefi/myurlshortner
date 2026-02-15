package com.acme.myurlshortner.consumer.application.repo.config

import io.r2dbc.pool.ConnectionPool
import io.r2dbc.pool.ConnectionPoolConfiguration
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactoryOptions
import io.r2dbc.spi.IsolationLevel
import org.jetbrains.exposed.v1.core.vendors.PostgreSQLDialect
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabaseConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RepoConfig(
    val properties: DataSourceConfig
) {

    @Configuration
    data class DataSourceConfig(
        @Value($$"${app.datasource.url}")
        val url: String,
    )

    @Bean
    fun reactiveDataSource(): R2dbcDatabase {
        val connectionFactory = ConnectionFactories.get(ConnectionFactoryOptions.parse(properties.url))
        val pool = ConnectionPool(
            ConnectionPoolConfiguration
                .builder(connectionFactory)
                .build()
        )
        return R2dbcDatabase.connect(
            connectionFactory = pool,
            databaseConfig = R2dbcDatabaseConfig {
                defaultMaxAttempts = 1
                defaultR2dbcIsolationLevel = IsolationLevel.READ_COMMITTED
                explicitDialect = PostgreSQLDialect()
            }
        )
    }
}