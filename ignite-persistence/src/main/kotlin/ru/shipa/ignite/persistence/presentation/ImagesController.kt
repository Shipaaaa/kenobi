package ru.shipa.ignite.persistence.presentation

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.shipa.core.entity.ImageEntity
import ru.shipa.ignite.persistence.domain.ImagesInteractor

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
}
