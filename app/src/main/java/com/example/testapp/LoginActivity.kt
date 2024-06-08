package com.example.testapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.testapp.databinding.ActivityLoginBinding
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    private lateinit var bindingClass: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingClass = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(bindingClass.root)

        val mDateBase = FirebaseFirestore.getInstance()

        bindingClass.apply {
            loginButton.setOnClickListener {
                val login = userLogin.text.toString().trim()
                val password = userPassword.text.toString().trim()

                if (login.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this@LoginActivity, R.string.not_all_fields_are_filled_in, Toast.LENGTH_LONG).show()
                } else {
                    mDateBase.collection("users").get().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val documents = task.result
                            var userFound = false
                            for (document in documents) {
                                val user = document.toObject(User::class.java)
                                if (user.login == login) {
                                    userFound = true
                                    if (user.password == password) {
                                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        Toast.makeText(this@LoginActivity, R.string.incorrect_password, Toast.LENGTH_LONG).show()
                                    }
                                    break
                                }
                            }
                            if (!userFound) {
                                Toast.makeText(this@LoginActivity, R.string.user_is_not_found, Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Toast.makeText(this@LoginActivity, "Error getting documents: ${task.exception}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }

            textViewIsLogin?.setOnClickListener {
                val intent = Intent(this@LoginActivity, RegistrationActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        hideSystemUI()
    }

    private fun hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let { controller ->
                controller.hide(WindowInsets.Type.systemBars())
                controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
    }
}