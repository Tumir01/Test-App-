package com.example.testapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.testapp.databinding.ActivityRegistrationBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class RegistrationActivity : AppCompatActivity() {
    private lateinit var bindingClass : ActivityRegistrationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingClass = ActivityRegistrationBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(bindingClass.root)
        bindingClass.apply {

            val mDateBase = Firebase.firestore

            textViewIsLogin.setOnClickListener{
                val intent = Intent(this@RegistrationActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }

            signUpButton.setOnClickListener{
                val login = userLoginRegistration.text.toString().trim()
                val password = userPasswordRegistration.text.toString().trim()
                val email = userEmailRegistration.text.toString().trim()


                if (login.isEmpty() || password.isEmpty() || email.isEmpty()) {
                    Toast.makeText(this@RegistrationActivity, R.string.not_all_fields_are_filled_in, Toast.LENGTH_LONG).show()
                } else {
                    User(login, password, email)
                    mDateBase.collection("users").document().set(User(login, password, email))
                    val intent = Intent(this@RegistrationActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }
}