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
import com.kibo.skripsilagi.model.DetailTransaksi
import com.kibo.skripsilagi.model.Produk
import com.kibo.skripsilagi.model.Transaksi
import com.kibo.skripsilagi.util.Config
import com.squareup.picasso.Picasso
import java.text.NumberFormat
import java.util.*
import javax.xml.transform.ErrorListener
import kotlin.collections.ArrayList

class AdapterProdukTransaksi (var data:ArrayList<DetailTransaksi>): RecyclerView.Adapter<AdapterProdukTransaksi.Holder>() {

    class Holder(view: View):RecyclerView.ViewHolder(view){
        val imgProduk = view.findViewById<ImageView>(R.id.img_produk)
        val tvNama = view.findViewById<TextView>(R.id.tv_nama)
        val tvHarga = view.findViewById<TextView>(R.id.tv_harga)
        val tvTotalHarga = view.findViewById<TextView>(R.id.tv_totalHarga)
        val tvJumlah = view.findViewById<TextView>(R.id.tv_jumlah)
        val layout = view.findViewById<CardView>(R.id.layout)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val View = LayoutInflater.from(parent.context).inflate(R.layout.item_produk_transaksi, parent, false)
        return Holder(View)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {

        val a = data[position]

        val name = a.produk.name
        val p = a.produk
        holder.tvNama.text = name
        holder.tvHarga.text = Helper().gantiRupiah(p.harga)
        holder.tvTotalHarga.text = Helper().gantiRupiah(a.total_harga)
        holder.tvJumlah.text = a.total_item.toString() + " Items"

        holder.layout.setOnClickListener {
//            listener.onClicked(a)
        }

        val image = Config.productUrl + p.image
        Picasso.get()
            .load(image)
            .placeholder(R.drawable.produk)
            .error(R.drawable.produk)
            .into(holder.imgProduk)
    }

    interface Listeners{
        fun onClicked(data: DetailTransaksi)
    }
}