package com.example.mac.ezbooks.ui.main

import android.app.Activity
import android.app.DownloadManager
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.mac.ezbooks.R
import com.example.mac.ezbooks.di.*
import com.google.api.client.json.Json
import com.google.api.services.books.Books
import com.google.api.services.books.model.Volume
import com.google.api.services.books.model.Volumes
import com.google.common.reflect.TypeToken
import kotlinx.android.synthetic.main.upload_book_layout.*
import kotlinx.android.synthetic.main.upload_book_layout.view.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import javax.sql.DataSource


class UploadBookFragment : Fragment(){
    private lateinit var booksViewModel : MainViewModel
    private var mCurrentPhotoPath: String = ""
    private lateinit var newTextbooks : Textbooks
    private var pendingUpload: ByteArray? = null
    private var imageHandler : ImageHandler = ImageHandler()
    private var databaseManager: FirebaseDatabaseManager = FirebaseDatabaseManager()
    val GET_FROM_GALLERY = 3
    val REQUEST_IMAGE_CAPTURE = 1
    val RECENT_LIST_SIZE = 5
    val CONNECTON_TIMEOUT_MILLISECONDS = 600000
    var url_base = "https://www.googleapis.com/books/v1/volumes?q=isbn:"
    var selectedImage : Uri? = null
    private lateinit var bookImage : ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.upload_book_layout, container, false)
        booksViewModel = activity?.run {
            ViewModelProviders.of(this).get(MainViewModel::class.java) }
                ?: throw Exception("Invalid Activity")

        bookImage = view.user_image

        view.submit_book_button.setOnClickListener {
            //Ensures that the id of each new textbook will never collide!!!!
            var id_generated = 1L
            if(booksViewModel.selling_textbooks.isNotEmpty()) {
                id_generated = booksViewModel.selling_textbooks.first().book_id + 1
            }
            if(!book_title_editText.text.isBlank() && !book_isbn_editText.text.isBlank()){//Want these two attribute filled!!!
                newTextbooks = Textbooks(id_generated, book_title_editText.text.toString(),
                        book_isbn_editText.text.toString(),
                        book_course_editText.text.toString(),
                        book_instructor_editText.text.toString(), booksViewModel.user_account, null)

                //Add the new text books into view model (it will store it into the database)
                booksViewModel.selling_textbooks.add(0, newTextbooks)

                //Update recent_selling_Textbooks on phone so we do not have to wait for database
                booksViewModel.recent_selling_Textbooks.add(0,newTextbooks)
                if(booksViewModel.recent_selling_Textbooks.size > RECENT_LIST_SIZE)
                    booksViewModel.recent_selling_Textbooks.removeAt(RECENT_LIST_SIZE)


                //Navigate back home
                /*
                fragmentManager?.popBackStack()
                //Task to keep the home page labels intact
                activity?.findViewById<NavigationView>(R.id.nav_view)?.setCheckedItem(R.id.nav_home)
                activity?.title ="EZ Books Home"
                */
                url_base = url_base + "9781617293290"
                GetBooksAsyncTask().execute(url_base)

                /*
                databaseManager.createTextbook(booksViewModel.user_account, newTextbooks, selectedImage)

                Toast.makeText(activity,
                        "You have successfully posted a textbook!",
                        Toast.LENGTH_LONG).show()
                */
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
                imageHandler.dispatchTakePictureIntent(this, this.activity!! , mCurrentPhotoPath)
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                GET_FROM_GALLERY -> {

                    try {
                        selectedImage = data?.getData()
                        Glide.with(this.context!!).load(selectedImage).into( bookImage )
                        /*
                        var bitmap = MediaStore.Images.Media.getBitmap(getActivity()
                                ?.getContentResolver(), selectedImage)
                        user_image.setImageBitmap(bitmap)
                        var stream = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, stream)
                        pendingUpload = stream.toByteArray()
                        */

                    } catch (e: IOException) {
                        Log.i("TAG", "Some exception " + e)
                    }
                }
                REQUEST_IMAGE_CAPTURE ->{
                    try {
                        selectedImage = data?.extras?.get("data") as Uri
                        Glide.with(this.context!!).load(selectedImage).into(bookImage)
                        /*
                        val imageBitmap = data?.extras?.get("data") as? Bitmap
                        user_image.setImageBitmap(imageBitmap)
                        var stream = ByteArrayOutputStream()
                        imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 20, stream)
                        //imageHandler.setPic(user_image, mCurrentPhotoPath)
                        //imageHandler.galleryAddPic(this.activity!!, mCurrentPhotoPath )
                        pendingUpload = stream.toByteArray()
                        */
                    }
                    catch(e : IOException){
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
                val url = URL(url_base)

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
            //var bookData = Gson().fromJson(result, Books::class.java)
            var bookData = Gson().fromJson(result, JsonObject::class.java)
            var books = bookData["items"] as JsonArray
            Log.d("Eeron: " , "I Parsed it! Made it!!!")
            if(bookData == null){
                Log.d("Eeron ", "Something went wrong with the parse")
            }
            else {//Show that the data has been successfully parsed
                Log.d("Eeron ", "Made it!")
                val textbookdata = books[0] as JsonObject
                val info = textbookdata["volumeInfo"] as JsonObject
                val textbookTitle = info["title"] as String
                val textbookImageLink = info["imageLinks"] as JsonObject
                val textbookSmallThumb = textbookImageLink.get("smallThumbnail")
                var textbookThumb = textbookImageLink.get("thumbnail")


                if(selectedImage == null) {
                    selectedImage = Uri.parse(textbookThumb.asString.trim('"', ' '))

                    Picasso.get().load("http://books.google.com/books/content?id=hbDrBAhjDQIC&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api"
                            /*textbookSmallThumb.toString().trim('"', ' ')*/).into(bookImage, object : Callback {
                        override fun onError(e: Exception) {
                            Log.d("Eeron ", e.toString())
                        }

                        override fun onSuccess() {
                            databaseManager.createTextbook(booksViewModel.user_account, newTextbooks, selectedImage)

                            //Navigate back home
                            fragmentManager?.popBackStack()
                            //Task to keep the home page labels intact
                            activity?.findViewById<NavigationView>(R.id.nav_view)?.setCheckedItem(R.id.nav_home)
                            activity?.title ="EZ Books Home"

                            Toast.makeText(activity,
                                    "You have successfully posted a textbook!",
                                    Toast.LENGTH_LONG).show()
                        }
                    })
                    //selectedImage = Uri.parse(textbookThumb.asString)
                }
            }

            /*
            databaseManager.createTextbook(booksViewModel.user_account, newTextbooks, selectedImage)

            //Navigate back home
            fragmentManager?.popBackStack()
            //Task to keep the home page labels intact
            activity?.findViewById<NavigationView>(R.id.nav_view)?.setCheckedItem(R.id.nav_home)
            activity?.title ="EZ Books Home"

            Toast.makeText(activity,
                    "You have successfully posted a textbook!",
                    Toast.LENGTH_LONG).show()
            */
        }
    }

}