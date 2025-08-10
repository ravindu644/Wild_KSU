package com.rifsxd.ksunext.ui.util

import android.content.Context
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object ImageStorageUtils {
    private const val TAG = "ImageStorageUtils"
    private const val BACKGROUND_IMAGE_FILENAME = "background_image.jpg"
    
    /**
     * Copy an image from URI to internal storage
     * @param context Application context
     * @param sourceUri URI of the image to copy
     * @return File path of the copied image, or null if failed
     */
    fun copyImageToInternalStorage(context: Context, sourceUri: Uri): String? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(sourceUri)
            if (inputStream == null) {
                Log.e(TAG, "Failed to open input stream for URI: $sourceUri")
                return null
            }
            
            // Create internal storage directory for images
            val imagesDir = File(context.filesDir, "images")
            if (!imagesDir.exists()) {
                imagesDir.mkdirs()
            }
            
            // Create the destination file
            val destinationFile = File(imagesDir, BACKGROUND_IMAGE_FILENAME)
            
            // Copy the image
            val outputStream = FileOutputStream(destinationFile)
            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            
            Log.d(TAG, "Successfully copied image to: ${destinationFile.absolutePath}")
            return destinationFile.absolutePath
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to copy image to internal storage", e)
            null
        }
    }
    
    /**
     * Get the internal storage path for background image
     * @param context Application context
     * @return File path if exists, null otherwise
     */
    fun getInternalBackgroundImagePath(context: Context): String? {
        val imagesDir = File(context.filesDir, "images")
        val backgroundFile = File(imagesDir, BACKGROUND_IMAGE_FILENAME)
        
        return if (backgroundFile.exists()) {
            backgroundFile.absolutePath
        } else {
            null
        }
    }
    
    /**
     * Delete the internal background image
     * @param context Application context
     * @return true if deleted successfully, false otherwise
     */
    fun deleteInternalBackgroundImage(context: Context): Boolean {
        return try {
            val imagesDir = File(context.filesDir, "images")
            val backgroundFile = File(imagesDir, BACKGROUND_IMAGE_FILENAME)
            
            if (backgroundFile.exists()) {
                val deleted = backgroundFile.delete()
                Log.d(TAG, "Background image deleted: $deleted")
                deleted
            } else {
                Log.d(TAG, "Background image file does not exist")
                true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete background image", e)
            false
        }
    }
    
    /**
     * Convert file path to file:// URI string
     * @param filePath Absolute file path
     * @return file:// URI string
     */
    fun filePathToUri(filePath: String): String {
        return "file://$filePath"
    }
}