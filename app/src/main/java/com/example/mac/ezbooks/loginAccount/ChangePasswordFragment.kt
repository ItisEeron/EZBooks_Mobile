package com.example.mac.ezbooks.loginAccount

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.mac.ezbooks.MainActivity
import com.example.mac.ezbooks.R
import com.google.firebase.auth.FirebaseAuth

class ChangePasswordFragment : Fragment() {
    private var etEmail: EditText? = null
    private var btnSubmit: Button? = null
    //Firebase references
    private var mAuth: FirebaseAuth? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.password_reset_layout, container, false)
        etEmail = view.findViewById(R.id.et_email)
        btnSubmit = view.findViewById(R.id.btn_submit)
        mAuth = FirebaseAuth.getInstance()
        btnSubmit!!.setOnClickListener { sendPasswordResetEmail() }

        return view
    }

    private fun sendPasswordResetEmail() {
        val email = etEmail?.text.toString()
        if (!TextUtils.isEmpty(email)) {
            mAuth!!
                    .sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val message = "Email sent."
                            Log.d("Eeron Tag", message)
                            Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show()
                            updateUI()
                        } else {
                            Log.w("Eeron Tag", task.exception!!.message)
                            Toast.makeText(this.context, "No user found with this email.", Toast.LENGTH_SHORT).show()
                        }
                    }
        } else {
            Toast.makeText(this.context, "Enter Email", Toast.LENGTH_SHORT).show()
        }
    }
    private fun updateUI() {
        //start next activity
        activity?.supportFragmentManager?.popBackStack()
    }



}