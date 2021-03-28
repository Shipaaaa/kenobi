package ru.shipa.kafka.producer

import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import ru.shipa.core.entity.LogEntity
import java.util.*

/**
 * Kafka producer. Sends data to Kafka.
 *
 * @author v.shipugin
 */
class KafkaLogsProducer(
    var producer: Producer<String, LogEntity>,
    val keyGenerator: () -> String
) {

    companion object {
        private const val TOPIC_NAME = "SYSLOG_TOPIC"
    }

    private val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Sending data to Kafka.
     *
     * @param data list on logs from syslog
     */
    fun sendData(data: List<String>) {
        data
            .map { LogEntity.fromLine(it) }
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