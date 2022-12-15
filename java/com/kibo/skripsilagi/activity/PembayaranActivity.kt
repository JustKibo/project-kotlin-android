package com.kibo.skripsilagi.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.kibo.skripsilagi.R
import com.kibo.skripsilagi.adapter.AdapterBank
import com.kibo.skripsilagi.app.ApiConfig
import com.kibo.skripsilagi.helper.Helper
import com.kibo.skripsilagi.model.Bank
import com.kibo.skripsilagi.model.Checkout
import com.kibo.skripsilagi.model.ResponModel
import com.kibo.skripsilagi.model.Transaksi
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog
import kotlinx.android.synthetic.main.activity_pembayaran.*
import kotlinx.android.synthetic.main.toolbar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PembayaranActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pembayaran)
        Helper().setToolbar(this,  toolbar, "Pembayaran")

        displayBank()
    }

    fun displayBank(){
        val arrBank = ArrayList<Bank>()
        arrBank.add(Bank("BCA", "0938377872", "kibo", R.drawable.logo_bca))
        arrBank.add(Bank("BRI", "0832084203", "kibo", R.drawable.logo_bri))
        arrBank.add(Bank("MANDIRI", "187316391", "kibo", R.drawable.logo_madiri))

        val layoutManager = LinearLayoutManager (this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        rv_data.layoutManager = layoutManager
        rv_data.adapter = AdapterBank(arrBank, object : AdapterBank.Listeners{
            override fun onClicked(data: Bank, index: Int) {
                bayar(data)
            }

        })
    }

    fun bayar(bank: Bank){

        val json = intent.getStringExtra("extra")!!.toString()
        val checkout = Gson().fromJson(json, Checkout::class.java)
        checkout.bank = bank.nama

        val loading = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        loading.setTitleText("Loading....").show()

        ApiConfig.instanceRetrofit.checkout(checkout).enqueue(object : Callback<ResponModel> {

            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                loading.dismiss()
                if (!response.isSuccessful){
                    error(response.message())
                    return
                }

                val respon = response.body()!!
                if (respon.success == 1){

                    val jsBank = Gson().toJson(bank, Bank::class.java)
                    val jsTransaksi = Gson().toJson(respon.transaksi, Transaksi::class.java)
                    val jsCheckout = Gson().toJson(checkout, Checkout::class.java)

                    val intent = Intent(this@PembayaranActivity, SuccessActivity::class.java)
                    intent.putExtra("bank", jsBank)
                    intent.putExtra("transaksi", jsTransaksi)
                    intent.putExtra("checkout", jsCheckout)
                    startActivity(intent)

                } else{
                    error(respon.message)
                    Toast.makeText(this@PembayaranActivity, "Error:"+respon.message, Toast.LENGTH_SHORT).show()

                }
            }

            override fun onFailure(call: Call<ResponModel>, t: Throwable) {
                loading.dismiss()
                error(t.message.toString())
//                Toast.makeText(this@PengirimanActivity, "Error:"+t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    fun error(pesan:String){
        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
            .setTitleText("Oops...")
            .setContentText("pesan")
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}