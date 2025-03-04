package com.timeit.habito.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.timeit.habito.R
import com.timeit.habito.databinding.ActivityProfileBinding
import com.timeit.habito.utils.TokenManager
import javax.inject.Inject

class ProfileActivity : AppCompatActivity() {
    @Inject
    lateinit var tokenManager: TokenManager
    private lateinit var binding: ActivityProfileBinding
    private lateinit var username: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        tokenManager = TokenManager(this)


        binding.username.text = tokenManager.getUsername()
        val email = tokenManager.getEmail().toString()
        binding.email.text = email
        binding.btnLogout.setOnClickListener {
            tokenManager.clearToken()
            Toast.makeText(this,"Logged Out Successfully",Toast.LENGTH_LONG).show()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}