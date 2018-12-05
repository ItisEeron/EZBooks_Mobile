package com.example.mac.ezbooks.ui.main

import android.arch.lifecycle.ViewModel
import android.support.design.widget.NavigationView
import com.example.mac.ezbooks.di.FirebaseDatabaseManager
import java.io.Serializable


class MainViewModel : ViewModel() {
    lateinit var selling_textbooks: MutableList<Textbooks>
    lateinit var selected_selling: Textbooks
    lateinit var user_account:UserAccount
    lateinit var requested_textbooks: MutableList<Searched_Textbooks>
    lateinit var selected_requested: Searched_Textbooks
    lateinit var recent_requested_Textbooks: MutableList<Searched_Textbooks>
    lateinit var recent_selling_Textbooks: MutableList<Textbooks>
    private val databaseManager = FirebaseDatabaseManager()


    fun getAllTextbooks(user_id : String, user_name: String?, user_email: String?, navigationView: NavigationView?) {
        if(!::user_account.isInitialized){
            user_account =  UserAccount(user_id, null,user_name,
                    user_email, null, "5/17/18",
                    0, null)

        }
        if (!::selling_textbooks.isInitialized) {
            selling_textbooks = mutableListOf()
            recent_selling_Textbooks = mutableListOf()
        }
        if(!::requested_textbooks.isInitialized) {
            requested_textbooks = mutableListOf()
            recent_requested_Textbooks = mutableListOf()
        }
        getUserAccount(user_id, navigationView)


    }

    private fun getUserAccount(user_id : String, navigationView: NavigationView?){
        // Do an asynchronous operation to fetch user
        databaseManager.retrieveAccount(user_id, this, navigationView)
    }//getUserAccount
}


//TODO:CHECK WITH OTHER EZ-BOOKS MEMBERS TO MAKE SURE INFORMATION IS VALID!!
data class Textbooks( val book_id: Long, var Title:String, var isbn:String, var book_img: ByteArray?, var instructor:String?, var course:String?,
                     var affiliated_account:UserAccount?, var potential_buyers: MutableList<Potential_Buyer>?)
data class UserAccount(var user_id: String?, var profile_img: ByteArray?, var user_name:String?, var email_address:String?,
                    var phone_number:String?, val date_joined:String?, var account_status:Int,
                    var class_standing:String?)
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