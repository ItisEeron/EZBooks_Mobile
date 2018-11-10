package com.example.mac.ezbooks.ui.main

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mac.ezbooks.HomeFragment
import com.example.mac.ezbooks.R
import kotlinx.android.synthetic.main.edit_user_account_layout.*
import kotlinx.android.synthetic.main.edit_user_account_layout.view.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Environment
import android.support.v4.content.FileProvider
import android.util.Log
import kotlinx.android.synthetic.main.home_fragment.view.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class EditAccountFragment : Fragment(){

    private lateinit var ezbooksViewModel: MainViewModel
    private lateinit var main_account : UserAccount
    private lateinit var mCurrentPhotoPath: String
    private var pendingUpload: ByteArray? = null
    val GET_FROM_GALLERY = 3
    val REQUEST_IMAGE_CAPTURE = 1
    val REQUEST_TAKE_PHOTO = 1

    override fun onCreate(savedInstanceState : Bundle?) {
        //Super allows the original function to execute then you add your own code
        super.onCreate(savedInstanceState)
        activity?.title = "Edit Your Account"

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.edit_user_account_layout, container, false)

        ezbooksViewModel = activity?.run {
            ViewModelProviders.of(this).get(MainViewModel::class.java) }
                ?: throw Exception("Invalid Activity")

        main_account = ezbooksViewModel.user_account

        //Present User Account image as the uploaded image for this page
        if(main_account.profile_img != null){
            var bitmap = BitmapFactory.
                    decodeByteArray(main_account.profile_img,
                            0, main_account!!.profile_img!!.size)
            view.current_user_image.setImageBitmap(bitmap)
        }


        //TODO: Make is so that hints show
        view.submit_account_changes_button.setOnClickListener{
            val fragmentManager = activity?.supportFragmentManager

            //Make Sure that actual edits have been inputted before submitted
            main_account.user_name = if(view.name_editText.text.toString().isBlank())
                main_account.user_name else view.name_editText.text.toString()

            main_account.phone_number = if(view.phone_number_editText.text.toString().isBlank())
                main_account.phone_number else view.phone_number_editText.text.toString()

            main_account.email_address = if(view.email_editText.text.toString().isBlank())
                main_account.email_address else view.email_editText.text.toString()

            main_account.class_standing = if(view.class_standing_editText.text.toString().isBlank())
                main_account.class_standing else view.class_standing_editText.text.toString()


           if(pendingUpload != null )
                main_account.profile_img = pendingUpload

            fragmentManager?.popBackStack()
            //Task to keep the home page labels intact
            activity?.findViewById<NavigationView>(R.id.nav_view)?.setCheckedItem(R.id.nav_home)
            activity?.title ="EZ Books Home"


        }

        view.reset_credentials.setOnClickListener{
            view.name_editText.text.clear()
            view.phone_number_editText.text.clear()
            view.email_editText.text.clear()
            view.class_standing_editText.text.clear()
            pendingUpload = null

            if(ezbooksViewModel.user_account.profile_img != null){
                var bitmap = BitmapFactory.
                        decodeByteArray(ezbooksViewModel.user_account.profile_img,
                                0, ezbooksViewModel!!.user_account!!.profile_img!!.size)
                view.current_user_image.setImageBitmap(bitmap)
            }
            else{
                //TODO: SET IMAGE TO DEFAULT VALUE
                view.current_user_image.setImageDrawable(resources.getDrawable(R.mipmap.ic_launcher_round))
            }
        }

        view.upload_image_button.setOnClickListener{
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, GET_FROM_GALLERY)
        }

        if(context!!.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            view.reset_image_button.setOnClickListener {
                //TODO: RESET IMAGE TO PREVIOUS
                dispatchTakePictureIntent()
                Log.i("Eeron Log", "I Made it!!")
                //setPic()
            }
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
                        Log.i("Eeron Log 3:" , " I Made IT!")

                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        Log.i("Eeron Log 4:" , " I Made IT!")

                        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                        Log.i("Eeron Log 5:" , " I Made IT!")

                    }
                }
            }
        }


    private fun galleryAddPic() {
        Log.i("Eeron Log 4:" , " I Made IT!")
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            val f = File(mCurrentPhotoPath)
            mediaScanIntent.data = Uri.fromFile(f)
            activity?.sendBroadcast(mediaScanIntent)
        }
        Log.i("Eeron Log 7:" , " I Made IT!")

    }

    private fun setPic() {
        // Get the dimensions of the View
        val targetW: Int = current_user_image.width
        val targetH: Int = current_user_image.height

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
            current_user_image.setImageBitmap(bitmap)
            var stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            pendingUpload = stream.toByteArray()
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        Log.i("Eeron Log 1:" , " I Made IT!")
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            mCurrentPhotoPath = absolutePath
            Log.i("Eeron Log 2:" , " I Made IT! " + mCurrentPhotoPath )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i("Eeron Log 5.1:", " I Made IT! ")

        if(resultCode == RESULT_OK) {
            when (requestCode) {
                GET_FROM_GALLERY -> {

                    var selectedImage = data?.getData()
                    try {
                        var bitmap = MediaStore.Images.Media.getBitmap(getActivity()
                                ?.getContentResolver(), selectedImage)
                        current_user_image.setImageBitmap(bitmap)
                        var stream = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                        pendingUpload = stream.toByteArray()

                    } catch (e: IOException) {
                        Log.i("TAG", "Some exception " + e)
                    }
                }
                REQUEST_IMAGE_CAPTURE ->{
                    Log.i("Eeron Log 5:", " I Made IT! ")
                    val imageBitmap = data?.extras?.get("data") as? Bitmap
                    Log.i("Eeron Log 6:", " I Made IT! ")

                    setPic()
                    galleryAddPic()
                    Log.i("Eeron Log 7:", " I Made IT! ")
                }

            }
        }
    }


}