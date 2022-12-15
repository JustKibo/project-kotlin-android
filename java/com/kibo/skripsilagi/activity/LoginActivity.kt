package com.kibo.skripsilagi.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.kibo.skripsilagi.MainActivity
import com.kibo.skripsilagi.R
import com.kibo.skripsilagi.app.ApiConfig
import com.kibo.skripsilagi.helper.SharedPref
import com.kibo.skripsilagi.model.ResponModel
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    lateinit var s:SharedPref
    lateinit var fcm:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        s = SharedPref(this)
        getFcm()

        btn_login.setOnClickListener {
            login()
        }
    }

    private fun getFcm(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("Respon", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            fcm = token.toString()
            // Log and toast
            Log.d("respon fcm:", token.toString())
        })
    }

    fun login (){
            if  (edt_email.text.isEmpty()){
                edt_email.error = "Kolom Email Tidak Boleh Kosong"
                edt_email.requestFocus()
                return
            } else if (edt_password.text.isEmpty()){
                edt_password.error = "Kolom Password Tidak Boleh Kosong"
                edt_password.requestFocus()
                return
            }


            pb.visibility = View.VISIBLE
            ApiConfig.instanceRetrofit.login(edt_email.text.toString(), edt_password.text.toString(), fcm).enqueue(object : Callback<ResponModel> {

                override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                    pb.visibility = View.GONE

                    val respon = response.body()!!

                    if (respon.success == 1){
                        s.setStatusLogin(true)
                        s.setUser(respon.user)
 //                       s.setString(s.nama, respon.user.name)
 //                       s.setString(s.phone, respon.user.phone)
 //                       s.setString(s.email, respon.user.email)

                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                        Toast.makeText(this@LoginActivity, "Selamat datang:"+respon.user.name, Toast.LENGTH_SHORT).show()
                    } else{
                        Toast.makeText(this@LoginActivity, "Error:"+respon.message, Toast.LENGTH_SHORT).show()

                    }
                }

                override fun onFailure(call: Call<ResponModel>, t: Throwable) {
                    pb.visibility = View.GONE
                    Toast.makeText(this@LoginActivity, "Error:"+t.message, Toast.LENGTH_SHORT).show()
                }

            })
    }
}