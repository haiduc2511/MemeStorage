package com.example.memestorage.utils

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Environment
import androidx.core.content.FileProvider
import com.example.memestorage.R
import java.io.File


object FileHelper {
    fun getImageFolderInAppDir(context: Context): String {
        val directory = File(context.filesDir.absolutePath + "/ARDrawing/Pictures")
        if (!directory.exists()) directory.mkdirs()
        return directory.absolutePath
    }

    fun getVideoFolderInAppDir(context: Context): String {
        val directory = File(context.filesDir.absolutePath + "/ARDrawing/Videos")
        if (!directory.exists()) directory.mkdirs()
        return directory.absolutePath
    }

    fun getPhotoImageFile(context: Context, name: String): File {
        val dir = getImageFolderInAppDir(context)
        return File("$dir/${name}.png")
    }

    fun getVideoFile(context: Context, name: String): File {
        val dir = getVideoFolderInAppDir(context)
        return File("$dir/${name}.mp4")
    }

    fun getPhotoImageTempFile(context: Context): File {
        val directory = File(context.cacheDir.absolutePath)
        if (!directory.exists()) directory.mkdirs()
        return File("${directory.absolutePath}/capturedPhoto.png")
    }

    fun sharePhoto(activity: Activity, path: String) {
        val file = File(path)
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "image/*"
        val fileUri = FileProvider.getUriForFile(
            activity,
            activity.getString(R.string.file_provider_authorities),
            file
        )
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri)
        activity.startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    fun shareVideo(activity: Activity, path: String) {
        val file = File(path)
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "video/*"
        val fileUri = FileProvider.getUriForFile(
            activity,
            activity.getString(R.string.file_provider_authorities),
            file
        )
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri)
        activity.startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    fun File.savePhotoIntoGallery(application: Application) {
        val dirPath =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path
        val destFile = File(dirPath, path.substringAfterLast("/"))
        if (destFile.exists().not()) {
            destFile.createNewFile()
        }
        inputStream().use { input ->
            destFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        scanFile(destFile, application)
    }

    private fun scanFile(file: File, application: Application) {
        MediaScannerConnection.scanFile(
            application, arrayOf(file.toString()),
            null, null
        )
    }


}