package com.example.mac.ezbooks.ui.main

import android.arch.lifecycle.ViewModel
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import com.example.mac.ezbooks.R


class MainViewModel : ViewModel() {
    lateinit var selling_textbooks: MutableList<Textbooks>
    lateinit var selected_selling: Textbooks
    lateinit var user_account:UserAccount
    lateinit var requested_textbooks: MutableList<Textbooks>
    lateinit var selected_requested: Textbooks
    lateinit var recent_requested_Textbooks: MutableList<Textbooks>
    lateinit var recent_selling_Textbooks: MutableList<Textbooks>
    private val RECENTS_SIZE = 5

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
            getUserAccount()
        }

    }

    private fun loadTextbookselling() {
        // Do an asynchronous operation to fetch users
        // This function will gather the data from the servers, however, we are using dummy data right now
        // TODO: Get data from database; using fake data for now
        selling_textbooks = mutableListOf()
        selling_textbooks.addAll(listOf(
                Textbooks("Of Mice and Men", "904585849", null,"Jeane Briggs",
                "English 101", UserAccount(104955, null,"Eeron Grant",
                "eerongrant@gmail.com", "9255663812", "5/17/18",
                0, "Senior", Reports(0, null,
                null))),
                Textbooks("Of Mice and Men", "904585849", null,"Jeane Briggs",
                "English 101", UserAccount(104955, null,"Eeron Grant",
                "eerongrant@gmail.com", "9255663812", "5/17/18",
                0, "Senior", Reports(0, null,
                null))),
                Textbooks("Of Mice and Men", "904585849", null,"Jeane Briggs",
                "English 101", UserAccount(104955, null,"Eeron Grant",
                "eerongrant@gmail.com", "9255663812", "5/17/18",
                0, "Senior", Reports(0, null,
                null))),
                Textbooks("Of Mice and Men", "904585849", null,"Jeane Briggs",
                "English 101", UserAccount(104955, null,"Eeron Grant",
                "eerongrant@gmail.com", "9255663812", "5/17/18",
                0, "Senior", Reports(0, null,
                null))),
                Textbooks("Of Mice and Men", "904585849", null,"Jeane Briggs",
                "English 101", UserAccount(104955, null,"Eeron Grant",
                "eerongrant@gmail.com", "9255663812", "5/17/18",
                0, "Senior", Reports(0, null,
                null))),
                Textbooks("Of Mice and Men", "904585849", null,"Jeane Briggs",
                "English 101", UserAccount(104955, null,"Eeron Grant",
                "eerongrant@gmail.com", "9255663812", "5/17/18",
                0, "Senior", Reports(0, null,
                null))),
                Textbooks("Of Mice and Men", "904585849", null,"Jeane Briggs",
                "English 101", UserAccount(104955, null,"Eeron Grant",
                "eerongrant@gmail.com", "9255663812", "5/17/18",
                0, "Senior", Reports(0, null,
                null))),
                Textbooks("Of Mice and Men", "904585849", null,"Jeane Briggs",
                "English 101", UserAccount(104955, null,"Eeron Grant",
                "eerongrant@gmail.com", "9255663812", "5/17/18",
                0, "Senior", Reports(0, null,
                null)))))

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
                        "English 101", UserAccount(104955, null,"Eeron Grant",
                        "eerongrant@gmail.com", "9255663812", "5/17/18",
                        0, "Senior", Reports(0, null,
                        null))),
                Textbooks("Of Mice and Men", "904585849", null,"Jeane Briggs",
                        "English 101", UserAccount(104955, null,"Eeron Grant",
                        "eerongrant@gmail.com", "9255663812", "5/17/18",
                        0, "Senior", Reports(0, null,
                        null))),
                Textbooks("Of Mice and Men", "904585849", null,"Jeane Briggs",
                        "English 101", UserAccount(104955, null,"Eeron Grant",
                        "eerongrant@gmail.com", "9255663812", "5/17/18",
                        0, "Senior", Reports(0, null,
                        null))),
                Textbooks("Of Mice and Men", "904585849", null,"Jeane Briggs",
                        "English 101", UserAccount(104955, null,"Eeron Grant",
                        "eerongrant@gmail.com", "9255663812", "5/17/18",
                        0, "Senior", Reports(0, null,
                        null))),
                Textbooks("Of Mice and Men", "904585849", null,"Jeane Briggs",
                        "English 101", UserAccount(104955, null,"Eeron Grant",
                        "eerongrant@gmail.com", "9255663812", "5/17/18",
                        0, "Senior", Reports(0, null,
                        null))),
                Textbooks("Of Mice and Men", "904585849", null,"Jeane Briggs",
                        "English 101", UserAccount(104955, null,"Eeron Grant",
                        "eerongrant@gmail.com", "9255663812", "5/17/18",
                        0, "Senior", Reports(0, null,
                        null))),
                Textbooks("Of Mice and Men", "904585849", null,"Jeane Briggs",
                        "English 101", UserAccount(104955, null,"Eeron Grant",
                        "eerongrant@gmail.com", "9255663812", "5/17/18",
                        0, "Senior", Reports(0, null,
                        null))),
                Textbooks("Of Mice and Men", "904585849", null,"Jeane Briggs",
                        "English 101", UserAccount(104955, null,"Eeron Grant",
                        "eerongrant@gmail.com", "9255663812", "5/17/18",
                        0, "Senior", Reports(0, null,
                        null)))))

        recent_requested_Textbooks = mutableListOf()
        recent_requested_Textbooks.addAll(requested_textbooks.subList(0, RECENTS_SIZE))
    }


    fun getUserAccount():UserAccount {
        // Do an asynchronous operation to fetch users

        //DO work in SucessListener for reading from database.. This will allow information to update
        //AFTER the data has been retrieved
        // This function will gather the data from the servers, however, we are using dummy data right now
        // TODO: Get data from database; using fake data for now
        user_account =  UserAccount(104955, null,"Eeron Grant",
                "eerongrant@gmail.com", "9255663812", "5/17/18",
                0, "Senior", Reports(0, null,
                null))
        return user_account
    }//getUserAccount


}


//TODO:CHECK WITH OTHER EZ-BOOKS MEMBERS TO MAKE SURE INFORMATION IS VALID!!
data class Textbooks(var Title:String, var isbn:String, var book_img: ByteArray?, var instructor:String, var course:String,
                     val affiliated_account:UserAccount)
data class UserAccount(val user_id:Long, var profile_img: ByteArray?, var user_name:String, var email_address:String,
                    var phone_number:String, val date_joined:String, var account_status:Int,
                    var class_standing:String, var reported_flags:Reports)
data class Reports(var num_of_flags:Int, var reported_reasons: MutableMap<String, Int>?,
                   var other_reason_log : ArrayList<String>?)
