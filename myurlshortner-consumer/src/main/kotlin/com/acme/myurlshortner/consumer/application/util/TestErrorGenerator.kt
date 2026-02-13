package com.acme.myurlshortner.consumer.application.util

import org.slf4j.LoggerFactory
import kotlin.random.Random

object TestErrorGenerator {
    private val logger = LoggerFactory.getLogger(TestErrorGenerator::class.java)

    fun generateTestError() {
        val randomError = Random.nextInt(1, 101)
        if (randomError in 1..50) {
            logger.info("Generated error number $randomError")
            throw IllegalArgumentException("Random error 1 <= $randomError <= 50")
        } else {
            logger.info("No Error")
        }
    }
}
