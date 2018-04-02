package uk.sky.poc.kafkaapi.listener

import com.google.gson.Gson
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import uk.sky.poc.kafkaapi.config.KafkaConfig

@Component
class KafkaListener(private val entities: MutableMap<String, String>) {

    private val gson: Gson = Gson()

    @KafkaListener(topics = [(KafkaConfig.TOPIC)], containerFactory = KafkaConfig.FACTORY, groupId = "api")
    fun receiveEntities(results: List<Map<String, String>>) {
        for (map in results)
            entities[map["id"].toString()] = gson.toJson(map)
    }

}