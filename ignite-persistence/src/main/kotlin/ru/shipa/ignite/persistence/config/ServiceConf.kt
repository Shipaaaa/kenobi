package ru.shipa.ignite.persistence.config

import org.apache.ignite.Ignite
import org.apache.ignite.configuration.CacheConfiguration
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import ru.shipa.core.entity.ImageEntity
import ru.shipa.ignite.persistence.data.ImagesRepository
import ru.shipa.ignite.persistence.domain.IgniteKafkaLifecycleBean
import ru.shipa.ignite.persistence.domain.ImagesInteractor
import ru.shipa.ignite.persistence.presentation.HealthController
import ru.shipa.ignite.persistence.presentation.ImagesController

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
    fun provideImagesController(imagesInteractor: ImagesInteractor): ImagesController {
        return ImagesController(imagesInteractor)
    }

    @Bean
    fun provideHealthController(imagesInteractor: ImagesInteractor): HealthController {
        return HealthController(imagesInteractor)
    }

    @Bean
    fun provideIgniteKafkaLifecycleBean(
        @Value("\${ignite.service.cacheName}") cacheName: String,
        @Value("\${kafka.bootstrap.serversIp}") bootstrapServersIp: String
    ): IgniteKafkaLifecycleBean {
        return IgniteKafkaLifecycleBean(cacheName, bootstrapServersIp)
    }
}
