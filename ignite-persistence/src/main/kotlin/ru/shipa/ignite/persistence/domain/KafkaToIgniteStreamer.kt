package ru.shipa.ignite.persistence.domain

import org.apache.ignite.Ignite
import org.apache.ignite.IgniteDataStreamer
import org.apache.ignite.stream.kafka.KafkaStreamer
import org.springframework.context.event.ContextStartedEvent
import org.springframework.context.event.ContextStoppedEvent
import org.springframework.context.event.EventListener
import ru.shipa.core.entity.ImageEntity

/**
 * Kafka consumer. Receives data from the Kafka producer.
 *
 * @author v.shipugin
 */
class KafkaToIgniteStreamer(
    private val ignite: Ignite,
    private val kafkaStreamer: KafkaStreamer<String, ImageEntity>,
    private val igniteDataStreamer: IgniteDataStreamer<String, ImageEntity>
) {

    /**
     * Start getting images
     */
    @EventListener(classes = [ContextStartedEvent::class])
    fun handleContextStartedEvent() {
        kafkaStreamer.start()
    }

    /**
     * Stop getting images
     */
    @EventListener(classes = [ContextStoppedEvent::class])
    fun handleContextStoppedEvent() {
        kafkaStreamer.stop()
        igniteDataStreamer.close()
        ignite.close()
    }
}
