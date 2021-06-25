package ru.shipa.image.processing

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.composite.ColorDodgeComposite
import com.sksamuel.scrimage.filter.GrayscaleFilter
import com.sksamuel.scrimage.filter.InvertFilter
import com.sksamuel.scrimage.filter.LensBlurFilter
import com.sksamuel.scrimage.nio.JpegWriter
import org.apache.ignite.configuration.IgniteConfiguration
import org.apache.ignite.spark.JavaIgniteContext
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi
import org.apache.ignite.spi.discovery.tcp.ipfinder.kubernetes.TcpDiscoveryKubernetesIpFinder
import org.apache.spark.api.java.JavaSparkContext
import ru.shipa.core.entity.ImageEntity
import ru.shipa.image.processing.ImageProcessingApp.main
import java.awt.image.BufferedImage.TYPE_INT_ARGB
import java.awt.image.BufferedImage.TYPE_INT_ARGB_PRE
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Image to sketch sample app.
 *
 * @author v.shipugin
 * @see main
 */
object ImageProcessingApp {

    // Format example 2021-06-16T22:28:27.415
    private val WORK_DATE_TIME = System.getenv("WORK_DATE_TIME")
    private val IGNITE_NAMESPACE = System.getenv("IGNITE_NAMESPACE") ?: "ignite"
    private val IGNITE_SERVICE_NAME = System.getenv("IGNITE_SERVICE_NAME") ?: "ignite-cluster"
    private val IGNITE_MASTER_URL = System.getenv("IGNITE_MASTER_URL") ?: "https://kubernetes.default.svc.cluster.local:443"
    private val IGNITE_CACHE_NAME = System.getenv("IGNITE_CACHE_NAME") ?: "images-cache"

    private val instanceName: UUID = UUID.randomUUID()

    @JvmStatic
    fun main(args: Array<String>) {
        val currentTime = LocalDateTime.now()
        println("[${currentTime}]: start app")

        val workDateTimeString = WORK_DATE_TIME
        val workTime = LocalDateTime.parse(workDateTimeString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        println("[${workTime}]: wait until")

        val waitTime = Duration.between(currentTime, workTime).toMillis()
        println("Wait millis: $waitTime")

        TimeUnit.MILLISECONDS.sleep(waitTime)
        println("[${LocalDateTime.now()}]: start work")

        startSpark()
    }

    private fun startSpark() {
        val sparkContext = JavaSparkContext()
        val igniteContext = JavaIgniteContext<String, ImageEntity>(sparkContext) {
            val kuberDiscoverySpi = TcpDiscoverySpi().apply {
                ipFinder = TcpDiscoveryKubernetesIpFinder().apply {
                    setServiceName(IGNITE_SERVICE_NAME)
                    setNamespace(IGNITE_NAMESPACE)
                    setMasterUrl(IGNITE_MASTER_URL)
                }
            }

            IgniteConfiguration().apply {
                igniteInstanceName = instanceName.toString()
                isPeerClassLoadingEnabled = true
                isClientMode = true

                println("Ignite config - kuber")
                println(kuberDiscoverySpi)
                discoverySpi = kuberDiscoverySpi
            }
        }
        val cache = igniteContext.fromCache(IGNITE_CACHE_NAME)

        println("cache foreach:")
        cache.foreach { tuple ->
            println("tuple: (${tuple._1},${tuple._2.name})")
            processImage(tuple._2)
        }

        igniteContext.close(true)
    }

    private fun processImage(image: ImageEntity) {
        val decodedImage = Base64.getDecoder().decode(image.encodedImageBlob)
        val originalImage = ImmutableImage.loader().fromBytes(decodedImage)

        val grayImage = originalImage.copy(TYPE_INT_ARGB_PRE)
            .filter(GrayscaleFilter())

        val blurImage = grayImage.copy()
            .filter(InvertFilter())
            .filter(LensBlurFilter(20F, 50F, 255F, 5))

        grayImage
            .composite(ColorDodgeComposite(.9), blurImage)
            .copy(TYPE_INT_ARGB)
            .forWriter(JpegWriter.Default).write("contrast_2.0.jpg")
    }
}
