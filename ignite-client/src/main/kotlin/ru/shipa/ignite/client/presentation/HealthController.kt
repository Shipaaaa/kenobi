package ru.shipa.ignite.client.presentation

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import ru.shipa.ignite.client.domain.ImagesInteractor
import ru.shipa.ignite.client.domain.model.HealthEntity

@RequestMapping
class HealthController(private val imagesInteractor: ImagesInteractor) {

    @GetMapping(path = ["/health"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getHealth(): ResponseEntity<HealthEntity> {
        imagesInteractor.checkCache()
        return ResponseEntity(HealthEntity(), HttpStatus.OK)
    }
}
