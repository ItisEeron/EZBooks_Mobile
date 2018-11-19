package com.example.mac.ezbooks.di

import android.app.Activity
import android.arch.lifecycle.MutableLiveData
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.util.Base64
import android.util.Log
import com.example.mac.ezbooks.ui.main.MainViewModel
import com.example.mac.ezbooks.ui.main.Reports
import com.example.mac.ezbooks.ui.main.UserAccount
import com.google.firebase.FirebaseError
import com.google.firebase.database.*
import java.sql.ResultSet

class FirebaseDatabaseManager (){
    private val KEY_ACCOUNT = "account"
    private val KEY_TEXTBOOK = "textbook"
    private val KEY_REPORTS = "reports"
    private val KEY_CLASS = "class_standing"
    private val KEY_EMAIL = "email"
    private val KEY_NAME = "name"
    private val KEY_ID = "id"
    private val KEY_PHONE = "phone_number"
    private val KEY_PROF = "profile_img"

    private val database = FirebaseDatabase.getInstance()


    fun createUser(id: String, name: String, email: String) {
        var myRef = database.getReference(KEY_ACCOUNT)
        myRef.child(id).child(KEY_ID).setValue(id.toLong())
        myRef.child(id).child(KEY_NAME).setValue(name)
        myRef.child(id).child(KEY_PHONE).setValue(email)

    }

    fun updateAccount(account : UserAccount){
        var myRef = database.getReference(KEY_ACCOUNT)
        myRef.child(account.user_id.toString())
                .child(KEY_NAME).setValue(account.user_name)
        myRef.child(account.user_id.toString())
                .child(KEY_EMAIL).setValue(account.email_address)
        myRef.child(account.user_id.toString())
                .child(KEY_CLASS).setValue(account.class_standing)
        myRef.child(account.user_id.toString())
                .child(KEY_PHONE).setValue(account.phone_number)

        //conver profile_image into something readable by firebase
        var prof_image = Base64.encodeToString(account.profile_img, Base64.NO_WRAP)
        myRef.child(account.user_id.toString())
                .child(KEY_PROF).setValue(prof_image)
    }


    //Read from database
    fun retrieveAccount(id : Long , viewModel: MainViewModel, fragment: Fragment){
        var myRef = database.getReference(KEY_ACCOUNT)
        myRef.child(id.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                println(p0.message)
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val accounts = snapshot?.children
                var iter = accounts.iterator()
                var id: Long? = null
                var name: String? = null
                var phone_number: String? = null
                var email: String? = null
                var class_standing: String? = null
                var prof_img: ByteArray? = null

                for (item in iter) {
                    println("Eeron ${item}")
                    when (item.key) {
                        KEY_ID -> {
                            id = item.value as Long
                        }
                        KEY_PHONE -> {
                            phone_number = item.value as String
                        }
                        KEY_CLASS -> {
                            class_standing = item.value as String
                        }
                        KEY_EMAIL -> {
                            email = item.value as String
                        }
                        KEY_NAME -> {
                            name = item.value as String
                        }
                        KEY_PROF -> {
                            var prof_img_string = item.value as String
                            prof_img = Base64.decode(prof_img_string, Base64.DEFAULT)
                        }
                    }//when
                }//for

                viewModel.user_account = UserAccount(id?.toLong(), prof_img, name, email, phone_number,
                        "", 0, class_standing, null)

                Log.d("Eeron Tag: ", name + " " + email + " " + phone_number)

                //This refreshes the view!!!!
                var ft : FragmentTransaction = fragment.activity!!.supportFragmentManager.beginTransaction()
                ft.detach(fragment)
                ft.attach(fragment)
                ft.commit()
            }
        })

    }

    fun inputPutValues(map: HashMap<String, Any?>){

    }


}