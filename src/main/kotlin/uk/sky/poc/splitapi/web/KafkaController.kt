package uk.sky.poc.splitapi.web

import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import uk.sky.poc.splitapi.support.Error
import uk.sky.poc.splitapi.support.loadKafkaData

@RestController
class KafkaController(private val kafkaTemplate: KafkaTemplate<String, Map<String, Any>>,
                      private val entities: Map<String, String>) {

    @GetMapping("/counter")
    fun getCounter(): Mono<Int> {
        return Mono.just(entities.size)
    }

    @GetMapping("/entity/{uuid}", produces = [APPLICATION_JSON_UTF8_VALUE])
    fun getEntity(@PathVariable uuid: String): Mono<String> {
        return entities[uuid]?.let { Mono.just(it) }
                ?: Mono.error(NoSuchElementException())
    }

    @PostMapping("/load-kafka")
    fun loadKafka(): Mono<String> = loadKafkaData(kafkaTemplate)

    @ExceptionHandler(NoSuchElementException::class)
    @ResponseStatus(NOT_FOUND)
    fun notFound(ex: Exception): Mono<Error> {
        return Mono.just(Error("Entity not Found!", "1234", ex.javaClass.name))
    }
}