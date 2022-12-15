package com.kibo.skripsilagi.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.kibo.skripsilagi.R
import com.kibo.skripsilagi.adapter.AdapterKurir
import com.kibo.skripsilagi.app.ApiConfigAlamat
import com.kibo.skripsilagi.helper.Helper
import com.kibo.skripsilagi.helper.SharedPref
import com.kibo.skripsilagi.model.Checkout
import com.kibo.skripsilagi.model.rajaongkir.Costs
import com.kibo.skripsilagi.model.rajaongkir.ResponOngkir
import com.kibo.skripsilagi.room.MyDatabase
import com.kibo.skripsilagi.util.ApiKey
import kotlinx.android.synthetic.main.activity_pengiriman.*
import kotlinx.android.synthetic.main.toolbar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

 class PengirimanActivity : AppCompatActivity() {

    lateinit var myDb : MyDatabase
    var totalHarga = 0
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pengiriman)
        Helper().setToolbar(this,  toolbar, "Pengiriman")
        myDb = MyDatabase.getInstance(this)!!

        totalHarga = Integer.valueOf(intent.getStringExtra("extra")!!)
        tv_totalBelanja.text = Helper().gantiRupiah(totalHarga)
        mainButton()
        setSpiner()
    }

    fun setSpiner(){
        val arrayString = ArrayList<String>()
        arrayString.add("JNE")
        arrayString.add("POS")
        arrayString.add("TIKI")

        val adapter = ArrayAdapter<Any>(this, R.layout.item_spinner, arrayString.toTypedArray())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spn_kurir.adapter = adapter
        spn_kurir.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position != 0){
                    getOngkir(spn_kurir.selectedItem.toString())
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

    }

    @SuppressLint("SetTextI18n")
    fun checkAlamat(){

        if (myDb.daoAlamat().getByStatus(true) != null){
            div_alamat.visibility = View.VISIBLE
            div_kosong.visibility = View.GONE
            div_metodePengiriman.visibility = View.VISIBLE

            val a = myDb.daoAlamat().getByStatus(true)!!
            tv_nama.text = a.name
            tv_phone.text = a.phone
            tv_alamat.text = a.alamat +", " + a.kota + ", " + a.kodepos + ", (" + a.type +")"
            btn_tambahAlamat.text = "Ubah Alamat"

            getOngkir("JNE")
        } else {
            div_alamat.visibility = View.GONE
            div_kosong.visibility = View.VISIBLE
            btn_tambahAlamat.text = "Tambah Alamat"
        }
    }

    private fun mainButton(){
        btn_tambahAlamat.setOnClickListener {
            startActivity(Intent(this, ListAlamatActivity::class.java))
        }

        btn_bayar.setOnClickListener {
            bayar()
        }
    }

     private fun bayar(){
         val user = SharedPref(this).getUser()!!
         val a = myDb.daoAlamat().getByStatus(true)!!

         val listProduk = myDb.daoKeranjang().getAll() as ArrayList
         var totalItem = 0
         var totalHarga = 0
         val produks = ArrayList<Checkout.Item>()
         for (p in listProduk){
             if (p.selected){
                 totalItem += p.jumlah
                 totalHarga += (p.jumlah * Integer.valueOf(p.harga))

                 val produk = Checkout.Item()
                 produk.id = "" + p.id
                 produk.total_item = "" + p.jumlah
                 produk.total_harga = "" + (p.jumlah * Integer.valueOf(p.harga))
                 produk.catatan = "Catatan baru"
                 produks.add(produk)
             }
         }

         val checkout = Checkout()
         checkout.user_id = "" + user.id
         checkout.total_item = "" + totalItem
         checkout.total_harga = "" + totalHarga
         checkout.name = a.name
         checkout.phone = a.phone
         checkout.jasa_pengiriman = jasaKirim
         checkout.ongkir = ongkir
         checkout.kurir = kurir
         checkout.detail_lokasi = tv_alamat.text.toString()
         checkout.total_transfer = "" + (totalHarga + Integer.valueOf(ongkir))
         checkout.produks = produks

         val json = Gson().toJson(checkout, Checkout::class.java)
         Log.d("Respon:", "json:" + json)
         val intent = Intent(this, PembayaranActivity::class.java)
         intent.putExtra("extra", json)
         startActivity(intent)
     }

    private fun getOngkir(kurir:String) {

        val alamat = myDb.daoAlamat().getByStatus(true)

        val origin = "501"
        val destination = "" + alamat!!.id_kota.toString()
        val berat = 1000

        ApiConfigAlamat.instanceRetrofit.ongkir(ApiKey.key, origin, destination, berat, kurir.toLowerCase()).enqueue(object :
            Callback<ResponOngkir> {
            override fun onResponse(call: Call<ResponOngkir>, response: Response<ResponOngkir>) {
                if (response.isSuccessful){
                    Log.d("Success", "Berhasil memuat data:")
                    val result = response.body()!!.rajaongkir.results
                    if (result.isNotEmpty()){
                        displayOngkir(result[0].code.toUpperCase(), result[0].costs)
                    }
                }else {
                    Log.d("Error", "Gagal memuat data:" + response.message())
                }
            }

            override fun onFailure(call: Call<ResponOngkir>, t: Throwable) {
                Log.d("Error", "Gagal memuat data:" + t.message)
            }

        })
    }

     var ongkir = ""
     var kurir = ""
     var jasaKirim = ""
    private fun displayOngkir(_kurir:String, arrayList : ArrayList<Costs>){

        var arrayOngkir = ArrayList<Costs>()
        for (i in arrayList.indices){
            val ongkir = arrayList[i]
            if (i == 0){
                ongkir.isActive = true
            }
                arrayOngkir.add(ongkir)
        }
        setTotal(arrayOngkir[0].cost[0].value)
        ongkir = arrayOngkir[0].cost[0].value
        kurir = _kurir
        jasaKirim = arrayOngkir[0].service

        val layoutManager = LinearLayoutManager (this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        var adapter:AdapterKurir? = null
        adapter = AdapterKurir (arrayOngkir, _kurir, object : AdapterKurir.Listeners{
            override fun onClicked(data: Costs, index: Int) {
                val newArrayOngkir = ArrayList<Costs>()
                for (ongkir in arrayOngkir){
                    ongkir.isActive = data.description == ongkir.description
                    newArrayOngkir.add(ongkir)
                }
                arrayOngkir = newArrayOngkir
                adapter!!.notifyDataSetChanged()
                setTotal(data.cost[0].value)

                ongkir = data.cost[0].value
                kurir = _kurir
                jasaKirim = data.service
            }
        })
        rv_metode.adapter = adapter
        rv_metode.layoutManager = layoutManager
    }

     fun setTotal(ongkir : String){
         tv_ongkir.text = Helper().gantiRupiah(ongkir)
         tv_total.text = Helper().gantiRupiah(Integer.valueOf(ongkir) + totalHarga)

     }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onResume() {
        checkAlamat()
        super.onResume()
    }
}