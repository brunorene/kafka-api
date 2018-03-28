package uk.sky.poc.kafkaapi.listener

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import uk.sky.poc.kafkaapi.KafkaConfig

@Component
class KafkaBackground(var counter: Int = 0) {

    @KafkaListener(topics = [(KafkaConfig.TOPIC)], containerFactory = KafkaConfig.FACTORY, groupId = "api")
    fun receiveEntities(results: List<Map<String, String>>) {
        println(results)
        counter += results.size
    }

}