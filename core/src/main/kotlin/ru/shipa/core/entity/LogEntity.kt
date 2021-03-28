package ru.shipa.core.entity

import kotlinx.serialization.Serializable
import ru.shipa.core.serializers.LocalDateAsStringSerializer
import java.time.LocalDateTime
import java.util.*

/**
 * System log data.
 *
 * @author v.shipugin
 */
@Serializable
data class LogEntity(
    @Serializable(with = LocalDateAsStringSerializer::class)
    val dateTime: LocalDateTime,
    val host: String,
    val process: String,
    val priority: LogPriority,
    val message: String
) {

    companion object {

        /**
         * Parsing log string to object.
         *
         * Example:
         * 2020-12-27T01:36:36 VLAD-PC BARD SIMPSON: <NOTICE> I WILL NOT DANCE ON ANYONE’S GRAVE
         */
        fun fromLine(line: String): LogEntity {
            val logDates: List<String> = StringTokenizer(line).toList().map { it.toString() }

            return LogEntity(
                dateTime = LocalDateTime.parse(logDates[0]),
                host = logDates[1],
                process = logDates[2].removeSuffix(":"),
                priority = LogPriority.valueOf(line.substringAfter("<").substringBefore('>')),
                message = line.substringAfter(">")
            )
        }
    }
}

