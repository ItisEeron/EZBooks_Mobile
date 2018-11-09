package com.example.mac.ezbooks.ui.main

import android.arch.lifecycle.ViewModel


class MainViewModel : ViewModel() {
    lateinit var selling_textbooks: ArrayList<Textbooks>
    lateinit var selected_selling: Textbooks
    lateinit var user_account:UserAccount
    lateinit var requested_textbooks: ArrayList<Textbooks>
    lateinit var selected_requested: Textbooks

    fun getAllTextbooks(): ArrayList<Textbooks> {
        /*if (!::selling_textbooks.isInitialized) {
            selling_textbooks = ArrayList()
            loadTextbookselling()
        }
        if(!::requested_textbooks.isInitialized) {
            requested_textbooks = ArrayList()
            loadTextbookbuying()
        }*/
        if(!::user_account.isInitialized){
            getUserAccount()
        }
        return selling_textbooks
    }

    private fun loadTextbookselling() {
        // Do an asynchronous operation to fetch users
        // This function will gather the data from the servers, however, we are using dummy data right now
        // TODO: Get data from database; using fake data for now
        selling_textbooks.add(Textbooks("Of Mice and Men", "904585849", "Jeane Briggs",
                "English 101", UserAccount(104955, "andriod_image_1.jpg" ,"Eeron Grant",
                "eerongrant@gmail.com", "9255663812", "5/17/18",
                0, "Senior", Reports(0, null,
                null))))
    }

    private fun loadTextbookbuying() {
        // Do an asynchronous operation to fetch users
        // This function will gather the data from the servers, however, we are using dummy data right now
        // TODO: Get data from database; using fake data for now
        requested_textbooks.add(Textbooks("Of Mice and Men", "904585849", "Jeane Briggs",
                "English 101", UserAccount(104955, "android_image_1.jpg","Eeron Grant",
                "eerongrant@gmail.com", "9255663812", "5/17/18",
                0, "Senior", Reports(0, null,
                null))))
    }


    fun getUserAccount():UserAccount {
        // Do an asynchronous operation to fetch users
        // This function will gather the data from the servers, however, we are using dummy data right now
        // TODO: Get data from database; using fake data for now
        user_account =  UserAccount(104955, "android_image_1.jpg","Eeron Grant",
                "eerongrant@gmail.com", "9255663812", "5/17/18",
                0, "Senior", Reports(0, null,
                null))
        return user_account
    }//getUserAccount


}

//TODO:CHECK WITH OTHER EZ-BOOKS MEMBERS TO MAKE SURE INFORMATION IS VALID!!
data class Textbooks(var Title:String, var isbn:String, var instructor:String, var course:String,
                     var affiliated_account:UserAccount)
data class UserAccount(val user_id:Long, var profile_img: String?, var user_name:String, var email_address:String,
                    var phone_number:String, val date_joined:String, var account_status:Int,
                    var class_standing:String, var reported_flags:Reports)
data class Reports(var num_of_flags:Int, var reported_reasons: MutableMap<String, Int>?,
                   var other_reason_log : ArrayList<String>?)
