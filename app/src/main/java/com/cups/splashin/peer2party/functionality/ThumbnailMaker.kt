package com.cups.splashin.peer2party.functionality

import android.graphics.Bitmap
import kotlin.math.min

fun makeThumbnail(
    screenW: Int,
    screenH: Int,
    imageWidth_: Int,
    imageHeight_: Int,
    image_: Bitmap
): Bitmap {
    var imageWidth = imageWidth_
    var imageHeight = imageHeight_
    var image = image_

    val scaleX: Double = screenW.toDouble() / imageWidth
    val scaleY: Double = screenH.toDouble() / imageHeight
    val scale = min(scaleX, scaleY)

    if (imageWidth >= screenW / 2) {
        //for downscale:
        imageWidth = (imageWidth * scale / 1.5).toInt()
        imageHeight = (imageHeight * scale / 1.5).toInt()
        image = Bitmap.createScaledBitmap(
            image, imageWidth, imageHeight, false
        )
    } else if (imageWidth < screenW / 2) {
        //for upscale:
        imageWidth = (imageWidth / 1.5 * scale).toInt()
        imageHeight = (imageHeight / 1.5 * scale).toInt()
        image = Bitmap.createScaledBitmap(
            image, imageWidth, imageHeight, false
        )
    }

    return image
}