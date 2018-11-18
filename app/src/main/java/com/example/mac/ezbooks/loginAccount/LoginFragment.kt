package com.example.mac.ezbooks.loginAccount

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mac.ezbooks.R
import kotlinx.android.synthetic.main.login_page.view.*

class LoginFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.login_page, container, false)
        view.login_button.setOnClickListener{
           var entryCount = activity!!.supportFragmentManager.backStackEntryCount
            activity!!.supportFragmentManager.popBackStack()
        }
        view.create_account_button.setOnClickListener{
            var entryCount = activity!!.supportFragmentManager.backStackEntryCount
            activity!!.supportFragmentManager.beginTransaction().replace(R.id.flContent,
                    CreateAccount()).addToBackStack("createAccount").commit()
        }

        return view
    }
}