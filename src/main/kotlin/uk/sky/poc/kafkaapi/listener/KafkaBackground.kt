package uk.sky.poc.kafkaapi.listener

import com.hazelcast.core.HazelcastInstance
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import uk.sky.poc.kafkaapi.KafkaConfig

@Component
class KafkaBackground(var counter: Int = 0, private val hz: HazelcastInstance) {

    companion object {
        const val ENTITIES = "entities"
    }

    @KafkaListener(topics = [(KafkaConfig.TOPIC)], containerFactory = KafkaConfig.FACTORY, groupId = "api")
    fun receiveEntities(results: List<Map<String, String>>) {
        val distMap: MutableMap<String, Map<String, Any>> = hz.getMap(ENTITIES)
        for (map in results)
            distMap[map["id"].toString()] = map
        counter += results.size
    }

}