package com.acme.myurlshortner.consumer

import org.jetbrains.exposed.v1.spring.boot.autoconfigure.ExposedAutoConfiguration
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@ImportAutoConfiguration(
    value = [ExposedAutoConfiguration::class],
    exclude = [DataSourceTransactionManagerAutoConfiguration::class]
)
@EnableScheduling
class MyUrlShortnerConsumerApplication

fun main(args: Array<String>) {
    runApplication<MyUrlShortnerConsumerApplication>(*args)
}
