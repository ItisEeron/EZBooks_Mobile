package com.example.mac.ezbooks.di

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import android.support.design.widget.NavigationView
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.Request
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.mac.ezbooks.R
import com.example.mac.ezbooks.ui.main.*
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso


class FirebaseDatabaseManager{
    private val KEY_ACCOUNT = "account"
    private val KEY_TEXTBOOK_REF = "textbook_ref"
    private val KEY_TEXTBOOK = "textbook"
    private val KEY_REPORTS = "reports"
    private val KEY_CLASS = "class_standing"
    private val KEY_EMAIL = "email"
    private val KEY_NAME = "name"
    private val KEY_ID = "id"
    private val KEY_PHONE = "phone_number"
    private val KEY_REQ_BOOKS = "req_books"
    private val KEY_OTHERS_LOG = "others_log"
    private val KEY_BOOK_ID = "book_id"
    private val KEY_TITLE = "title"
    private val KEY_ISBN = "isbn"
    private val KEY_INSTRUCTOR = "instructor"
    private val KEY_COURSE = "course"
    private val KEY_POTENTIAL_BUYERS = "potential_buyers"
    private val KEY_BUYER_ID = "buyer_id"
    private val KEY_BUYER_NAME = "buyer_name"
    private val KEY_BUYER_APPROVAL = "buyer_approval"

    private val TEXTBOOK_IMG_HEADER = "images/textbooks/"
    private val ACCOUNT_IMG_HEADER = "images/accounts/"

    private val database = FirebaseDatabase.getInstance()
    private val storage = FirebaseStorage.getInstance()

    @GlideModule class MyAppGlideModule : AppGlideModule(){
        override fun applyOptions(context: Context, builder: GlideBuilder) {
            builder.setDefaultRequestOptions(RequestOptions().format(DecodeFormat.PREFER_ARGB_8888))
        }
    }

    fun createUser(id: String, name: String, email: String) {
        val myRef = database.getReference(KEY_ACCOUNT)
        myRef.child(id).child(KEY_ID).setValue(id)
        myRef.child(id).child(KEY_NAME).setValue(name)
        myRef.child(id).child(KEY_EMAIL).setValue(email)
    }

    fun createTextbook(user_account: UserAccount, textbook: Textbooks, selectedImage : Uri?) {
        val book_id = textbook.book_id.toString()
        var myRef = database.getReference(KEY_ACCOUNT).child(user_account.user_id!!).child(KEY_TEXTBOOK)
        var storageRef = storage.getReference()
        myRef.child(book_id).child(KEY_BOOK_ID).setValue(textbook.book_id)
        myRef.child(book_id).child(KEY_TITLE).setValue(textbook.Title)
        myRef.child(book_id).child(KEY_ISBN).setValue(textbook.isbn)
        myRef.child(book_id).child(KEY_INSTRUCTOR).setValue(textbook.instructor)
        myRef.child(book_id).child(KEY_COURSE).setValue(textbook.course)


        var imageRef = storageRef.child(TEXTBOOK_IMG_HEADER+user_account.user_id+textbook.book_id)

        //starting upload
        if(selectedImage != null) {
            imageRef.putFile(selectedImage)
        }

        //Now create the reference for searching purposes
        myRef = database.getReference(KEY_TEXTBOOK_REF)
        myRef.child(user_account.user_id.toString() + textbook.book_id.toString()).child(KEY_ACCOUNT).setValue(user_account.user_id!!)
        myRef.child(user_account.user_id.toString() + textbook.book_id.toString()).child(KEY_NAME).setValue(textbook.affiliated_account!!.user_name)
        myRef.child(user_account.user_id.toString()+ textbook.book_id.toString()).child(KEY_BOOK_ID).setValue(textbook.book_id)
        myRef.child(user_account.user_id.toString() + textbook.book_id.toString()).child(KEY_TITLE).setValue(textbook.Title)
        myRef.child(user_account.user_id.toString() + textbook.book_id.toString()).child(KEY_ISBN).setValue(textbook.isbn)
        myRef.child(user_account.user_id.toString() + textbook.book_id.toString()).child(KEY_INSTRUCTOR).setValue(textbook.instructor)
        myRef.child(user_account.user_id.toString() + textbook.book_id.toString()).child(KEY_COURSE).setValue(textbook.course)
        myRef.child(user_account.user_id.toString() + textbook.book_id.toString()).child(KEY_EMAIL).setValue(user_account.email_address)
        myRef.child(user_account.user_id.toString() + textbook.book_id.toString()).child(KEY_PHONE).setValue(user_account.phone_number)
    }

    fun updateAccount(account : UserAccount, selectedImage: Uri?){
        val myRef = database.getReference(KEY_ACCOUNT)
        var storageRef = storage.getReference()
        myRef.child(account.user_id.toString())
                .child(KEY_NAME).setValue(account.user_name)
        myRef.child(account.user_id.toString())
                .child(KEY_EMAIL).setValue(account.email_address)
        myRef.child(account.user_id.toString())
                .child(KEY_CLASS).setValue(account.class_standing)
        myRef.child(account.user_id.toString())
                .child(KEY_PHONE).setValue(account.phone_number)

        var imageRef = storageRef.child(ACCOUNT_IMG_HEADER+account.user_id)
        if(selectedImage != null)
            imageRef.putFile(selectedImage)
    }

    fun removeTextbook(textbook: Searched_Textbooks){
        var myRef = database.getReference(KEY_ACCOUNT).child(textbook.userid.toString()).child(KEY_TEXTBOOK)
                .child(textbook.bookid.toString())
        var storageRef = storage.getReference(TEXTBOOK_IMG_HEADER+textbook.userid+textbook.bookid)

        storageRef.delete()
        myRef.removeValue()

        myRef = database.getReference(KEY_TEXTBOOK_REF).child(textbook.userid.toString() + textbook.userid.toString())
        myRef.removeValue()
    }

    //Read from database
    fun retrieveAccount(id : String , viewModel: MainViewModel, navigationView: NavigationView?){
        val myRef = database.getReference(KEY_ACCOUNT)
        var storageRef = storage.getReference()

        myRef.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                println(p0.message)
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                inputAccount(snapshot, viewModel, null)

                //Set up Navigation Header//////////////////////////////////////////////////////////
                navigationView?.getHeaderView(0)?.
                        findViewById<TextView>(R.id.user_header_name)
                        ?.text = viewModel.user_account.user_name

                var load = storageRef.child(ACCOUNT_IMG_HEADER+id)
                load.downloadUrl.addOnSuccessListener {
                    Picasso.with(navigationView?.context).load(it.toString()).into(navigationView?.getHeaderView(0)?.
                            findViewById<ImageView>(R.id.user_header_image))
                }.addOnFailureListener {
                    Toast.makeText(navigationView?.context, "There was an error loading the users image", Toast.LENGTH_SHORT)
                }
            }
            ////////////////////////////////////////////////////////////////////////////////////
        })

    }

    fun sendRequest(user_name: String, user_id : String ,owner_id : String , book_id : Long){
        var myRef = database.getReference(KEY_TEXTBOOK_REF).child((owner_id + book_id.toString()))
        myRef.child(KEY_POTENTIAL_BUYERS).child(user_id).child(KEY_BUYER_APPROVAL).setValue(false)
        myRef.child(KEY_POTENTIAL_BUYERS).child(user_id).child(KEY_BUYER_NAME).setValue(user_name)
        myRef.child(KEY_POTENTIAL_BUYERS).child(user_id).child(KEY_BUYER_ID).setValue(user_id)


        myRef = database.getReference(KEY_ACCOUNT).child(owner_id).child(KEY_TEXTBOOK)
                .child(book_id.toString()).child(KEY_POTENTIAL_BUYERS)
        myRef.child(user_id).child(KEY_BUYER_APPROVAL).setValue(false)
        myRef.child(user_id).child(KEY_BUYER_NAME).setValue(user_name)
        myRef.child(user_id).child(KEY_BUYER_ID).setValue(user_id)

        //Now add the request to user account for reference
        myRef = database.getReference(KEY_ACCOUNT).child(user_id).child(KEY_REQ_BOOKS)
                .child(owner_id + book_id.toString())

        myRef.child(KEY_ID).setValue(owner_id)
        myRef.child(KEY_BOOK_ID).setValue(book_id)

        //Finally set the approval in the buyers account to false
        myRef = database.getReference(KEY_ACCOUNT).child(user_id).child(KEY_REQ_BOOKS)
                .child(owner_id + book_id.toString()).child(KEY_BUYER_APPROVAL)
        myRef.setValue(false)

    }

    fun inputAccount (snapshot: DataSnapshot, viewModel: MainViewModel, textbook: Textbooks?){
        val accounts = snapshot.children
        val iter = accounts.iterator()
        var id: String? = null
        var name: String? = null
        var phone_number: String? = null
        var email: String? = null
        var class_standing: String? = null
        var reported_flags = 0L
        for (item in iter) {
            println("Eeron ${item}")
            when (item.key) {
                KEY_ID -> {
                    id = item.value as String
                }
                KEY_PHONE -> {
                    phone_number = item.value.toString()
                }
                KEY_CLASS -> {
                    class_standing = item.value.toString()
                }
                KEY_EMAIL -> {
                    email = item.value as String
                }
                KEY_NAME -> {
                    name = item.value as String
                }
                KEY_REPORTS -> {
                    val reports = item.children
                    for(report in reports){
                        if(report.key != "others_log"){
                            reported_flags = reported_flags + report.value.toString().toLong()
                        }
                    }
                }
            }//when
        }//for


        if(textbook != null){
            textbook.affiliated_account = UserAccount(id, name, email, phone_number,
                    "", if(reported_flags > 20) -1 else 1, class_standing)
        }else {
            viewModel.user_account = UserAccount(id, name, email, phone_number,
                    "", if(reported_flags > 20) -1 else 1, class_standing)
        }

        Log.d("Eeron Tag: ", name + " " + email + " " + phone_number)

    }//input_account

    fun removeRequest(viewModel: MainViewModel, textbook: Searched_Textbooks){
        var myRef = database.getReference(KEY_ACCOUNT).child(textbook.userid.toString()).child(KEY_TEXTBOOK)
                .child(textbook.bookid.toString()).child(KEY_POTENTIAL_BUYERS)
                .child(viewModel.user_account.user_id.toString())
        myRef.removeValue()

        myRef = database.getReference(KEY_TEXTBOOK_REF).child(textbook.userid.toString() + textbook.bookid.toString())
                .child(KEY_POTENTIAL_BUYERS).child(viewModel.user_account.user_id.toString())
        myRef.removeValue()

        myRef = database.getReference(KEY_ACCOUNT).child(viewModel.user_account.user_id.toString())
                .child(KEY_REQ_BOOKS).child(textbook.userid.toString() + textbook.bookid.toString())
        myRef.removeValue()

    }

    fun removeOtherUser(textbook: Searched_Textbooks, removed : Potential_Buyer){
        var myRef = database.getReference(KEY_ACCOUNT).child(textbook.userid!!).child(KEY_TEXTBOOK)
                .child(textbook.bookid.toString()).child(KEY_POTENTIAL_BUYERS)
                .child(removed.account_id)
        myRef.removeValue()

        myRef = database.getReference(KEY_TEXTBOOK_REF).child(textbook.userid + textbook.bookid.toString())
                .child(KEY_POTENTIAL_BUYERS).child(removed.account_id)
        myRef.removeValue()

        myRef = database.getReference(KEY_ACCOUNT).child(removed.account_id)
                .child(KEY_REQ_BOOKS).child(textbook.userid + textbook.bookid.toString())
        myRef.removeValue()

    }

    fun toggleRequestApproval(viewModel: MainViewModel, textbook: Searched_Textbooks, approval : Boolean, buyer: Potential_Buyer){
        var myRef = database.getReference(KEY_ACCOUNT).child(viewModel.user_account.user_id!!).child(KEY_TEXTBOOK)
                .child(textbook.bookid.toString()).child(KEY_POTENTIAL_BUYERS)
                .child(buyer.account_id).child(KEY_BUYER_APPROVAL)
        myRef.setValue(approval)
        myRef = database.getReference(KEY_TEXTBOOK_REF).child(viewModel.user_account.user_id.toString() + textbook.bookid.toString())
                .child(KEY_POTENTIAL_BUYERS).child(buyer.account_id).child(KEY_BUYER_APPROVAL)
        myRef.setValue(approval)

        myRef = database.getReference(KEY_ACCOUNT).child(buyer.account_id).child(KEY_REQ_BOOKS)
                .child(viewModel.user_account.user_id!! + textbook.bookid.toString()).child(KEY_BUYER_APPROVAL)
        myRef.setValue(approval)

    }

    fun reportUser(viewModel: MainViewModel, report : String, other_reason : String?){
        val myRef = database.getReference(KEY_ACCOUNT).child(viewModel.selected_requested.userid!!)
                .child(KEY_REPORTS).child(report)
        myRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                var report_count =  1L
                if(snapshot.value != null){
                    report_count = (snapshot.value as Long) + 1L
                }

                myRef.setValue(report_count)

                if(other_reason != null){
                    myRef.parent!!.child(KEY_OTHERS_LOG).child(report_count.toString())
                            .setValue(other_reason)
                }
            }
        })
    }

    fun getAccountImg(account_id: String, imageView: ImageView){
        var storageRef = storage.getReference()

        var load = storageRef.child(ACCOUNT_IMG_HEADER+account_id)
        val requestOptions = RequestOptions()
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)

        Log.d("Eeron Tag:" ,load.getBytes(Long.MAX_VALUE).toString())
        //Glide.with(imageView.context).load(load.downloadUrl).apply(requestOptions.skipMemoryCache(false))
          //      .into(imageView)
            //    .onLoadFailed(imageView.context.resources.getDrawable(R.drawable.blank_profile_picture_973460_640))

        load.metadata.addOnSuccessListener {
            Log.d("Eeron Tag:" , it.sizeBytes.toString())
        }

        load.downloadUrl.addOnSuccessListener {
            Picasso.with(imageView.context).load(it.toString()).resize(500, 500)
            .centerCrop().into(imageView)
        }.addOnFailureListener {
            Toast.makeText(imageView.context, "There was an error loading the users image", Toast.LENGTH_SHORT)
            imageView.setImageResource(R.drawable.blank_profile_picture_973460_640)
        }

    }

    fun getTextbookImg(textbook_id: String, account_id: String, imageView: ImageView){
        var storageRef = storage.getReference()

        var load = storageRef.child(TEXTBOOK_IMG_HEADER+account_id+textbook_id)
        val requestOptions = RequestOptions()
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(false)

        load.metadata.addOnSuccessListener {
            Log.d("Eeron Tag:" , it.sizeBytes.toString())
        }


        load.downloadUrl.addOnSuccessListener {
            Picasso.with(imageView.context).load(it.toString()).resize(100, 100)
                    .centerCrop().into(imageView)
        }.addOnFailureListener {
            Toast.makeText(imageView.context, "There was an error loading the users image", Toast.LENGTH_SHORT)
            imageView.setImageResource(R.drawable.android_image_5)

        }

    }

}