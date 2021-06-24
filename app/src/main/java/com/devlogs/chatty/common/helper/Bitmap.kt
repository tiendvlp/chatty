package com.devlogs.chatty.common.helper

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas

import android.graphics.drawable.BitmapDrawable

import android.graphics.drawable.Drawable
import com.devlogs.chatty.common.getFullDownloadAvatarUrl
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.URL


fun drawableToBitmap(drawable: Drawable): Bitmap? {
    var bitmap: Bitmap? = null
    if (drawable is BitmapDrawable) {
        val bitmapDrawable = drawable
        if (bitmapDrawable.bitmap != null) {
            return bitmapDrawable.bitmap
        }
    }
    bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
        Bitmap.createBitmap(
            1,
            1,
            Bitmap.Config.ARGB_8888
        ) // Single color bitmap will be created of 1x1 pixel
    } else {
        Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
    }
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight())
    drawable.draw(canvas)
    return bitmap
}

fun URL.toBitmap(): Bitmap?{
    return try {
        BitmapFactory.decodeStream(openStream())
    }catch (e: IOException){
        null
    }
}

@Throws(IOException::class)
 fun getImageBytes(imageUrl: String): ByteArray {
    val url = URL(imageUrl)
    val output = ByteArrayOutputStream()
    url.openStream().use { stream ->
        val buffer = ByteArray(4096)
        while (true) {
            val bytesRead = stream.read(buffer)
            if (bytesRead < 0) {
                break
            }
            output.write(buffer, 0, bytesRead)
        }
    }
    return output.toByteArray()
}

fun getUserAvatar ( email : String) : ByteArray {
    return getImageBytes(getFullDownloadAvatarUrl(email))
}

fun ByteArray.toBitmap () : Bitmap {
    return BitmapFactory.decodeByteArray(this, 0, size, BitmapFactory.Options())
}