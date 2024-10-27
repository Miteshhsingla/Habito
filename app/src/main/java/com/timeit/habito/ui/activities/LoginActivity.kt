package com.timeit.habito.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.timeit.habito.R
import com.timeit.habito.data.api.Authentication
import com.timeit.habito.databinding.ActivityLoginBinding
import com.timeit.habito.utils.TokenManager
import javax.inject.Inject

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: Authentication
    @Inject
    lateinit var tokenManager: TokenManager

    private lateinit var email: String
    private lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenManager = TokenManager(this)
        auth = Authentication()

        val token = tokenManager.getToken()
        if (token != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.tvSignIn.setOnClickListener {
            val intent = Intent(this,SignUpActivity::class.java)
            startActivity(intent)
        }
        binding.btnSignIn.setOnClickListener {
            email = binding.etEmailAddress.text.toString()
            password = binding.etPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show()
            } else {
                auth.login(this, tokenManager, email, password)
            }
        }

    }
}