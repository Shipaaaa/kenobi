package ru.shipa.ignite.server.domain

import ru.shipa.core.entity.ImageEntity
import ru.shipa.ignite.server.data.ImagesRepository

class ImagesInteractor(private val imagesRepository: ImagesRepository) {

    fun checkCache(): Boolean = imagesRepository.checkCache()

    fun putImage(image: ImageEntity): ImageEntity {
        imagesRepository.put(image)
        return image
    }

    fun getImage(name: String): ImageEntity = imagesRepository[name]

    fun getAllImages(): List<ImageEntity> = imagesRepository.getAll()
}
