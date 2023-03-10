package com.kibo.skripsilagi.helper

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.toolbar.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class Helper {
    fun gantiRupiah(string: String) :String{
        return NumberFormat.getCurrencyInstance(Locale("in" , "ID")).format(Integer.valueOf(string))
    }

    fun gantiRupiah(value: Int) :String{
        return NumberFormat.getCurrencyInstance(Locale("in" , "ID")).format(value)
    }

    fun gantiRupiah(value: Boolean) :String{
        return NumberFormat.getCurrencyInstance(Locale("in" , "ID")).format(value)
    }

    fun setToolbar(activity: Activity, toolbar: Toolbar, title: String){
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        activity.supportActionBar!!.title = title
        activity.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    fun convertTanggal(tgl: String, formatBaru: String, formatLama: String = "yyy-MM-dd kk:mm:ss") :String{
        val dateFormat = SimpleDateFormat(formatLama)
        val convert = dateFormat.parse(tgl)
        dateFormat.applyPattern(formatBaru)
        return dateFormat.format(convert)
    }
}