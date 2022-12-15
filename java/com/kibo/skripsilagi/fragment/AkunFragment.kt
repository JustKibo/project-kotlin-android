package com.kibo.skripsilagi.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import com.kibo.skripsilagi.MainActivity
import com.kibo.skripsilagi.R
import com.kibo.skripsilagi.activity.LoginActivity
import com.kibo.skripsilagi.activity.RiwayatActivity
import com.kibo.skripsilagi.activity.TambahAlamatActivity
import com.kibo.skripsilagi.helper.SharedPref

class AkunFragment : Fragment() {

    lateinit var s:SharedPref
    lateinit var btnlogout:TextView
    lateinit var tvNama:TextView
    lateinit var tvPhone:TextView
    lateinit var tvEmail:TextView

    lateinit var btnRiwayat: RelativeLayout
    lateinit var btnSettingAlamat: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view: View = inflater.inflate(R.layout.fragment_akun, container, false)
        init(view)

        s = SharedPref(requireActivity())

        mainButton()
        setData()
        return view
    }

    fun mainButton(){
        btnlogout.setOnClickListener {
            s.setStatusLogin(false)
        }

        btnRiwayat.setOnClickListener {
            startActivity(Intent(requireActivity(), RiwayatActivity::class.java))
        }

        btnSettingAlamat.setOnClickListener {
            startActivity(Intent(requireActivity(), TambahAlamatActivity::class.java))
        }
    }

    fun setData(){

        if (s.getUser() == null){
            val intent = Intent(activity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            return
        }

        val user = s.getUser()!!

        tvNama.text = user.name
        tvEmail.text = user.email
        tvPhone.text = user.phone
    }

    private fun init(view: View) {
        btnlogout = view.findViewById(R.id.btn_logout)
        tvNama = view.findViewById(R.id.tv_nama)
        tvEmail = view.findViewById(R.id.tv_email)
        tvPhone = view.findViewById(R.id.tv_phone)
        btnRiwayat = view.findViewById(R.id.btn_riwayat)
        btnSettingAlamat = view.findViewById(R.id.btn_settingAlamat)
    }
}