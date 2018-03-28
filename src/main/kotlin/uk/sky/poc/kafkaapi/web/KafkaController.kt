package uk.sky.poc.kafkaapi.web

import com.google.gson.stream.JsonReader
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import uk.sky.poc.kafkaapi.KafkaConfig
import uk.sky.poc.kafkaapi.listener.KafkaBackground
import java.io.FileReader

@RestController
class KafkaController(private val kafkaTemplate: KafkaTemplate<String, Map<String, Any>>,
                      private val kafkaBackground: KafkaBackground) {


    @GetMapping("/counter")
    fun getCounter() = Mono.just(kafkaBackground.counter)

    @PostMapping("/load-kafka")
    fun loadKafka(): Mono<String> {
        var count = 0
        JsonReader(FileReader("solr_dump.json")).use { reader ->
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
}