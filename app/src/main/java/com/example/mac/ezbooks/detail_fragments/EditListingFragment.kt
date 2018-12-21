package com.example.mac.ezbooks.detail_fragments

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
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
import com.example.mac.ezbooks.ui.main.Textbooks
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.upload_book_layout.*
import kotlinx.android.synthetic.main.upload_book_layout.view.*
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


class EditListingFragment : Fragment() {
    private lateinit var booksViewModel : MainViewModel
    private var mCurrentPhotoPath: String = ""
    private var pendingUpload: ByteArray? = null
    private var imageHandler : ImageHandler = ImageHandler()
    private val databaseManager = FirebaseDatabaseManager()
    var selectedImage : Uri? = null
    val GET_FROM_GALLERY = 3
    private lateinit var bookImage : ImageView
    private lateinit var selectedTextbook : Textbooks
    val CONNECTON_TIMEOUT_MILLISECONDS = 600000
    var url_base = "https://www.googleapis.com/books/v1/volumes?q=isbn:"
    var searchBookURL: String = ""
    var popStack = true
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.upload_book_layout, container, false)

        bookImage = view.user_image
        view.post_book_label.text = "Edit a Book:"
        view.submit_book_button.text = "Submit Edits"

        if(selectedImage != null){
            Picasso.get().load(selectedImage).into(bookImage)
        }

        view.submit_book_button.setOnClickListener {
            searchBookURL = ""
            popStack = true

            if((!book_isbn_editText.text.isBlank())){
                popStack = false
                searchBookURL = url_base + book_isbn_editText.text.toString()
                if(!book_title_editText.text.isBlank()) {
                    selectedTextbook.Title = book_title_editText.text.toString()
                }//if
                if(!book_course_editText.text.isBlank()){
                    selectedTextbook.course = book_course_editText.text.toString()
                }//if
                if(!book_instructor_editText.text.isBlank()){
                    selectedTextbook.instructor = book_instructor_editText.text.toString()
                }//if
                GetBooksAsyncTask().execute(searchBookURL)

            }//if
            else{
                popStack = true
                if(!book_title_editText.text.isBlank()) {
                    selectedTextbook.Title = book_title_editText.text.toString()
                }//if
                if(!book_course_editText.text.isBlank()){
                    selectedTextbook.course = book_course_editText.text.toString()
                }//if
                if(!book_instructor_editText.text.isBlank()){
                    selectedTextbook.instructor = book_instructor_editText.text.toString()
                }//if
                databaseManager.createTextbook(booksViewModel.user_account, selectedTextbook, selectedImage)
            }


            //Navigate back home
            if(popStack == true)
                fragmentManager?.popBackStack()

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

        booksViewModel = activity?.run {
            ViewModelProviders.of(this).get(MainViewModel::class.java)
        }
                ?: throw Exception("Invalid Activity")

        selectedTextbook = booksViewModel.selected_selling
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                GET_FROM_GALLERY -> {
                    try {
                        selectedImage = data?.getData()
                    } catch (e: IOException) {
                        Log.i("TAG", "Some exception " + e)
                    }
                }

            }
        }
    }

    inner class GetBooksAsyncTask : AsyncTask<String, String, String>() {
        override fun doInBackground(vararg params: String?): String {
            var urlConnection: HttpURLConnection? = null
            var inString = ""
            try {
                val url = URL(searchBookURL)

                urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.connectTimeout = CONNECTON_TIMEOUT_MILLISECONDS
                urlConnection.readTimeout = CONNECTON_TIMEOUT_MILLISECONDS

                //var inString = streamToString(urlConnection.inputStream)

                // replaces need for streamToString()
                inString = urlConnection.inputStream.bufferedReader().readText()

                publishProgress(inString)
            } catch (ex: Exception) {
                Log.d("Eeron" ,"HttpURLConnection exception" + ex)
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect()
                }
            }

            return inString
        }

        override fun onPostExecute(result: String) {
            var bookData = Gson().fromJson(result, JsonObject::class.java)
            val totalItems = bookData["totalItems"].asInt
            if(totalItems == 0){
                Log.d("Eeron ", "This book was not found in the Database")
                Toast.makeText(activity,
                        "We could not find this textbook",
                        Toast.LENGTH_LONG).show()
            } else {
                //var bookData = Gson().fromJson(result, JsonObject::class.java)
                var books = bookData["items"] as JsonArray
                Log.d("Eeron: " , "I Parsed it! Made it!!!")
                //Show that the data has been successfully parsed
                Log.d("Eeron ", "Made it!")
                val textbookdata = books[0] as JsonObject
                val info = textbookdata["volumeInfo"] as JsonObject
                var textbookTitle =info["title"]
                selectedTextbook.Title = textbookTitle.asString
                val textbookImageLink = info["imageLinks"] as JsonObject
                var textbookThumb = textbookImageLink.get("thumbnail")

                //MAKE SURE THAT ANY EXTRA PARENTHESIS AND SPACES ARE REMOVED!!!
                var textbookThumbString = textbookThumb.toString().trim('"', ' ')

                //NOTICE FOR GOOGLE IMAGES, MUST REPLACE http WITH https ELSE YOU WILL GET FAILURE
                textbookThumbString = textbookThumbString.replaceFirst("http:", "https:")

                if(selectedImage == null) {
                    Picasso.get().load(textbookThumbString).into(bookImage, object : Callback {
                        override fun onError(e: Exception) {
                            Log.d("Eeron ", e.toString())
                        }

                        override fun onSuccess() {
                            selectedTextbook.thumbnailURL = textbookThumbString
                            selectedTextbook.isbn = book_isbn_editText.text.toString()
                            databaseManager.createTextbook(booksViewModel.user_account, selectedTextbook, selectedImage)
                            fragmentManager?.popBackStack()
                            //Task to keep the home page labels intact

                            Toast.makeText(activity,
                                    "You have successfully edited the textbook!",
                                    Toast.LENGTH_LONG).show()
                        }
                    })
                }else{
                    selectedTextbook.thumbnailURL = textbookThumbString
                    selectedTextbook.isbn = book_isbn_editText.text.toString()
                    databaseManager.createTextbook(booksViewModel.user_account, selectedTextbook, selectedImage)
                    fragmentManager?.popBackStack()
                    //Task to keep the home page labels intact

                    Toast.makeText(activity,
                            "You have successfully edited the textbook!",
                            Toast.LENGTH_LONG).show()
                }
            }

        }
    }

}
