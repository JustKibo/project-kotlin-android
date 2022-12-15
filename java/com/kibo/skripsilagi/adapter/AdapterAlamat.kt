package com.kibo.skripsilagi.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.kibo.skripsilagi.R
import com.kibo.skripsilagi.activity.DetailProdukActivity
import com.kibo.skripsilagi.activity.LoginActivity
import com.kibo.skripsilagi.helper.Helper
import com.kibo.skripsilagi.model.Alamat
import com.kibo.skripsilagi.model.Produk
import com.kibo.skripsilagi.util.Config
import com.squareup.picasso.Picasso
import java.text.NumberFormat
import java.util.*
import javax.xml.transform.ErrorListener
import kotlin.collections.ArrayList

class AdapterAlamat (var data:ArrayList<Alamat>, var listener: Listeners): RecyclerView.Adapter<AdapterAlamat.Holder>() {

    class Holder(view: View):RecyclerView.ViewHolder(view){
        val tvNama = view.findViewById<TextView>(R.id.tv_nama)
        val tvPhone = view.findViewById<TextView>(R.id.tv_phone)
        val tvAlamat = view.findViewById<TextView>(R.id.tv_alamat)
        val layout = view.findViewById<CardView>(R.id.layout)
        val rd = view.findViewById<RadioButton>(R.id.rd_alamat)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val View = LayoutInflater.from(parent.context).inflate(R.layout.item_alamat, parent, false)
        return Holder(View)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {

        val a = data[position]

        holder.rd.isChecked = a.isSelected
        holder.tvNama.text = a.name
        holder.tvPhone.text = a.phone
        holder.tvAlamat.text = a.alamat +", "+a.kota +", "+a.kecamatan +", "+a.kodepos +", ("+a.type +")"

        holder.rd.setOnClickListener {
            a.isSelected = true
            listener.onClicked(a)
        }

        holder.layout.setOnClickListener {
            a.isSelected = true
            listener.onClicked(a)
        }
    }

    interface Listeners{
        fun onClicked(data:Alamat)
    }
}