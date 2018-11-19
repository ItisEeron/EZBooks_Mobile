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


class MainViewModel : ViewModel() {
    lateinit var selling_textbooks: MutableList<Textbooks>
    lateinit var selected_selling: Textbooks
    lateinit var user_account:UserAccount
    lateinit var requested_textbooks: MutableList<Textbooks>
    lateinit var selected_requested: Textbooks
    lateinit var recent_requested_Textbooks: MutableList<Textbooks>
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
        getAllTextbooks()
    }


    /////////////////////////////////////////////////////////////////

    fun getAllTextbooks() {
       if (!::selling_textbooks.isInitialized) {
            loadTextbookselling()
        }
        if(!::requested_textbooks.isInitialized) {
            loadTextbookbuying()
        }
        if(!::user_account.isInitialized){
            user_account =  UserAccount(null, null,null,
                    null, null, "5/17/18",
                    0, null, null)
            //getUserAccount()

        }

    }

    private fun loadTextbookselling() {
        // Do an asynchronous operation to fetch users
        // This function will gather the data from the servers, however, we are using dummy data right now
        // TODO: Get data from database; using fake data for now
        selling_textbooks = mutableListOf()
        selling_textbooks.addAll(listOf(
                Textbooks("Of Mice and Men", "904585849", null,"Jeane Briggs",
                        "English 101", UserAccount(104955L, null,"Eeron Grant",
                        "eerongrant@gmail.com", "9255663812", "5/17/18",
                        0, "Senior", Reports(0, null,
                        null)), mutableListOf(Potential_Buyer(104955L, "Eeron Grant", true), Potential_Buyer(55005L, "Serena Grant", false),
                        Potential_Buyer(55005L, "Serena Grant", false))),
                Textbooks("Of Mice and Men", "904585849", null,"Jeane Briggs",
                        "English 101", UserAccount(104955L, null,"Eeron Grant",
                        "eerongrant@gmail.com", "9255663812", "5/17/18",
                        0, "Senior", Reports(0, null,
                        null)), null),
                Textbooks("Of Mice and Men", "904585849", null,"Jeane Briggs",
                        "English 101", UserAccount(104955L, null,"Eeron Grant",
                        "eerongrant@gmail.com", "9255663812", "5/17/18",
                        0, "Senior", Reports(0, null,
                        null)) , null),
                Textbooks("Of Mice and Men", "904585849", null,"Jeane Briggs",
                        "English 101", UserAccount(104955L, null,"Eeron Grant",
                        "eerongrant@gmail.com", "9255663812", "5/17/18",
                        0, "Senior", Reports(0, null,
                        null)),mutableListOf(Potential_Buyer(104955L, "Eeron Grant", true))),
                Textbooks("Of Mice and Men", "904585849", null,"Jeane Briggs",
                        "English 101", UserAccount(104955L, null,"Eeron Grant",
                        "eerongrant@gmail.com", "9255663812", "5/17/18",
                        0, "Senior", Reports(0, null,
                        null)), mutableListOf(Potential_Buyer(104955L, "Eeron Grant", true))),
                Textbooks("Of Mice and Men", "904585849", null,"Jeane Briggs",
                        "English 101", UserAccount(104955L, null,"Eeron Grant",
                        "eerongrant@gmail.com", "9255663812", "5/17/18",
                        0, "Senior", Reports(0, null,
                        null)), null),
                Textbooks("Of Mice and Men", "904585849", null,"Jeane Briggs",
                        "English 101", UserAccount(104955L, null,"Eeron Grant",
                        "eerongrant@gmail.com", "9255663812", "5/17/18",
                        0, "Senior", Reports(0, null,
                        null)), null ),
                Textbooks("Of Mice and Men", "904585849", null,"Jeane Briggs",
                        "English 101", UserAccount(104955L, null,"Eeron Grant",
                        "eerongrant@gmail.com", "9255663812", "5/17/18",
                        0, "Senior", Reports(0, null,
                        null)), mutableListOf(Potential_Buyer(104955L, "Eeron Grant", true)))))

        recent_selling_Textbooks = mutableListOf()
        recent_selling_Textbooks.addAll(selling_textbooks.subList(0, RECENTS_SIZE))



    }

    private fun loadTextbookbuying() {
        // Do an asynchronous operation to fetch users
        // This function will gather the data from the servers, however, we are using dummy data right now
        // TODO: Get data from database; using fake data for now
        requested_textbooks = mutableListOf()
        requested_textbooks.addAll(listOf(
                Textbooks("Of Mice and Men", "904585849", null,"Jeane Briggs",
                        "English 101", UserAccount(104955L, null,"Eeron Grant",
                        "eerongrant@gmail.com", "9255663812", "5/17/18",
                        0, "Senior", Reports(0, null,
                        null)), mutableListOf(Potential_Buyer(104955L, "Eeron Grant", true))),
                Textbooks("Of Mice and Men", "904585849", null,"Jeane Briggs",
                        "English 101", UserAccount(104955L, null,"Eeron Grant",
                        "eerongrant@gmail.com", "9255663812", "5/17/18",
                        0, "Senior", Reports(0, null,
                        null)), null),
                Textbooks("Of Mice and Men", "904585849", null,"Jeane Briggs",
                        "English 101", UserAccount(104955L, null,"Eeron Grant",
                        "eerongrant@gmail.com", "9255663812", "5/17/18",
                        0, "Senior", Reports(0, null,
                        null)) , null),
                Textbooks("Of Mice and Men", "904585849", null,"Jeane Briggs",
                        "English 101", UserAccount(104955L, null,"Eeron Grant",
                        "eerongrant@gmail.com", "9255663812", "5/17/18",
                        0, "Senior", Reports(0, null,
                        null)),mutableListOf(Potential_Buyer(104955L, "Eeron Grant", true))),
                Textbooks("Of Mice and Men", "904585849", null,"Jeane Briggs",
                        "English 101", UserAccount(104955L, null,"Eeron Grant",
                        "eerongrant@gmail.com", "9255663812", "5/17/18",
                        0, "Senior", Reports(0, null,
                        null)), mutableListOf(Potential_Buyer(104955L, "Eeron Grant", true))),
                Textbooks("Of Mice and Men", "904585849", null,"Jeane Briggs",
                        "English 101", UserAccount(104955L, null,"Eeron Grant",
                        "eerongrant@gmail.com", "9255663812", "5/17/18",
                        0, "Senior", Reports(0, null,
                        null)), null),
                Textbooks("Of Mice and Men", "904585849", null,"Jeane Briggs",
                        "English 101", UserAccount(104955L, null,"Eeron Grant",
                        "eerongrant@gmail.com", "9255663812", "5/17/18",
                        0, "Senior", Reports(0, null,
                        null)), null ),
                Textbooks("Of Mice and Men", "904585849", null,"Jeane Briggs",
                        "English 101", UserAccount(104955L, null,"Eeron Grant",
                        "eerongrant@gmail.com", "9255663812", "5/17/18",
                        0, "Senior", Reports(0, null,
                        null)), mutableListOf(Potential_Buyer(104955L, "Eeron Grant", false)))))

        recent_requested_Textbooks = mutableListOf()
        recent_requested_Textbooks.addAll(requested_textbooks.subList(0, RECENTS_SIZE))
    }


    fun getUserAccount(fragment: Fragment){
        // Do an asynchronous operation to fetch user
        // TODO: Get data from database; using fake data for now
        databaseManager.retrieveAccount(104955L, this, fragment)
    }//getUserAccount
}


//TODO:CHECK WITH OTHER EZ-BOOKS MEMBERS TO MAKE SURE INFORMATION IS VALID!!
//TODO: ADD A MUTABLE MAP IN TEXTBOOKS TO ALLOW THE SELLER TO CLEAR USERS TO VIEW THEIR INFORMATION
data class Textbooks(var Title:String, var isbn:String, var book_img: ByteArray?, var instructor:String?, var course:String?,
                     val affiliated_account:UserAccount, var potential_buyers: MutableList<Potential_Buyer>?)
data class UserAccount(var user_id:Long?, var profile_img: ByteArray?, var user_name:String?, var email_address:String?,
                    var phone_number:String?, val date_joined:String?, var account_status:Int,
                    var class_standing:String?, var reported_flags:Reports?)
data class Reports(var num_of_flags:Int, var reported_reasons: MutableMap<String, Int>?,
                   var other_reason_log : ArrayList<String>?)
data class Potential_Buyer(val account_id:Long, var account_name:String, var approved:Boolean)
