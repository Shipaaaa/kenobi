package ru.shipa.core.serializers

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.apache.kafka.common.serialization.Serializer
import ru.shipa.core.entity.ImageEntity

/**
 * ImageEntity class serializer.
 * Needed for Kafka producer.
 *
 * @author v.shipugin
 */
class ImageEntitySerializer : Serializer<ImageEntity> {

    /**
     * Serializing data.
     */
    override fun serialize(topic: String, data: ImageEntity): ByteArray {
        return Json.encodeToString(data).toByteArray(Charsets.UTF_8)
    }
}
