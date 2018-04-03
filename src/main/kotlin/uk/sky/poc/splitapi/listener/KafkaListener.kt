package uk.sky.poc.splitapi.listener

import com.google.gson.Gson
import org.apache.kafka.common.TopicPartition
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.listener.ConsumerSeekAware
import org.springframework.stereotype.Component
import uk.sky.poc.splitapi.config.KafkaConfig

@Component
class KafkaListener(private val entities: MutableMap<String, String>) : ConsumerSeekAware {

    override fun onIdleContainer(assignments: MutableMap<TopicPartition, Long>?, callback: ConsumerSeekAware.ConsumerSeekCallback?) {
    }

    override fun onPartitionsAssigned(assignments: MutableMap<TopicPartition, Long>?, callback: ConsumerSeekAware.ConsumerSeekCallback?) {
        assignments?.forEach { partition, _ -> callback?.seekToBeginning(partition.topic(), partition.partition()) }
    }

    override fun registerSeekCallback(callback: ConsumerSeekAware.ConsumerSeekCallback?) {
    }

    private val gson: Gson = Gson()
    private val log: Logger = LogManager.getLogger(javaClass)

    @KafkaListener(topics = ["uk_entities"], containerFactory = KafkaConfig.FACTORY, groupId = "spring-boot-split-api")
    fun receiveEntities(results: List<Map<String, String>>) {
        log.info("received {} items", results.size)
        for (map in results)
            entities[map["id"].toString()] = gson.toJson(map)
    }

}