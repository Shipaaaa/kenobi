package ru.shipa.ignite.client.domain

import org.apache.ignite.Ignite
import org.apache.ignite.IgniteDataStreamer
import org.apache.ignite.lang.IgniteBiTuple
import org.apache.ignite.lifecycle.LifecycleBean
import org.apache.ignite.lifecycle.LifecycleEventType
import org.apache.ignite.lifecycle.LifecycleEventType.AFTER_NODE_START
import org.apache.ignite.lifecycle.LifecycleEventType.BEFORE_NODE_STOP
import org.apache.ignite.resources.IgniteInstanceResource
import org.apache.ignite.stream.kafka.KafkaStreamer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.slf4j.LoggerFactory
import ru.shipa.core.entity.ImageEntity
import ru.shipa.core.serializers.ImageEntityDeserializer

/**
 * Kafka consumer. Receives data from the Kafka producer.
 *
 * @author v.shipugin
 */
class IgniteKafkaLifecycleBean(
    private val cacheName: String,
    private val bootstrapServersIp: String
) : LifecycleBean {

    companion object {
        private const val TOPIC_NAME = "IMAGES_TOPIC"

        private const val GROUP_ID = "IMAGES_GROUP"
        private const val COMMIT_INTERVAL_MS = "1000"
    }

    @IgniteInstanceResource
    private lateinit var ignite: Ignite

    /**
     * Kafka configuration definition
     */
    private val kafkaConfig = mapOf(
        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServersIp,
        ConsumerConfig.GROUP_ID_CONFIG to GROUP_ID,

        ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to "true",
        ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG to COMMIT_INTERVAL_MS,

        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java.name,
        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to ImageEntityDeserializer::class.java.name
    )

    private val logger = LoggerFactory.getLogger(this::class.java)

    private lateinit var igniteDataStreamer: IgniteDataStreamer<String, ImageEntity>
    private lateinit var kafkaStreamer: KafkaStreamer<String, ImageEntity>

    @Suppress("NON_EXHAUSTIVE_WHEN")
    override fun onLifecycleEvent(eventType: LifecycleEventType) {
        when (eventType) {
            AFTER_NODE_START -> {
                initKafkaToIgniteStreamer()
                kafkaStreamer.start()
            }
            BEFORE_NODE_STOP -> {
                kafkaStreamer.stop()
                igniteDataStreamer.close()
            }
        }
    }

    private fun initKafkaToIgniteStreamer() {
        igniteDataStreamer = ignite.dataStreamer<String, ImageEntity>(cacheName).apply {
            allowOverwrite(true)
            autoFlushFrequency(30_000)
        }

        kafkaStreamer = KafkaStreamer<String, ImageEntity>().apply {
            ignite = this@IgniteKafkaLifecycleBean.ignite
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
