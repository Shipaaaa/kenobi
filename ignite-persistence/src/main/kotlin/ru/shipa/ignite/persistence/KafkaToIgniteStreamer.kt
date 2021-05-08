package ru.shipa.ignite.persistence

import org.apache.ignite.Ignite
import org.apache.ignite.IgniteDataStreamer
import org.apache.ignite.lang.IgniteBiTuple
import org.apache.ignite.stream.kafka.KafkaStreamer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.slf4j.LoggerFactory
import ru.shipa.core.entity.ImageEntity
import ru.shipa.core.serializers.ImageEntityDeserializer
import ru.shipa.ignite.persistence.IgnitePersistenceApp.DATA_CACHE_NAME

/**
 * Kafka consumer. Receives data from the Kafka producer.
 *
 * @author v.shipugin
 */
class KafkaToIgniteStreamer(private val ignite: Ignite) {

    companion object {
        private val BOOTSTRAP_SERVERS_IP = System.getenv("KAFKA_BOOTSTRAP_SERVERS_IP") ?: "127.0.0.1:9092"

        private const val TOPIC_NAME = "IMAGES_TOPIC"

        private const val GROUP_ID = "IMAGES_GROUP"
        private const val COMMIT_INTERVAL_MS = "1000"
    }

    /**
     * Kafka configuration definition
     */
    private val kafkaConfig = mapOf(
        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to BOOTSTRAP_SERVERS_IP,
        ConsumerConfig.GROUP_ID_CONFIG to GROUP_ID,

        ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to "true",
        ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG to COMMIT_INTERVAL_MS,

        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java.name,
        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to ImageEntityDeserializer::class.java.name
    )

    private val logger = LoggerFactory.getLogger(this::class.java)

    private lateinit var igniteDataStreamer: IgniteDataStreamer<String, ImageEntity>
    private lateinit var kafkaStreamer: KafkaStreamer<String, ImageEntity>

    /**
     * Stream initializing
     */
    fun init() {
        igniteDataStreamer = ignite.dataStreamer<String, ImageEntity>(DATA_CACHE_NAME).apply {
            allowOverwrite(true)
            autoFlushFrequency(30_000)
        }

        kafkaStreamer = KafkaStreamer<String, ImageEntity>().apply {
            ignite = this@KafkaToIgniteStreamer.ignite
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

    /**
     * Start getting logs
     */
    fun start() {
        kafkaStreamer.start()
    }

    /**
     * Stop getting logs
     */
    fun stop() {
        kafkaStreamer.stop()
        igniteDataStreamer.close()
        ignite.close()
    }
}
