package uk.sky.poc.kafkaapi.config

import com.hazelcast.config.Config
import com.hazelcast.config.DiscoveryStrategyConfig
import com.hazelcast.core.Hazelcast
import com.hazelcast.core.HazelcastInstance
import com.hazelcast.spi.properties.GroupProperty.DISCOVERY_SPI_ENABLED
import com.hazelcast.zookeeper.ZookeeperDiscoveryProperties.*
import com.hazelcast.zookeeper.ZookeeperDiscoveryStrategyFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "hazelcast.zookeeper")
class HazelcastConfig {

    lateinit var host: String
    lateinit var port: String
    lateinit var path: String
    lateinit var groupId: String

    @Bean
    fun hzConfig(): Config {
        val config = Config()
        config.setProperty(DISCOVERY_SPI_ENABLED.name, "true")
        config.networkConfig.join.multicastConfig.isEnabled = false
        val discoveryStrategyConfig = DiscoveryStrategyConfig(ZookeeperDiscoveryStrategyFactory())
        discoveryStrategyConfig.addProperty(ZOOKEEPER_URL.key(), "$host:$port")
        discoveryStrategyConfig.addProperty(ZOOKEEPER_PATH.key(), path)
        discoveryStrategyConfig.addProperty(GROUP.key(), groupId)
        config.networkConfig.join.discoveryConfig.addDiscoveryStrategyConfig(discoveryStrategyConfig)
        return config
    }

    @Bean
    fun hzInstance(): HazelcastInstance = Hazelcast.newHazelcastInstance(hzConfig())
}