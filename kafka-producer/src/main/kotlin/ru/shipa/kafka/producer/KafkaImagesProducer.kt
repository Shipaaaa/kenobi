package ru.shipa.kafka.producer

import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import ru.shipa.core.entity.ImageEntity
import java.io.File

/**
 * Kafka producer. Sends data to Kafka.
 *
 * @author v.shipugin
 */
class KafkaImagesProducer(
    var producer: Producer<String, ImageEntity>,
    val keyGenerator: () -> String
) {

    companion object {
        private const val TOPIC_NAME = "IMAGES_TOPIC"
    }

    private val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Sending data to Kafka.
     *
     * @param data list on logs from syslog
     */
    fun sendData(data: Sequence<File>) {
        data
            .map { ImageEntity.fromFile(it) }
            .map { ProducerRecord(TOPIC_NAME, keyGenerator(), it) }
            .forEach { record ->
                producer.send(record) { metadata, exception ->
                    metadata?.let { logger.debug("Message has been sent to topic: ${metadata.topic()}") }
                    exception?.let { logger.error(exception.message, exception) }
                }
            }
    }

    /**
     * Finishing data producer.
     */
    fun stop() {
        producer.close()
    }
}
