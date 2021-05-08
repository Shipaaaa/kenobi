package ru.shipa.core.serializers

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.apache.kafka.common.serialization.Deserializer
import ru.shipa.core.entity.ImageEntity

/**
 * ImageEntity class deserializer.
 * Needed for Kafka consumer.
 *
 * @author v.shipugin
 */
class ImageEntityDeserializer : Deserializer<ImageEntity> {

    /**
     * Deserializing data.
     */
    override fun deserialize(topic: String, data: ByteArray): ImageEntity {
        return Json.decodeFromString(data.toString(Charsets.UTF_8))
    }
}
