package ru.shipa.core.serializers

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.apache.kafka.common.serialization.Serializer
import ru.shipa.core.entity.LogEntity

/**
 * LogEntity class serializer.
 * Needed for Kafka producer.
 *
 * @author v.shipugin
 */
class LogEntitySerializer : Serializer<LogEntity> {

    /**
     * Serializing data.
     */
    override fun serialize(topic: String, data: LogEntity?): ByteArray {
        return Json.encodeToString(data).toByteArray(Charsets.UTF_8)
    }
}