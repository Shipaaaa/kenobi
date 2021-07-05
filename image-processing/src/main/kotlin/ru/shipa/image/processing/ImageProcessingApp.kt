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
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.apache.spark.api.java.JavaSparkContext
import ru.shipa.core.entity.ImageEntity
import ru.shipa.image.processing.ImageProcessingApp.main
import java.awt.image.BufferedImage.TYPE_INT_ARGB
import java.awt.image.BufferedImage.TYPE_INT_ARGB_PRE
import java.io.File
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
    private val IGNITE_MASTER_URL =
        System.getenv("IGNITE_MASTER_URL") ?: "https://kubernetes.default.svc.cluster.local:443"
    private val IGNITE_CACHE_NAME = System.getenv("IGNITE_CACHE_NAME") ?: "images-cache"

    private val instanceName: UUID = UUID.randomUUID()

    private val logger: Logger = Logger.getLogger("ImageProcessingApp")

    @JvmStatic
    fun main(args: Array<String>) {
        val currentTime = LocalDateTime.now()
        logger.log(Level.INFO, "[${currentTime}]: start app")

        val workDateTimeString = WORK_DATE_TIME
        val workTime = LocalDateTime.parse(workDateTimeString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        logger.log(Level.INFO, "[${workTime}]: wait until")

        val waitTime = Duration.between(currentTime, workTime).toMillis()
        logger.log(Level.INFO, "Wait millis: $waitTime")

        TimeUnit.MILLISECONDS.sleep(waitTime)
        logger.log(Level.INFO, "[${LocalDateTime.now()}]: start work")

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

                logger.log(Level.INFO, "Ignite config - kuber")
                logger.log(Level.INFO, kuberDiscoverySpi)
                discoverySpi = kuberDiscoverySpi
            }
        }
        val cache = igniteContext.fromCache(IGNITE_CACHE_NAME)

        logger.log(Level.INFO, "cache size ${cache.count()}")

        logger.log(Level.INFO, "cache foreach:")
        cache.foreach { tuple ->
            logger.log(Level.INFO, "tuple: (${tuple._1},${tuple._2.name})")
            processImage(tuple._2)
        }

        logger.log(Level.INFO, "igniteContext.close")
        igniteContext.close(true)
    }

    private fun processImage(image: ImageEntity) {
        logger.log(Level.INFO, "start image processing: ${image.key}-${image.name}")
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
            .forWriter(JpegWriter.Default).write("${image.key}-${image.name}.jpg")

        logger.log(Level.INFO, "image processing done: ${image.key}-${image.name}")
    }

    private fun localProcessing() {
        readFiles("/Users/ruasgvl/Developer/git/mephi/kenobi/image-processing/images")
            .map { ImageEntity.fromFile(UUID.randomUUID().toString(), it) }
            .forEach { processImage(it) }
    }

    private fun readFiles(imgDirPath: String): Sequence<File> {
        println("Reading images...")

        return File(imgDirPath)
            .walkTopDown()
            .filter { !it.isDirectory }
            .apply { forEach { file -> println("Images name: ${file.name}") } }
    }
}
