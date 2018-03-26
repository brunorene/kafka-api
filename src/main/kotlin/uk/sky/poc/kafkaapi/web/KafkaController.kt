package uk.sky.poc.kafkaapi.web

import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import uk.sky.poc.kafkaapi.KafkaConfig
import java.io.FileReader

@RestController
class KafkaController(private val kafkaTemplate: KafkaTemplate<String, String>) {

    @PostMapping("/load-kafka")
    fun loadKafka(): Mono<String> {
        val gson = Gson()
        var count: Int = 0
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
                                            var list = mutableListOf<String>()
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
                                kafkaTemplate.send(ProducerRecord(KafkaConfig.TOPIC,
                                        map[KafkaConfig.ID].toString(), gson.toJson(map)))
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