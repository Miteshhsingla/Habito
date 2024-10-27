package com.timeit.habito.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.timeit.habito.R
import com.timeit.habito.data.api.Authentication
import com.timeit.habito.data.dataModels.SignUpRequest
import com.timeit.habito.databinding.ActivitySignUpBinding
import com.timeit.habito.utils.TokenManager
import javax.inject.Inject

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var email: String
    private lateinit var username: String
    private lateinit var password: String
    private lateinit var auth : Authentication



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Authentication()

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setSupportActionBar(binding.toolbar)
            setHomeAsUpIndicator(R.drawable.ic_android_back)
        }

        binding.btnSignUp.setOnClickListener {
            signUpUser()
        }
        binding.tvSignIn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signUpUser() {
        email = binding.etEmailAddress.text.toString()
        username = binding.etUsername.text.toString()
        password = binding.etPassword.text.toString()

        val signUpRequest = SignUpRequest(email,username,password)
        auth.signup(this,signUpRequest)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}