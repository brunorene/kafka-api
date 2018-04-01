package uk.sky.poc.kafkaapi.web

import com.google.gson.stream.JsonReader
import com.hazelcast.core.HazelcastInstance
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import uk.sky.poc.kafkaapi.config.KafkaConfig
import uk.sky.poc.kafkaapi.listener.KafkaBackground
import uk.sky.poc.kafkaapi.support.Error
import java.io.FileReader

@RestController
class KafkaController(private val kafkaTemplate: KafkaTemplate<String, Map<String, Any>>,
                      private val kafkaBackground: KafkaBackground,
                      hz: HazelcastInstance) {

    private val entities: Map<String, String> = hz.getMap<String, String>(KafkaBackground.ENTITIES)

    @GetMapping("/counter")
    fun getCounter() = Mono.just(kafkaBackground.counter)

    @GetMapping("/entity/{uuid}", produces = [APPLICATION_JSON_UTF8_VALUE])
    fun getEntity(@PathVariable uuid: String): Mono<String> = entities[uuid]?.let { Mono.just(it) }
            ?: Mono.error(NoSuchElementException())

    @PostMapping("/load-kafka")
    fun loadKafka(): Mono<String> {
        var count = 0
        JsonReader(FileReader("data-project/solr_dump.json")).use { reader ->
            reader.beginObject()
            while (reader.hasNext()) {
                if (reader.nextName() == "response") {
                    reader.beginObject()
                    while (reader.hasNext()) {
                        if (reader.nextName() == "docs") {
                            reader.beginArray()
                            while (reader.hasNext()) {
                                reader.beginObject()
                                // creating map to send to Kafka
                                val map = mutableMapOf<String, Any>()
                                while (reader.hasNext()) {
                                    val name = reader.nextName()
                                    when (reader.peek().toString()) {
                                        "BEGIN_ARRAY" -> {
                                            val list = mutableListOf<String>()
                                            reader.beginArray()
                                            while (reader.hasNext())
                                                list.add(reader.nextString())
                                            reader.endArray()
                                            map[name] = list
                                        }
                                        else -> map[name] = reader.nextString()
                                    }
                                }
                                reader.endObject()
                                kafkaTemplate.send(ProducerRecord<String, Map<String, Any>>(KafkaConfig.TOPIC,
                                        map[KafkaConfig.ID].toString(), map))
                                count++
                            }
                            reader.endArray()
                        } else
                            reader.skipValue()
                    }
                    reader.endObject()
                } else
                    reader.skipValue()
            }
            reader.endObject()
        }
        return Mono.just("$count were sent to Kafka")
    }

    @ExceptionHandler(NoSuchElementException::class)
    @ResponseStatus(NOT_FOUND)
    fun notFound(ex: Exception): Mono<Error> = Mono.just(Error("Entity not Found!", "1234", ex.javaClass.name))
}