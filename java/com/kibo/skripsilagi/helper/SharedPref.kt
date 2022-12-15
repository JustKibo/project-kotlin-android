package com.kibo.skripsilagi.helper

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.kibo.skripsilagi.model.User

class SharedPref (activity: Activity) {

    val login = "Login"
    val nama = "nama"
    val phone = "phone"
    val email = "email"

    val user = "user"

    val mypref = "Main_PRF"
    val s : SharedPreferences

    init {
        s = activity.getSharedPreferences(mypref, Context.MODE_PRIVATE)
    }

    fun setStatusLogin(status:Boolean){
        s.edit().putBoolean(login, status).apply()

    }

    fun  getStatusLogin():Boolean{
        return s.getBoolean(login, false)
    }

    fun setUser(Value: User){
        val data : String = Gson().toJson(Value, User::class.java)
        s.edit().putString(user, data).apply()
    }

    fun getUser(): User? {
        val data = s.getString(user, null) ?: return null
        return Gson().fromJson<User>(data, User::class.java)
    }

    fun setString(key: String, Value: String){
        s.edit().putString(key, Value).apply()
    }

    fun getString(key: String): String{
        return s.getString(key, "")!!
    }
}