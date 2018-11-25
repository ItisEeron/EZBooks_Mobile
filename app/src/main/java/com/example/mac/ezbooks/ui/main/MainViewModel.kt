package com.example.mac.ezbooks.ui.main

import android.arch.lifecycle.ViewModel
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.support.v4.app.Fragment
import com.example.mac.ezbooks.R
import com.example.mac.ezbooks.di.FirebaseDatabaseManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.Serializable


class MainViewModel : ViewModel() {
    lateinit var selling_textbooks: MutableList<Textbooks>
    lateinit var selected_selling: Textbooks
    lateinit var user_account:UserAccount
    lateinit var requested_textbooks: MutableList<Searched_Textbooks>
    lateinit var selected_requested: Searched_Textbooks
    lateinit var recent_requested_Textbooks: MutableList<Searched_Textbooks>
    lateinit var recent_selling_Textbooks: MutableList<Textbooks>
    private val RECENTS_SIZE = 5
    private val databaseManager = FirebaseDatabaseManager()
    private val database = FirebaseDatabase.getInstance()

    private val KEY_ACCOUNT = "account"
    private val KEY_TEXTBOOK = "textbook"
    private val KEY_REPORTS = "reports"
    private val KEY_CLASS = "class_standing"
    private val KEY_EMAIL = "email"
    private val KEY_NAME = "name"
    private val KEY_ID = "id"
    private val KEY_PHONE = "phone_number"
    private val KEY_PROF = "profile_img"

    //Temporary Data///////////////////////////////////////////////////
    //TODO: GET THIS DATA TO SHOW TEXTBOOKS INSTEAD OF THESE TEMP BOOK
    data class TempBook (var titles:String, var details: String, var images:Int)

    private val OGlist = listOf(TempBook("Book One","Item one details",R.drawable.android_image_1),
            TempBook("Book Two","Item two details",R.drawable.android_image_2),
            TempBook("Book Three","Item three details",R.drawable.android_image_3),
            TempBook("Book Four","Item four details",R.drawable.android_image_4),
            TempBook("Book Five","Item five details",R.drawable.android_image_5),
            TempBook("Book Six","Item six details",R.drawable.android_image_6),
            TempBook("Book Seven","Item seven details",R.drawable.android_image_7),
            TempBook("Book Eight","Item eight details",R.drawable.android_image_8))

    val R_B_Page_List: MutableList<TempBook> = mutableListOf()
    val R_B_Home_List: MutableList<TempBook> = mutableListOf()

    init{
        R_B_Page_List.addAll(OGlist)
        R_B_Home_List.addAll(R_B_Page_List.subList(0, RECENTS_SIZE))
    }


    /////////////////////////////////////////////////////////////////

    fun getAllTextbooks(user_id : String, user_name: String?, user_email: String?) {
        if(!::user_account.isInitialized){
            user_account =  UserAccount(user_id, null,user_name,
                    user_email, null, "5/17/18",
                    0, null, null)

        }
        if (!::selling_textbooks.isInitialized) {
            selling_textbooks = mutableListOf()
            recent_selling_Textbooks = mutableListOf()
        }
        if(!::requested_textbooks.isInitialized) {
            requested_textbooks = mutableListOf()
            recent_requested_Textbooks = mutableListOf()
        }
        getUserAccount(user_id)


    }

    private fun loadTextbookselling(fragment: Fragment) {
        // Do an asynchronous operation to fetch users
        databaseManager.retrieveSellingTextbookList(user_account.user_id!!,this, fragment)

    }

    private fun loadTextbookbuying(fragment: Fragment) {
        // Do an asynchronous operation to fetch users
        // This function will gather the data from the servers, however, we are using dummy data right now
        databaseManager.retrieveRequestedTextbookList(user_account.user_id!!,this, fragment, null)
    }


    private fun getUserAccount(user_id : String){
        // Do an asynchronous operation to fetch user
        // TODO: Get data from database; using fake data for now
        databaseManager.retrieveAccount(user_id, this, null, null)
    }//getUserAccount
}


//TODO:CHECK WITH OTHER EZ-BOOKS MEMBERS TO MAKE SURE INFORMATION IS VALID!!
//TODO: ADD A MUTABLE MAP IN TEXTBOOKS TO ALLOW THE SELLER TO CLEAR USERS TO VIEW THEIR INFORMATION
data class Textbooks( val book_id: Long, var Title:String, var isbn:String, var book_img: ByteArray?, var instructor:String?, var course:String?,
                     var affiliated_account:UserAccount?, var potential_buyers: MutableList<Potential_Buyer>?)
data class UserAccount(var user_id: String?, var profile_img: ByteArray?, var user_name:String?, var email_address:String?,
                    var phone_number:String?, val date_joined:String?, var account_status:Int,
                    var class_standing:String?, var reported_flags:Reports?)
data class Reports(var num_of_flags:Int, var reported_reasons: MutableMap<String, Int>?,
                   var other_reason_log : ArrayList<String>?)
data class Potential_Buyer(val account_id:String, var account_name:String, var approved:Boolean)

data class Searched_Textbooks(val userid : String?,
                              val bookid : Long?,
                              val user_name : String?,
                              val user_email: String?,
                              val user_phone: String?,
                              val title : String?,
                              val isbn : String?,
                              val course : String?,
                              val instructor : String?,
                              val book_img : ByteArray?,
                              var potential_buyers: MutableList<Potential_Buyer>?) : Serializable