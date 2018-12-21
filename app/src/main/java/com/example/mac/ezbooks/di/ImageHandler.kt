package com.example.mac.ezbooks.di

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat.startActivityForResult
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.content.FileProvider
import android.widget.ImageView
import com.example.mac.ezbooks.detail_fragments.EditListingFragment
import com.example.mac.ezbooks.ui.main.EditAccountFragment
import com.example.mac.ezbooks.ui.main.UploadBookFragment
import java.text.SimpleDateFormat
import java.util.*
import java.io.*


class ImageHandler {
    private val REQUEST_TAKE_PHOTO = 1

    fun dispatchTakePictureIntent(fragment: Fragment, activity: FragmentActivity, mCurrentPhotoPath: String) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(activity.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile(mCurrentPhotoPath, activity)
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    println(ex)
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                            fragment.context!!,
                            "com.example.android.fileprovider",
                            it
                    )

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    when(fragment.tag){
                        "uploadBook"->{
                            (fragment as UploadBookFragment).selectedImage = photoURI
                        }
                        "editAccount"->{
                            (fragment as EditAccountFragment).selectedImage = photoURI
                        }
                        else->{
                            (fragment as EditListingFragment).selectedImage = photoURI
                        }
                    }

                    startActivityForResult(activity, takePictureIntent, REQUEST_TAKE_PHOTO, null)

                }
            }
        }
    }


    fun galleryAddPic(activity: FragmentActivity, mCurrentPhotoPath: String) {
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            val f = File(mCurrentPhotoPath)
            mediaScanIntent.data = Uri.fromFile(f)
            activity.sendBroadcast(mediaScanIntent)
        }

    }

    fun setPic(the_image: ImageView, mCurrentPhotoPath: String): ByteArray? {
        // Get the dimensions of the View
        val targetW: Int = the_image.width
        val targetH: Int = the_image.height
        var pendingUpload: ByteArray? = null
        val bmOptions = BitmapFactory.Options().apply {
            // Get the dimensions of the bitmap
            inJustDecodeBounds = true
            BitmapFactory.decodeFile(mCurrentPhotoPath, this)
            val photoW: Int = outWidth
            val photoH: Int = outHeight

            // Determine how much to scale down the image
            val scaleFactor: Int = Math.min(photoW / targetW, photoH / targetH)

            // Decode the image file into a Bitmap sized to fill the View
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
        }
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions)?.also { bitmap ->
            the_image.setImageBitmap(bitmap)
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            pendingUpload = stream.toByteArray()
        }

        return pendingUpload
    }

    @Throws(IOException::class)
    fun createImageFile(mCurrentPhotoPath: String, activity: Activity): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            mCurrentPhotoPath.replace(mCurrentPhotoPath, absolutePath)
        }
    }

}