package com.example.mac.ezbooks.ui.main

import android.app.Activity.RESULT_OK
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mac.ezbooks.R
import kotlinx.android.synthetic.main.edit_user_account_layout.*
import kotlinx.android.synthetic.main.edit_user_account_layout.view.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.mac.ezbooks.di.FirebaseDatabaseManager
import com.example.mac.ezbooks.di.ImageHandler
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream
import java.io.IOException

class EditAccountFragment : Fragment(){

    private lateinit var ezbooksViewModel: MainViewModel
    private lateinit var main_account : UserAccount
    private var mCurrentPhotoPath: String = ""
    private var pendingUpload: ByteArray? = null
    private var imageHandler : ImageHandler = ImageHandler()
    val GET_FROM_GALLERY = 3
    val REQUEST_IMAGE_CAPTURE = 1
    private var selectedImage : Uri? = null
    val databaseManager : FirebaseDatabaseManager = FirebaseDatabaseManager()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.edit_user_account_layout, container, false)

        ezbooksViewModel = activity?.run {
            ViewModelProviders.of(this).get(MainViewModel::class.java) }
                ?: throw Exception("Invalid Activity")

        main_account = ezbooksViewModel.user_account

        //Present User Account image as the uploaded image for this page
        databaseManager.getAccountImg(main_account.user_id.toString(), view.user_account_Image)

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

            databaseManager.updateAccount(main_account, selectedImage)

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

            databaseManager.getAccountImg(main_account.user_id.toString(), view.user_account_Image)
        }

        view.upload_image_button.setOnClickListener{
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, GET_FROM_GALLERY)
        }

        if(context!!.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            view.upload_from_camera_button.setOnClickListener {
                imageHandler.dispatchTakePictureIntent(this , this.activity!!, mCurrentPhotoPath)
                Log.i("Eeron Log", "I Made it!!")
                //setPic()
            }
        }

        return view
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == RESULT_OK) {
            when (requestCode) {
                GET_FROM_GALLERY -> {

                    try {
                        selectedImage = data?.getData()
                        Picasso.with(this.context!!).load(selectedImage).into(this.user_account_Image)
                        /*
                        var bitmap = MediaStore.Images.Media.getBitmap(getActivity()
                                ?.getContentResolver(), selectedImage)
                        user_account_Image.setImageBitmap(bitmap)
                        var stream = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, stream)
                        pendingUpload = stream.toByteArray()
                        */

                    } catch (e: IOException) {
                        Log.i("TAG", "Some exception " + e)
                    }
                }
                REQUEST_IMAGE_CAPTURE ->{
                    selectedImage = data?.extras?.get("data") as Uri
                    Picasso.with(this.context!!).load(selectedImage).into(this.user_account_Image)
                    /*
                    val imageBitmap = data?.extras?.get("data") as? Bitmap
                    user_account_Image.setImageBitmap(imageBitmap)
                    var stream = ByteArrayOutputStream()
                    imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 20, stream)
                    //imageHandler.setPic(user_account_Image, mCurrentPhotoPath)
                    //imageHandler.galleryAddPic(this.activity!!, mCurrentPhotoPath )
                    pendingUpload = stream.toByteArray()
                    */
                }

            }
        }
    }


}
