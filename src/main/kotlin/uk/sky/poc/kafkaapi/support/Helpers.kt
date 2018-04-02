package uk.sky.poc.kafkaapi.support

import com.google.gson.stream.JsonReader
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.kafka.core.KafkaTemplate
import reactor.core.publisher.Mono
import uk.sky.poc.kafkaapi.config.KafkaConfig
import java.io.FileReader

fun loadKafkaData(kafkaTemplate: KafkaTemplate<String, Map<String, Any>>): Mono<String> {
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