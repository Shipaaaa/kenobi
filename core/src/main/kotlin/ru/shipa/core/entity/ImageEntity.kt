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
    val name: String,
    val encodedImageBlob: String
) {

    companion object {
        /**
         * Parsing image to object.
         */
        fun fromFile(file: File): ImageEntity {
            return ImageEntity(
                name = file.name,
                encodedImageBlob = Base64.getEncoder().encodeToString(file.readBytes())
            )
        }
    }
}

