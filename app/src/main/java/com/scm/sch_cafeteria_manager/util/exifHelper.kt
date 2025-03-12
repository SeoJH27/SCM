package com.scm.sch_cafeteria_manager.util

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface


fun RotateBitmap(bitmap: Bitmap, orientation: Int): Bitmap? {
    val matrix = Matrix()
    when (orientation) {
        ExifInterface.ORIENTATION_NORMAL -> return bitmap
        ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.setScale(-1F, 1F)
        ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180F)
        ExifInterface.ORIENTATION_FLIP_VERTICAL -> {
            matrix.setRotate(180F)
            matrix.postScale(-1F, 1F)
        }

        ExifInterface.ORIENTATION_TRANSPOSE -> {
            matrix.setRotate(90F)
            matrix.postScale(-1F, 1F)
        }

        ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90F)
        ExifInterface.ORIENTATION_TRANSVERSE -> {
            matrix.setRotate(-90F)
            matrix.postScale(-1F, 1F)
        }

        ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(-90F)
        else -> return bitmap
    }
    try {
        val bmRotated =
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        bitmap.recycle()
        return bmRotated
    } catch (e: OutOfMemoryError) {
        e.printStackTrace()
        return null
    }
}
