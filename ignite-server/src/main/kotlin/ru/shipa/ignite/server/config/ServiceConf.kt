package ru.shipa.ignite.server.config

import org.apache.ignite.Ignite
import org.apache.ignite.configuration.CacheConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import ru.shipa.core.entity.ImageEntity
import ru.shipa.ignite.server.data.ImagesRepository
import ru.shipa.ignite.server.domain.ImagesInteractor
import ru.shipa.ignite.server.presentation.HealthController

@Configuration
@Import(IgniteConf::class)
class ServiceConf {

    @Bean
    fun provideImagesRepository(
        ignite: Ignite,
        imagesCacheConf: CacheConfiguration<String, ImageEntity>
    ): ImagesRepository {
        return ImagesRepository(ignite, imagesCacheConf)
    }

    @Bean
    fun provideImagesInteractor(imagesRepository: ImagesRepository): ImagesInteractor {
        return ImagesInteractor(imagesRepository)
    }

    @Bean
    fun provideHealthController(imagesInteractor: ImagesInteractor): HealthController {
        return HealthController(imagesInteractor)
    }
}
