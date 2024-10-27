package com.timeit.habito.data.api

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.timeit.habito.data.RetrofitClient
import com.timeit.habito.data.dataModels.LoginRequest
import com.timeit.habito.data.dataModels.LoginResponse
import com.timeit.habito.data.dataModels.SignUpRequest
import com.timeit.habito.ui.activities.LoginActivity
import com.timeit.habito.ui.activities.MainActivity
import com.timeit.habito.ui.activities.SignUpActivity
import com.timeit.habito.utils.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Authentication {

    fun login(context: Context, tokenManager: TokenManager,email: String, password: String) {
        val loginRequest = LoginRequest(email, password)

        RetrofitClient.apiService.login(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val token = response.body()?.access_token
                    val username = response.body()?.username
                    if (token != null && username != null) {
                        tokenManager.saveToken(token,username)
                        Log.d("token",token)
                        val intent = Intent(context, MainActivity::class.java)
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        context.startActivity(intent)
                    }
                } else {
                    Toast.makeText(context, "Login Failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun signup(context: Context, signUpRequest: SignUpRequest) {
        RetrofitClient.apiService.signup(signUpRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                        val intent = Intent(context, LoginActivity::class.java)
                        context.startActivity(intent)
                } else {
                    Toast.makeText(context, "Signup Failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}