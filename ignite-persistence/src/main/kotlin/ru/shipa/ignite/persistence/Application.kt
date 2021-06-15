package ru.shipa.ignite.persistence

import org.apache.ignite.Ignition
import org.apache.ignite.cluster.ClusterState
import org.apache.ignite.configuration.IgniteConfiguration
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi
import org.apache.ignite.spi.discovery.tcp.ipfinder.kubernetes.TcpDiscoveryKubernetesIpFinder
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Application

/**
 * Ignite Persistence application entry point.
 *
 * @author v.shipugin
 * @see main
 */
fun main(args: Array<String>) {
    runApplication<Application>(*args)
}

private fun igniteTest() {
    val IGNITE_NAMESPACE = System.getenv("IGNITE_NAMESPACE") ?: "namespace"
    val IGNITE_SERVICE_NAME = System.getenv("IGNITE_SERVICE_NAME") ?: "service"
    val IGNITE_MASTER_URL = System.getenv("IGNITE_MASTER_URL") ?: "kubernetes.default.svc.cluster.local:443"

    print(IGNITE_NAMESPACE)
    print(IGNITE_SERVICE_NAME)
    print(IGNITE_MASTER_URL)

    val kuberDiscoverySpi = TcpDiscoverySpi().apply {
        ipFinder = TcpDiscoveryKubernetesIpFinder().apply {
            setNamespace(IGNITE_NAMESPACE)
            setServiceName(IGNITE_SERVICE_NAME)
            setMasterUrl(IGNITE_MASTER_URL)
        }
    }

    val igniteCfg = IgniteConfiguration().apply {
        igniteInstanceName = "TestInstanceName"
        isPeerClassLoadingEnabled = true
        isClientMode = true
        discoverySpi = kuberDiscoverySpi
    }

    val ignite = Ignition.start(igniteCfg)

    ignite.cluster().state(ClusterState.ACTIVE)
    ignite.getOrCreateCache<String, String>("test-cache")
        .put("testKey", "testValue")
}
