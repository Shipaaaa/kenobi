package ru.shipa.ignite.server.presentation

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import ru.shipa.ignite.server.domain.ImagesInteractor
import ru.shipa.ignite.server.domain.model.HealthEntity

@RequestMapping
class HealthController(private val imagesInteractor: ImagesInteractor) {

    @GetMapping(path = ["/health"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getHealth(): ResponseEntity<HealthEntity> {
        imagesInteractor.checkCache()
        return ResponseEntity(HealthEntity(), HttpStatus.OK)
    }
}
