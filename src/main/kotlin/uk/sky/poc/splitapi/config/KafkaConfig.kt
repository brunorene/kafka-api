package uk.sky.poc.splitapi.config

import org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG
import org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG
import org.apache.kafka.clients.producer.ProducerConfig.*
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.config.KafkaListenerContainerFactory
import org.springframework.kafka.core.*
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer
import uk.sky.poc.splitapi.support.MapDeserializer
import uk.sky.poc.splitapi.support.MapSerializer

const val ID = "id"
const val FACTORY = "kafkaListenerContainerFactory"
const val ENTITIES = "uk_entities"

@Configuration
@ConfigurationProperties(prefix = "kafka")
@EnableKafka
class KafkaConfig {

    lateinit var host: String
    lateinit var port: String

    // PRODUCER

    @Bean
    fun producerFactory(): ProducerFactory<String, Map<String, Any>> = DefaultKafkaProducerFactory(producerConfigs())

    @Bean
    fun producerConfigs(): Map<String, Any> = mapOf(
            BOOTSTRAP_SERVERS_CONFIG to "$host:$port",
            KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            VALUE_SERIALIZER_CLASS_CONFIG to MapSerializer::class.java)

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, Map<String, Any>> = KafkaTemplate(producerFactory())

    // CONSUMER

    @Bean
    fun kafkaListenerContainerFactory(): KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, Map<String, Any>>> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, Map<String, Any>>()
        factory.consumerFactory = consumerFactory()
        factory.setConcurrency(2)
        factory.containerProperties.pollTimeout = 3000
        factory.isBatchListener = true
        return factory
    }

    @Bean
    fun consumerFactory(): ConsumerFactory<String, Map<String, Any>> {
        return DefaultKafkaConsumerFactory<String, Map<String, Any>>(consumerConfigs())
    }

    @Bean
    fun consumerConfigs() = mapOf(BOOTSTRAP_SERVERS_CONFIG to "$host:$port",
            KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            VALUE_DESERIALIZER_CLASS_CONFIG to MapDeserializer::class.java)

}