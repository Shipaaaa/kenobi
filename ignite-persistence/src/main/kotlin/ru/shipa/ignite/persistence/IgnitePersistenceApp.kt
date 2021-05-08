package ru.shipa.ignite.persistence

import org.apache.ignite.Ignition
import org.apache.ignite.cache.CacheAtomicityMode
import org.apache.ignite.cache.CacheMode
import org.apache.ignite.cluster.ClusterState
import org.apache.ignite.configuration.CacheConfiguration
import org.apache.ignite.configuration.DataStorageConfiguration
import org.apache.ignite.configuration.IgniteConfiguration
import ru.shipa.core.entity.ImageEntity
import ru.shipa.ignite.persistence.IgnitePersistenceApp.main

/**
 * Ignite Persistence application entry point.
 *
 * @author v.shipugin
 * @see main
 */
object IgnitePersistenceApp {

    const val DATA_CACHE_NAME = "data_cache_name"

    @JvmStatic
    fun main(args: Array<String>) {

        // Apache Ignite configuration
        val igniteCfg = IgniteConfiguration().apply {
            dataStorageConfiguration = DataStorageConfiguration().apply {
                isPeerClassLoadingEnabled = true
                defaultDataRegionConfiguration.isPersistenceEnabled = true
            }
        }

        // Configuring the cache of received logs
        val logsCacheCfg = CacheConfiguration<String, ImageEntity>(DATA_CACHE_NAME).apply {
            cacheMode = CacheMode.REPLICATED
            atomicityMode = CacheAtomicityMode.ATOMIC
        }

        // Launching Apache Ignite
        val ignite = Ignition.start(igniteCfg).apply {
            cluster().state(ClusterState.ACTIVE)
            getOrCreateCache(logsCacheCfg)
        }

        with(KafkaToIgniteStreamer(ignite)) {
            init()
            start()
        }
    }
}
