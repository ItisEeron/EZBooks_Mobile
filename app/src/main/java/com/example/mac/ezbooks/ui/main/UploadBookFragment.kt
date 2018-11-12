package com.example.mac.ezbooks.ui.main

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.mac.ezbooks.R
import kotlinx.android.synthetic.main.upload_book_layout.*
import kotlinx.android.synthetic.main.upload_book_layout.view.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class UploadBookFragment : Fragment(){
    private lateinit var booksViewModel : MainViewModel
    private lateinit var mCurrentPhotoPath: String
    private lateinit var newTextbooks : Textbooks
    private var pendingUpload: ByteArray? = null
    val GET_FROM_GALLERY = 3
    val REQUEST_IMAGE_CAPTURE = 1
    val REQUEST_TAKE_PHOTO = 1
    val RECENT_LIST_SIZE = 5


    override fun onCreate(savedInstanceState : Bundle?) {
        //Super allows the original function to execute then you add your own code
        super.onCreate(savedInstanceState)
        activity?.title = "Upload a Book to Sell"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.upload_book_layout, container, false)

        booksViewModel = activity?.run {
            ViewModelProviders.of(this).get(MainViewModel::class.java) }
                ?: throw Exception("Invalid Activity")


        view.submit_book_button.setOnClickListener {

            if(!book_title_editText.text.isBlank() && !book_isbn_editText.text.isBlank()){//Want these two attribute filled!!!
                newTextbooks = Textbooks(book_title_editText.text.toString(),
                        book_isbn_editText.text.toString(), null,
                        book_course_editText.text.toString(),
                        book_instructor_editText.text.toString(), booksViewModel.user_account)
                if(pendingUpload != null)
                    newTextbooks.book_img = pendingUpload

                //Add the new text books into view model (it will store it into the database)
                booksViewModel.selling_textbooks.add(0, newTextbooks)

                //Update recent_selling_Textbooks on phone so we do not have to wait for database
                booksViewModel.recent_selling_Textbooks.add(0,newTextbooks)
                if(booksViewModel.recent_selling_Textbooks.size > RECENT_LIST_SIZE)
                    booksViewModel.recent_selling_Textbooks.removeAt(RECENT_LIST_SIZE)


                //Navigate back home
                fragmentManager?.popBackStack()
                //Task to keep the home page labels intact
                activity?.findViewById<NavigationView>(R.id.nav_view)?.setCheckedItem(R.id.nav_home)
                activity?.title ="EZ Books Home"

                Toast.makeText(activity,
                        "You have successfully posted a textbook!",
                        Toast.LENGTH_LONG).show()
            }//if
            else {
               Toast.makeText(activity,
                       "Please enter a title and isbn for your book!!!",
                       Toast.LENGTH_LONG).show()
            }//else

        }//view.submit_book_button.setOnClickListener

        view.reset_book_details.setOnClickListener{
            view.book_title_editText.text.clear()
            view.book_isbn_editText.text.clear()
            view.book_course_editText.text.clear()
            view.book_instructor_editText.text.clear()
            pendingUpload = null

            view.upload_book_Image.setImageDrawable(resources.getDrawable(R.mipmap.ic_launcher_round))

        }

        //Upload an image from the gallery. Camera should not be needed for this
        view.upload_image_button.setOnClickListener{
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, GET_FROM_GALLERY)
        }

        //Test to see if the device has a camera, then execute if it does
        if(context!!.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            view.upload_from_camera_button.setOnClickListener {
                dispatchTakePictureIntent()
                Log.i("Eeron Log", "I Made it!!")
                //setPic()
            }
        }
        else{
            Toast.makeText(activity,
                    "You don't have a camera silly!",
                    Toast.LENGTH_SHORT).show()
        }


        return view
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(activity!!.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    println(ex)
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                            this.context!!,
                            "com.example.android.fileprovider",
                            it
                    )

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)

                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)

                }
            }
        }
    }


    private fun galleryAddPic() {
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            val f = File(mCurrentPhotoPath)
            mediaScanIntent.data = Uri.fromFile(f)
            activity?.sendBroadcast(mediaScanIntent)
        }

    }

    private fun setPic() {
        // Get the dimensions of the View
        val targetW: Int = upload_book_Image.width
        val targetH: Int = upload_book_Image.height

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
            upload_book_Image.setImageBitmap(bitmap)
            var stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            pendingUpload = stream.toByteArray()
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            mCurrentPhotoPath = absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                GET_FROM_GALLERY -> {

                    var selectedImage = data?.getData()
                    try {
                        var bitmap = MediaStore.Images.Media.getBitmap(getActivity()
                                ?.getContentResolver(), selectedImage)
                        upload_book_Image.setImageBitmap(bitmap)
                        var stream = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                        pendingUpload = stream.toByteArray()

                    } catch (e: IOException) {
                        Log.i("TAG", "Some exception " + e)
                    }
                }
                REQUEST_IMAGE_CAPTURE ->{
                    val imageBitmap = data?.extras?.get("data") as? Bitmap

                    setPic()
                    galleryAddPic()
                }

            }
        }
    }

}