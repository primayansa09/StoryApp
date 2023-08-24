package com.example.mystories.preferences

import android.content.Context
import android.content.SharedPreferences


class UserPreference(contet: Context) {
    companion object{
        private const val PREF_NAME = "preferences"
        private const val IS_LOGIN = "is_login"
        private const val TOKEN = "token"
    }
    private val preference = contet.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = preference.edit()

    fun setToken(token: String?){
        editor.putString(TOKEN, token)
        editor.apply()
    }

    fun getToken(): String? {
        return preference.getString(TOKEN, null)
    }

    fun setSession(value: Boolean){
        editor.putBoolean(IS_LOGIN, value)
        editor.apply()
    }

    fun isLogin(): Boolean{
        return preference.getBoolean(IS_LOGIN, false)
    }

    fun clearToken(){
        editor.clear()
        editor.apply()
    }
}