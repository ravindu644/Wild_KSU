package com.rifsxd.ksunext.ui.util

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.net.Uri
import android.util.Log
import androidx.compose.ui.graphics.toArgb
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.rifsxd.ksunext.ui.component.ImageCropSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ImageCropUtils {
    private const val TAG = "ImageCropUtils"
    
    fun saveImageCropSettings(prefs: SharedPreferences, uri: String, settings: ImageCropSettings) {
        prefs.edit().apply {
            putString("background_image_uri", uri)
            putFloat("background_crop_scale", settings.scale)
            putFloat("background_crop_offset_x", settings.offsetX)
            putFloat("background_crop_offset_y", settings.offsetY)
            apply()
        }
        Log.d(TAG, "Saved crop settings: scale=${settings.scale}, offsetX=${settings.offsetX}, offsetY=${settings.offsetY}")
    }
    
    fun loadImageCropSettings(prefs: SharedPreferences): ImageCropSettings {
        return ImageCropSettings(
            scale = prefs.getFloat("background_crop_scale", 1f),
            offsetX = prefs.getFloat("background_crop_offset_x", 0f),
            offsetY = prefs.getFloat("background_crop_offset_y", 0f)
        )
    }
    
    suspend fun loadImageBitmap(context: Context, uri: Uri): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                val imageLoader = ImageLoader(context)
                val request = ImageRequest.Builder(context)
                    .data(uri)
                    .build()
                
                val result = imageLoader.execute(request)
                if (result is SuccessResult) {
                    result.drawable.let { drawable ->
                        val bitmap = Bitmap.createBitmap(
                            drawable.intrinsicWidth,
                            drawable.intrinsicHeight,
                            Bitmap.Config.ARGB_8888
                        )
                        val canvas = Canvas(bitmap)
                        drawable.setBounds(0, 0, canvas.width, canvas.height)
                        drawable.draw(canvas)
                        bitmap
                    }
                } else {
                    Log.e(TAG, "Failed to load image from URI: $uri")
                    null
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading image bitmap", e)
                null
            }
        }
    }
    
    fun applyCropTransformation(
        bitmap: Bitmap,
        settings: ImageCropSettings,
        targetWidth: Int,
        targetHeight: Int
    ): Bitmap {
        try {
            val matrix = Matrix().apply {
                postScale(settings.scale, settings.scale)
                postTranslate(settings.offsetX, settings.offsetY)
            }
            
            val croppedBitmap = Bitmap.createBitmap(
                targetWidth,
                targetHeight,
                Bitmap.Config.ARGB_8888
            )
            
            val canvas = Canvas(croppedBitmap)
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            canvas.drawBitmap(bitmap, matrix, paint)
            
            Log.d(TAG, "Applied crop transformation: scale=${settings.scale}, offsetX=${settings.offsetX}, offsetY=${settings.offsetY}")
            return croppedBitmap
        } catch (e: Exception) {
            Log.e(TAG, "Error applying crop transformation", e)
            return bitmap
        }
    }
}