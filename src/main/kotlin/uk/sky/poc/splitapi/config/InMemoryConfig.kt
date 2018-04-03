package uk.sky.poc.splitapi.config

import org.mapdb.DBMaker
import org.mapdb.Serializer.STRING
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

//const val ENTITIES = "entities"

@Configuration
//@ConfigurationProperties(prefix = "inmemory")
class InMemoryConfig {

    //    lateinit var host: String
//    lateinit var port: String
//    lateinit var path: String
//    lateinit var groupId: String
//
//    @Bean
//    fun hzConfig(): Config {
//        val config = Config()
//        config.setProperty(DISCOVERY_SPI_ENABLED.name, "true")
//        config.networkConfig.join.multicastConfig.isEnabled = false
//        val discoveryStrategyConfig = DiscoveryStrategyConfig(ZookeeperDiscoveryStrategyFactory())
//        discoveryStrategyConfig.addProperty(ZOOKEEPER_URL.key(), "$host:$port")
//        discoveryStrategyConfig.addProperty(ZOOKEEPER_PATH.key(), path)
//        discoveryStrategyConfig.addProperty(GROUP.key(), groupId)
//        config.networkConfig.join.discoveryConfig.addDiscoveryStrategyConfig(discoveryStrategyConfig)
//        return config
//    }
//
//    @Bean
//    fun hzInstance(): HazelcastInstance = Hazelcast.newHazelcastInstance(hzConfig())
//
//    @Bean
//    fun entities(): Map<String, String> = hzInstance().getMap<String, String>(ENTITIES)
    @Bean
    fun entities(): Map<String, String> = DBMaker
            .memoryShardedHashMap(3)
            .counterEnable()
            .keySerializer(STRING)
            .valueSerializer(STRING)
            .create()
}