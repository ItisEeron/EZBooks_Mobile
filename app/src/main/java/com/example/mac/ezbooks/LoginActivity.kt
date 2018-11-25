package com.example.mac.ezbooks

import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.NonNull
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.example.mac.ezbooks.R.styleable.View
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var inputEmail : EditText
    private lateinit var inputPassword : EditText
    private lateinit var auth : FirebaseAuth
    private lateinit var progressBar : ProgressDialog
    private lateinit var btnSignup : Button
    private lateinit var btnLogin : Button
    //private lateinit var btnReset: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_page)


        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // set the view now
        setContentView(R.layout.login_page)


        progressBar = ProgressDialog(this)
        inputEmail = findViewById(R.id.login_user_account)
        inputPassword =  findViewById(R.id.login_user_password)
        btnSignup = findViewById(R.id.create_account_button)
        btnLogin = findViewById(R.id.login_button)
        //btnReset = (Button) findViewById(R.id.btn_reset_password)

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance()

        btnSignup.setOnClickListener{
           startActivity(Intent(this@LoginActivity, CreateAccountActivity::class.java))

        }

        /*
         btnReset.setOnClickListener(new View.OnClickListener() {
         @Override
          public void onClick(View v) {
          startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
         }
         });
        */

        btnLogin.setOnClickListener{
            var email = inputEmail.text.toString()
            var password = inputPassword.text.toString()

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Enter email address!", Toast.LENGTH_SHORT).show();
            }
            else if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Enter password!", Toast.LENGTH_SHORT).show();
            }
            else {
                //authenticate user
                progressBar!!.setMessage("Logging in...")
                progressBar!!.show()
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            if(task.isSuccessful){
                                updateUI()
                            }
                            else{
                                Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                            }
                        }
            }
        }
    }

    private fun updateUI() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}
