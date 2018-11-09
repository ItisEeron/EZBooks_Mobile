package com.example.mac.ezbooks.ui.main

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mac.ezbooks.HomeFragment
import com.example.mac.ezbooks.R
import kotlinx.android.synthetic.main.edit_user_account_layout.*
import kotlinx.android.synthetic.main.edit_user_account_layout.view.*
import android.content.pm.PackageManager
import android.graphics.Bitmap


class EditAccountFragment : Fragment(){

    private lateinit var ezbooksViewModel: MainViewModel
    private lateinit var main_account : UserAccount

    override fun onCreate(savedInstanceState : Bundle?) {
        //Super allows the original function to execute then you add your own code
        super.onCreate(savedInstanceState)
        activity?.title = "Edit Your Account"

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.edit_user_account_layout, container, false)

        ezbooksViewModel = activity?.run {
            ViewModelProviders.of(this).get(MainViewModel::class.java) }
                ?: throw Exception("Invalid Activity")

        main_account = ezbooksViewModel.user_account


        //TODO: Make is so that hints show
        view.submit_account_changes_button.setOnClickListener{
            val fragmentManager = activity?.supportFragmentManager

            //Make Sure that actual edits have been inputted before submitted
            main_account.user_name = if(view.name_editText.text.toString().isBlank())
                main_account.user_name else view.name_editText.text.toString()

            main_account.phone_number = if(view.phone_number_editText.text.toString().isBlank())
                main_account.phone_number else view.phone_number_editText.text.toString()

            main_account.email_address = if(view.email_editText.text.toString().isBlank())
                main_account.email_address else view.email_editText.text.toString()

            main_account.class_standing = if(view.class_standing_editText.text.toString().isBlank())
                main_account.class_standing else view.class_standing_editText.text.toString()

            fragmentManager?.popBackStack()
            //Task to keep the home page labels intact
            activity?.findViewById<NavigationView>(R.id.nav_view)?.setCheckedItem(R.id.nav_home)
            activity?.title ="EZ Books Home"


        }
        view.reset_credentials.setOnClickListener{
            view.name_editText.text.clear()
            view.phone_number_editText.text.clear()
            view.email_editText.text.clear()
            view.class_standing_editText.text.clear()
        }

        view.upload_image_button.setOnClickListener{

        }

        return view
    }



}