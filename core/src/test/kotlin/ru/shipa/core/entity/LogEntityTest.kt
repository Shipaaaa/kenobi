package ru.shipa.core.entity

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.assertFails

class LogEntityTest {

    @ParameterizedTest
    @ValueSource(
        strings = [
            "2020-05-02T11:26:14 VLAD-PC BARD_SIMPSON: <ALERT> I WILL NOT CREATE ART FROM DUNG",
            "2020-03-12T18:17:15 VLAD-PC BARD_SIMPSON: <DEBUG> I WILL NOT PLANT SUBLIMINAL MESSAGORES",
            "2020-08-28T21:09:13 VLAD-PC BARD_SIMPSON: <CRITICAL> I WILL NOT SELL MY KIDNEY ON eBAY",
            "2020-04-27T05:23:56 VLAD-PC BARD_SIMPSON: <NOTICE> I WILL NOT SELL MY KIDNEY ON eBAY",
        ]
    )
    fun `when correct log parsed - should return correct LogEntity`(logLine: String) {
        LogEntity.fromLine(logLine)
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "2020-05-02T11:26:14 VLAD-PC BARD_SIMPSON: <ALEfRT> I WILL NOT CREATE ART FROM DUNG",
            "2020-0312T18:17:15 VLAD-PC BARD_SIMPSON: <DEhgBUG> I WILL NOT PLANT SUBLIMINAL MESSAGORES",
            "2020-08-28T21:09:13 VLAD -PC BARD_SIMPSON: <CRsITICAL> I WILL NOT SELL MY KIDNEY ON eBAY",
            "2s020-04-27T05:23:56 VLAD-PC BARD SIMPSON: <NOTIfCE> I WILL NOT SELL MY KIDNEY ON eBAY",
            ""
        ]
    )
    fun `when wrong log parsed - should throw exception`(logLine: String) {
        assertFails { LogEntity.fromLine(logLine) }
    }
}