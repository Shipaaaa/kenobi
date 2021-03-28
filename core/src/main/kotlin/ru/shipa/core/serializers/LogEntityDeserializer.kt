package ru.shipa.core.serializers

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.apache.kafka.common.serialization.Deserializer
import ru.shipa.core.entity.LogEntity

/**
 * LogEntity class deserializer.
 * Needed for Kafka consumer.
 *
 * @author v.shipugin
 */
class LogEntityDeserializer : Deserializer<LogEntity> {

    /**
     * Deserializing data.
     */
    override fun deserialize(topic: String?, data: ByteArray?): LogEntity {
        return Json.decodeFromString(requireNotNull(data).toString(Charsets.UTF_8))
    }
}