package uk.sky.poc.kafkaapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KafkaApiApplication

fun main(args: Array<String>) {
    runApplication<KafkaApiApplication>(*args)
}
