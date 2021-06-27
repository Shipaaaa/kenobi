package ru.shipa.ignite.client.presentation

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.shipa.core.entity.ImageEntity
import ru.shipa.ignite.client.domain.ImagesInteractor

@RequestMapping("image")
class ImagesController(private val imagesInteractor: ImagesInteractor) {

    @PostMapping(
        path = ["/put"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun putImage(@RequestBody image: ImageEntity?): ResponseEntity<ImageEntity> {
        val imageEntity = imagesInteractor.putImage(requireNotNull(image))
        return ResponseEntity(imageEntity, HttpStatus.OK)
    }

    @GetMapping(
        path = ["/get/{name}"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getImage(@PathVariable name: String?): ResponseEntity<ImageEntity> {
        val imageEntity = imagesInteractor.getImage(requireNotNull(name))
        return ResponseEntity(imageEntity, HttpStatus.OK)
    }

    @GetMapping(
        path = ["/get/all"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getAllImages(): ResponseEntity<List<ImageEntity>> {
        val imageEntities = imagesInteractor.getAllImages()
        return ResponseEntity(imageEntities, HttpStatus.OK)
    }

    @GetMapping(
        path = ["/get/all/meta"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getAllImageMeta(): ResponseEntity<List<ImageEntity>> {
        val imageNames = imagesInteractor.getAllImages().map { imageEntity ->
            val shortBlob = imageEntity.encodedImageBlob.ellipsizeMiddle()
            imageEntity.copy(encodedImageBlob = shortBlob)
        }
        return ResponseEntity(imageNames, HttpStatus.OK)
    }

    private fun String.ellipsizeMiddle(length: Int = 50): String {
        return if (this.length > length) "${take(length / 2)}...${takeLast(length / 2)}" else this
    }
}

