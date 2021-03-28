package ru.shipa.kafka.producer

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import ru.shipa.core.entity.LogEntity
import ru.shipa.core.serializers.LogEntitySerializer
import ru.shipa.kafka.producer.KafkaProducerApp.main
import java.io.File
import java.util.*


/**
 * Kafka application entry point.
 *
 * @author v.shipugin
 * @see main
 */
object KafkaProducerApp {

    private val BOOTSTRAP_SERVERS_IP = System.getenv("KAFKA_BOOTSTRAP_SERVERS_IP") ?: "127.0.0.1:9092"

    private val config = mapOf(
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to BOOTSTRAP_SERVERS_IP,

        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java.name,
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to LogEntitySerializer::class.java.name,
    )

    @JvmStatic
    fun main(args: Array<String>) {
        val sysLogsPath = args[0]
        val data = readFile(sysLogsPath)
        val producer = KafkaProducer<String, LogEntity>(config)

        with(KafkaLogsProducer(producer, keyGenerator = { UUID.randomUUID().toString() })) {
            sendData(data)
            stop()
        }
    }

    /**
     * Reading logs from file
     *
     * @param sysLogsPath path to file with logs
     *
     * @return list of log lines
     */
    private fun readFile(sysLogsPath: String): List<String> {
        println("Reading file...")

        return File(sysLogsPath)
            .useLines { it.toList() }
            .also { sysLogs -> println("File:\n${sysLogs.firstOrNull()}\n...\n${sysLogs.lastOrNull()}\n") }
    }
}