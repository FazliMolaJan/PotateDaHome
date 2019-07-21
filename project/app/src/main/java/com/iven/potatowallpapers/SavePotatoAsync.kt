package com.iven.potatowallpapers

import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Environment
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*

class SavePotatoAsync(
    @NonNull private val contextReference: WeakReference<Context>?,
    @NonNull private val bitmap: Bitmap,
    private val deviceWidth: Int,
    private val deviceHeight: Int
) : AsyncTask<Void, Void, Void>() {

    private lateinit var progressDialog: AlertDialog

    private fun dirExists(dirPath: String): Boolean {
        val dir = File(dirPath)
        return dir.exists() && dir.isDirectory
    }

    // Method to save an image to external storage
    private fun saveImageToExternalStorage(bitmap: Bitmap) {

        val context = contextReference?.get()!!

        val dirPath: String = File.separator + context.resources.getString(R.string.app_name)

        // Get the external storage directory path
        val path = Environment.getExternalStorageDirectory().toString() + dirPath

        //create the directory if it doesn't exists
        if (!dirExists(path)) {
            val directory = File(dirPath)
            directory.mkdirs()
        }

        val s = SimpleDateFormat(context.resources.getString(R.string.time_pattern), Locale.getDefault())
        val format = s.format(Date())

        // Create a file to save the image
        val file = File(path, "${context.resources.getString(R.string.app_name) + format}.png")

        try {
            // Get the file output stream
            val stream: OutputStream = FileOutputStream(file)

            // Compress the bitmap
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)

            // Flush the output stream
            stream.flush()

            // Close the output stream
            stream.close()

        } catch (e: IOException) { // Catch the exception
            e.printStackTrace()
        }

        //refresh media store database
        if (Build.VERSION.SDK_INT < 29) {
            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            val uri = Uri.fromFile(file)
            mediaScanIntent.data = uri
            context.sendBroadcast(mediaScanIntent)
        }
        setWallpaper(FileProvider.getUriForFile(context, context.resources.getString(R.string.app_name), file))
    }

    override fun onPreExecute() {
        super.onPreExecute()

        val progressDialogBuilder = AlertDialog.Builder(contextReference?.get()!!)
        progressDialogBuilder.setView(R.layout.progress_dialog)
        progressDialogBuilder.setCancelable(false)
        progressDialog = progressDialogBuilder.create()
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()
    }

    override fun doInBackground(vararg p0: Void?): Void? {
        //save wallpaper png before applying (applying directly freezes the ui)
        saveImageToExternalStorage(cropBitmapFromCenterAndScreenSize(bitmap))
        return null
    }

    override fun onPostExecute(result: Void?) {
        if (progressDialog.isShowing) progressDialog.dismiss()
    }

    //set view as wallpaper
    private fun setWallpaper(@NonNull uri: Uri) {

        val context = contextReference?.get()!!
        val wallpaperManager = WallpaperManager.getInstance(context)

        try {
            //start crop and set wallpaper intent
            context.startActivity(wallpaperManager.getCropAndSetWallpaperIntent(uri))
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private fun cropBitmapFromCenterAndScreenSize(@NonNull bitmapToProcess: Bitmap): Bitmap {
        //https://stackoverflow.com/a/25699365

        val bitmapWidth = bitmapToProcess.width.toFloat()
        val bitmapHeight = bitmapToProcess.height.toFloat()

        val bitmapRatio = bitmapWidth / bitmapHeight
        val screenRatio = deviceWidth / deviceHeight
        val bitmapNewWidth: Int
        val bitmapNewHeight: Int

        if (screenRatio > bitmapRatio) {
            bitmapNewWidth = deviceWidth
            bitmapNewHeight = (bitmapNewWidth / bitmapRatio).toInt()
        } else {
            bitmapNewHeight = deviceHeight
            bitmapNewWidth = (bitmapNewHeight * bitmapRatio).toInt()
        }

        val newBitmap = Bitmap.createScaledBitmap(
            bitmap, bitmapNewWidth,
            bitmapNewHeight, true
        )

        val bitmapGapX = ((bitmapNewWidth - deviceWidth) / 2.0f).toInt()
        val bitmapGapY = ((bitmapNewHeight - deviceHeight) / 2.0f).toInt()

        //final bitmap
        return Bitmap.createBitmap(
            newBitmap, bitmapGapX, bitmapGapY,
            deviceWidth, deviceHeight
        )
    }
}
