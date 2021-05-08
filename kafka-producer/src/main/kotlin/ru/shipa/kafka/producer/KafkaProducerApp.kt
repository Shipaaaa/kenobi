package ru.shipa.kafka.producer

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import ru.shipa.core.entity.ImageEntity
import ru.shipa.core.serializers.ImageEntitySerializer
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

    private const val MAX_REQUEST_SIZE = 10_000_000

    private val config = mapOf(
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to BOOTSTRAP_SERVERS_IP,
        ProducerConfig.MAX_REQUEST_SIZE_CONFIG to MAX_REQUEST_SIZE,

        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java.name,
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to ImageEntitySerializer::class.java.name,
    )

    @JvmStatic
    fun main(args: Array<String>) {
        val imgDirPath = args[0]
        val data = readFiles(imgDirPath)
        val producer = KafkaProducer<String, ImageEntity>(config)

        with(KafkaImagesProducer(producer, keyGenerator = { UUID.randomUUID().toString() })) {
            sendData(data)
            stop()
        }
    }

    /**
     * Reading images from directory.
     *
     * @param imgDirPath path to directory with images
     *
     * @return sequence of files
     */
    private fun readFiles(imgDirPath: String): Sequence<File> {
        println("Reading images...")

        return File(imgDirPath)
            .walkTopDown()
            .filter { !it.isDirectory }
            .apply { forEach { file -> println("Images name: ${file.name}") } }
    }
}
