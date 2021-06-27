package ru.shipa.core.entity

import kotlinx.serialization.Serializable
import java.io.File
import java.util.*

/**
 * Image data.
 *
 * @author v.shipugin
 */
@Serializable
data class ImageEntity(
    val key: String,
    val name: String,
    val encodedImageBlob: String
) {

    companion object {
        /**
         * Parsing image to object.
         */
        fun fromFile(key: String, file: File): ImageEntity {
            return ImageEntity(
                key = key,
                name = file.name,
                encodedImageBlob = Base64.getEncoder().encodeToString(file.readBytes())
            )
        }
    }
}

