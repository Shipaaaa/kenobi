package ru.shipa.image.processing

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.color.Grayscale
import com.sksamuel.scrimage.composite.ColorDodgeComposite
import com.sksamuel.scrimage.filter.*
import com.sksamuel.scrimage.nio.JpegWriter
import java.awt.image.BufferedImage.TYPE_INT_ARGB
import java.awt.image.BufferedImage.TYPE_INT_ARGB_PRE

/**
 * Image to sketch sample app.
 *
 * @author v.shipugin
 * @see main
 */
object ImageProcessingApp {

    @JvmStatic
    fun main(args: Array<String>) {
        val originalImage = ImmutableImage.loader().fromResource("/img_3.png")

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
