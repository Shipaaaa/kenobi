package ru.shipa.ignite.server.config

import org.apache.ignite.Ignite
import org.apache.ignite.IgniteSpringBean
import org.apache.ignite.cache.CacheAtomicityMode
import org.apache.ignite.cache.CacheMode
import org.apache.ignite.configuration.CacheConfiguration
import org.apache.ignite.configuration.DataStorageConfiguration
import org.apache.ignite.configuration.IgniteConfiguration
import org.apache.ignite.spi.discovery.DiscoverySpi
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi
import org.apache.ignite.spi.discovery.tcp.ipfinder.TcpDiscoveryIpFinder
import org.apache.ignite.spi.discovery.tcp.ipfinder.kubernetes.TcpDiscoveryKubernetesIpFinder
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.shipa.core.entity.ImageEntity
import java.util.*

@Configuration
class IgniteConf {

    companion object {
        val instanceName: UUID = UUID.randomUUID()
    }

    @Value("\${ignite.service.localAddr:127.0.0.1}")
    private lateinit var localAddr: String

    @Value("\${ignite.service.ipFinderAddresses:dummy}")
    private lateinit var ipFinderAddresses: String

    @Value("\${ignite.service.clientMode:false}")
    private var clientMode: Boolean = false

    @Value("\${ignite.service.kuberMode:false}")
    private var kuberMode: Boolean = false

    @Value("\${ignite.service.serviceName}")
    private lateinit var serviceName: String

    @Value("\${ignite.service.namespace}")
    private lateinit var namespace: String

    @Value("\${ignite.service.masterUrl}")
    private lateinit var masterUrl: String

    @Value("\${ignite.service.cacheName}")
    private lateinit var cacheName: String

    @Bean
    fun provideTcpDiscoveryIpFinder(): TcpDiscoveryIpFinder {
        return TcpDiscoveryMulticastIpFinder().apply {
            localAddress = localAddr
            if ("dummy" != ipFinderAddresses) setAddresses(listOf(ipFinderAddresses))
        }
    }

    @Bean
    @Qualifier("LocalDiscoverySpi")
    fun provideLocalDiscoverySpi(tcpDiscoveryIpFinder: TcpDiscoveryIpFinder): DiscoverySpi {
        return TcpDiscoverySpi().apply {
            localAddress = localAddr
            ipFinder = tcpDiscoveryIpFinder
        }
    }

    @Bean
    @Qualifier("KubernetesDiscoverySpi")
    fun provideKuberDiscoverySpi(): DiscoverySpi {
        return TcpDiscoverySpi().apply {
            ipFinder = TcpDiscoveryKubernetesIpFinder().apply {
                setServiceName(serviceName)
                setNamespace(namespace)
                setMasterUrl(masterUrl)
            }
        }
    }

    @Bean
    fun provideIgniteConfiguration(
        @Qualifier("LocalDiscoverySpi") localDiscoverySpi: DiscoverySpi,
        @Qualifier("KubernetesDiscoverySpi") kuberDiscoverySpi: DiscoverySpi,
    ): IgniteConfiguration {

        return IgniteConfiguration().apply {
            igniteInstanceName = instanceName.toString()
            isPeerClassLoadingEnabled = true
            /* dataStorageConfiguration = DataStorageConfiguration().apply {
                defaultDataRegionConfiguration.isPersistenceEnabled = true
            }*/

            discoverySpi = if (kuberMode) {
                println("Ignite config - kuber")
                println(kuberDiscoverySpi)
                kuberDiscoverySpi
            } else {
                println("Ignite config - local")
                println(localDiscoverySpi)
                localDiscoverySpi
            }
        }
    }

    @Bean
    fun provideIgniteImagesCacheConfiguration(): CacheConfiguration<String, ImageEntity> {
        return CacheConfiguration<String, ImageEntity>().apply {
            name = cacheName
            cacheMode = CacheMode.PARTITIONED
            atomicityMode = CacheAtomicityMode.ATOMIC
            backups = 1
            setIndexedTypes(String::class.java, ImageEntity::class.java)
        }
    }

    @Bean
    fun provideIgnite(configuration: IgniteConfiguration): Ignite {
        return IgniteSpringBean().apply {
            setConfiguration(configuration)
        }
    }
}
