package com.example.nuberjam.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

object PhotoLoaderManager {
    private const val IMAGE_MAX_SIZE = 1000000

    private const val PHOTOS_DIR = "photos"
    private const val FILE_PROVIDER = "fileprovider"

    fun buildNewUri(context: Context): Uri {
        val photosDir = File(context.cacheDir, PHOTOS_DIR)
        photosDir.mkdirs()
        val photoFile = File(photosDir, generateFilename())
        val authority = "${context.packageName}.$FILE_PROVIDER"
        return FileProvider.getUriForFile(context, authority, photoFile)
    }

    private fun generateFilename() = "${System.currentTimeMillis()}.jpg"

    fun uriToFile(uri: Uri, context: Context): File? {
        context.contentResolver.openInputStream(uri)?.use { input ->
            val cachedPhoto = File(
                File(context.cacheDir, PHOTOS_DIR).also { it.mkdir() },
                generateFilename()
            )

            cachedPhoto.outputStream().use { output ->
                input.copyTo(output)
                val file = cachedPhoto.copyTo(
                    File(
                        File(context.filesDir, PHOTOS_DIR).also { it.mkdir() },
                        generateFilename()
                    )
                )

                return file.reduceFileImage()
            }
        }
        return null
    }

    fun deleteFile(file: File) {
        file.delete()
    }

    private fun File.reduceFileImage(): File {
        val file = this
        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality = 100
        var streamLength: Int
        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > IMAGE_MAX_SIZE)
        bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
        return file
    }
}