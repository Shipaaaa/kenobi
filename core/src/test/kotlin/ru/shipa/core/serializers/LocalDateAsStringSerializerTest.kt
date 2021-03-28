package ru.shipa.core.serializers

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import ru.shipa.core.entity.LogEntity

class LocalDateAsStringSerializerTest {

    @ParameterizedTest
    @ValueSource(
        strings = [
            "2020-05-02T11:26:14 VLAD-PC BARD_SIMPSON: <ALERT> I WILL NOT CREATE ART FROM DUNG",
            "2020-03-12T18:17:15 VLAD-PC BARD_SIMPSON: <DEBUG> I WILL NOT PLANT SUBLIMINAL MESSAGORES",
            "2020-08-28T21:09:13 VLAD-PC BARD_SIMPSON: <CRITICAL> I WILL NOT SELL MY KIDNEY ON eBAY",
            "2020-04-27T05:23:56 VLAD-PC BARD_SIMPSON: <NOTICE> I WILL NOT SELL MY KIDNEY ON eBAY",
        ]
    )
    fun `when correct log serialized - should return correct deserialized object`(logLine: String) {
        val log = LogEntity.fromLine(logLine)

        val encodeToString = Json.encodeToString(log)
        println(encodeToString)
        val bytes = encodeToString.toByteArray(Charsets.UTF_8)

        val result = Json.decodeFromString<LogEntity>(bytes.toString(Charsets.UTF_8))
        println(result)
    }
}