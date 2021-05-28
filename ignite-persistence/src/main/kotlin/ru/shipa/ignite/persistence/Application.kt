package ru.shipa.ignite.persistence

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication
class Application

/**
 * Ignite Persistence application entry point.
 *
 * @author v.shipugin
 * @see main
 */
fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
