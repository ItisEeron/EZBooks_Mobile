package com.example.mac.ezbooks.detail_fragments

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.example.mac.ezbooks.R
import com.example.mac.ezbooks.di.FirebaseDatabaseManager
import com.example.mac.ezbooks.di.ImageHandler
import com.example.mac.ezbooks.ui.main.MainViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.upload_book_layout.*
import kotlinx.android.synthetic.main.upload_book_layout.view.*
import java.io.ByteArrayOutputStream
import java.io.IOException


class EditListingFragment : Fragment() {
    private lateinit var booksViewModel : MainViewModel
    private var mCurrentPhotoPath: String = ""
    private var pendingUpload: ByteArray? = null
    private var imageHandler : ImageHandler = ImageHandler()
    private val databaseManager = FirebaseDatabaseManager()
    private var selectedImage : Uri? = null
    val GET_FROM_GALLERY = 3
    val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var bookImage : ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.upload_book_layout, container, false)

        booksViewModel = activity?.run {
            ViewModelProviders.of(this).get(MainViewModel::class.java)
        }
                ?: throw Exception("Invalid Activity")

        val selectedTextbook = booksViewModel.selected_selling
        bookImage = view.user_image
        view.post_book_label.text = "Edit a Book:"
        view.submit_book_button.text = "Submit Edits"


        view.submit_book_button.setOnClickListener {

            //TODO: COPY METHOD BREAKS
            if(!book_title_editText.text.isBlank()) {
                booksViewModel.selected_selling.Title = book_title_editText.text.toString()
            }//if
            if(!book_isbn_editText.text.isBlank()){
                booksViewModel.selected_selling.isbn = book_isbn_editText.text.toString()
            }//if
            if(!book_course_editText.text.isBlank()){
                booksViewModel.selected_selling.course = book_course_editText.text.toString()
            }//if
            if(!book_instructor_editText.text.isBlank()){
                booksViewModel.selected_selling.instructor = book_instructor_editText.text.toString()
            }//if

            databaseManager.getTextbookImg(selectedTextbook.book_id.toString(), selectedTextbook.affiliated_account?.user_id!!,
                    bookImage)

            databaseManager.createTextbook(booksViewModel.user_account,booksViewModel.selected_selling,
                    selectedImage)

            //Navigate back home
            fragmentManager?.popBackStack()
            //Task to keep the home page labels intact

            Toast.makeText(activity, "You have edited your textbook!",
                    Toast.LENGTH_LONG).show()


        }//view.submit_book_button.setOnClickListener

        view.reset_book_details.setOnClickListener{
            view.book_title_editText.text.clear()
            view.book_isbn_editText.text.clear()
            view.book_course_editText.text.clear()
            view.book_instructor_editText.text.clear()
            pendingUpload = null

            view.user_image.setImageDrawable(resources.getDrawable(R.mipmap.ic_launcher_round))

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
                imageHandler.dispatchTakePictureIntent(this, this.activity!!, mCurrentPhotoPath)
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


    override fun onCreate(savedInstanceState : Bundle?) {
        //Super allows the original function to execute then you add your own code
        super.onCreate(savedInstanceState)
        activity?.title = "Edit a Book to Sell"
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                GET_FROM_GALLERY -> {

                    try {
                        selectedImage = data?.getData()
                        Picasso.with(this.context).load(selectedImage).into(bookImage)
                        /*var bitmap = MediaStore.Images.Media.getBitmap(getActivity()
                                ?.getContentResolver(), selectedImage)
                        user_image.setImageBitmap(bitmap)
                        var stream = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                        pendingUpload = stream.toByteArray()
                        */

                    } catch (e: IOException) {
                        Log.i("TAG", "Some exception " + e)
                    }
                }
                REQUEST_IMAGE_CAPTURE ->{
                    selectedImage = data?.extras?.get("data") as? Uri
                    Picasso.with(this.context).load(selectedImage).into(bookImage)

                    /*val imageBitmap = data?.extras?.get("data") as? Bitmap

                    pendingUpload = imageHandler.setPic(user_image, mCurrentPhotoPath)
                    imageHandler.galleryAddPic(this.activity!! , mCurrentPhotoPath)
                    */
                }

            }
        }
    }

}
