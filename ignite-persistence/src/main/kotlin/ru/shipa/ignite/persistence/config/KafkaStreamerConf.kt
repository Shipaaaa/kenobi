package ru.shipa.ignite.persistence.config

import org.apache.ignite.Ignite
import org.apache.ignite.IgniteDataStreamer
import org.apache.ignite.lang.IgniteBiTuple
import org.apache.ignite.stream.kafka.KafkaStreamer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import ru.shipa.core.entity.ImageEntity
import ru.shipa.core.serializers.ImageEntityDeserializer

@Configuration
@Import(IgniteConf::class)
class KafkaStreamerConf {

    @Value("\${ignite.service.cacheName}")
    private lateinit var cacheName: String

    @Value("\${kafka.bootstrap.serversIp}")
    private lateinit var bootstrapServersIp: String

    private val logger = LoggerFactory.getLogger(this::class.java)

    companion object {
        private const val TOPIC_NAME = "IMAGES_TOPIC"

        private const val GROUP_ID = "IMAGES_GROUP"
        private const val COMMIT_INTERVAL_MS = "1000"
    }

    /**
     * Kafka configuration definition
     */
    @Bean
    fun provideKafkaConfig(): Map<String, String> {
        return mapOf(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServersIp,
            ConsumerConfig.GROUP_ID_CONFIG to GROUP_ID,

            ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to "true",
            ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG to COMMIT_INTERVAL_MS,

            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java.name,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to ImageEntityDeserializer::class.java.name
        )
    }

    @Bean
    fun provideIgniteDataStreamer(
        ignite: Ignite
    ): IgniteDataStreamer<String, ImageEntity> {
        return ignite.dataStreamer<String, ImageEntity>(cacheName).apply {
            allowOverwrite(true)
            autoFlushFrequency(30_000)
        }
    }

    @Bean
    fun provideKafkaStreamer(
        ignite: Ignite,
        igniteDataStreamer: IgniteDataStreamer<String, ImageEntity>,
        kafkaConfig: Map<String, String>
    ): KafkaStreamer<String, ImageEntity> {
        return KafkaStreamer<String, ImageEntity>().apply {
            this.ignite = ignite
            streamer = igniteDataStreamer
            setTopic(listOf(TOPIC_NAME))
            setThreads(4)
            setConsumerConfig(kafkaConfig.toProperties())

            setSingleTupleExtractor { msg ->
                val key = msg.key() as String
                val value = msg.value() as ImageEntity
                logger.debug("record key: $key value: ${value.name}\n")

                IgniteBiTuple(key, value)
            }
        }
    }
}
