package com.timeit.habito.utils

import android.content.Context

import com.timeit.habito.utils.Constants.PREFS_TOKEN_FILE
import com.timeit.habito.utils.Constants.ROLE
import com.timeit.habito.utils.Constants.USERNAME
import com.timeit.habito.utils.Constants.USER_TOKEN
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TokenManager @Inject constructor(@ApplicationContext context: Context) {

    private var prefs = context.getSharedPreferences(PREFS_TOKEN_FILE, Context.MODE_PRIVATE)

    fun saveToken(token : String,username:String){
        val editor = prefs.edit()
        editor.putString(USER_TOKEN,token)
        editor.putString(USERNAME,username)
        editor.apply()
    }

    fun getToken() : String? {
        return prefs.getString(USER_TOKEN,null)
    }

    fun getUsername() : String?{
        return prefs.getString(USERNAME, null)
    }

    fun clearToken() {
        val editor = prefs.edit()
        editor.remove(USER_TOKEN)
        editor.remove(ROLE)
        editor.remove(USERNAME)
        editor.apply()
    }
}