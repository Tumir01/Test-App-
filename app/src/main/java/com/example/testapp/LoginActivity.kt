package com.example.testapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.testapp.User.Companion.listOfUsers
import com.example.testapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var bindingClass : ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingClass = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(bindingClass.root)
        bindingClass.apply {
            loginButton.setOnClickListener{
                User("test", "test")

                val login = userLogin.text.toString().trim()
                val password = userPassword.text.trim().toString()

                if (login.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this@LoginActivity, "Not all fields are filled in!", Toast.LENGTH_LONG).show()
                } else if (!listOfUsers.containsKey(login)) {
                    Toast.makeText(this@LoginActivity, "User is not found", Toast.LENGTH_LONG).show()
                } else if (listOfUsers[login] != password) {
                    Toast.makeText(this@LoginActivity, "Incorrect password", Toast.LENGTH_LONG).show()
                } else if (listOfUsers[login] == password) {
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
        fun hideSystemUI() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.insetsController?.let { controller ->
                    controller.hide(WindowInsets.Type.systemBars())
                    controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            }
        }
        hideSystemUI()
    }
}