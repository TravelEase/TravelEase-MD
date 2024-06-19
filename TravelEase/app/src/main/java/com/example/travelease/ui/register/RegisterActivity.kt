package com.example.travelease.ui.register

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.travelease.SavedActivity
import com.example.travelease.databinding.ActivityRegisterBinding
import com.example.travelease.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)


        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance("https://travelease-7e34c-default-rtdb.asia-southeast1.firebasedatabase.app")

        binding.loginText.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        setupAction()
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            startActivity(Intent(this, SavedActivity::class.java))

            val username = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val pass = binding.edRegisterPassword.text.toString()
            val confirmPass = binding.edConfirmPassword.text.toString()

            if (username.isNotEmpty() && email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
                if (pass == confirmPass) {
                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val uid = firebaseAuth.currentUser!!.uid
                            val usernameRef = database.getReference("users/$uid")
                            val user = hashMapOf<String, Any>(
                                "username" to username,
                                "email" to email
                            )

                            usernameRef.setValue(user).addOnCompleteListener { innerTask ->
                                if (innerTask.isSuccessful) {
                                    Toast.makeText(this, "Akun Berhasil Terdaftar", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, LoginActivity::class.java)
                                    startActivity(intent)
                                } else {
                                    Log.e("RegisterActivity", "Error writing to database:", innerTask.exception)
                                }
                            }
                        }else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()

                        }
                    }
                } else {
                    Toast.makeText(this, "Password tidak cocok", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Form tidak boleh kosong", Toast.LENGTH_SHORT).show()

            }
        }
    }
}


