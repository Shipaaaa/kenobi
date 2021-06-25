package ru.shipa.ignite.server.data

import org.apache.ignite.Ignite
import org.apache.ignite.configuration.CacheConfiguration
import ru.shipa.core.entity.ImageEntity

class ImagesRepository(
    private val ignite: Ignite,
    private var imagesCacheConfiguration: CacheConfiguration<String, ImageEntity>
) {

    fun checkCache(): Boolean {
        ignite.getOrCreateCache(imagesCacheConfiguration)
        return true
    }

    fun put(imageEntity: ImageEntity) {
        ignite.getOrCreateCache(imagesCacheConfiguration).put(imageEntity.name, imageEntity)
    }

    operator fun get(id: String): ImageEntity {
        return ignite.getOrCreateCache(imagesCacheConfiguration).get(id)
    }

    fun getAll(): List<ImageEntity> {
        return ignite.getOrCreateCache(imagesCacheConfiguration)
            .map { it.value }
    }
}
