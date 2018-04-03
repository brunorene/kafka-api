package uk.sky.poc.splitapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KafkaApiApplication

fun main(args: Array<String>) {
    runApplication<KafkaApiApplication>(*args)
}
