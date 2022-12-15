package com.kibo.skripsilagi.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.drjacky.imagepicker.ImagePicker
import com.google.gson.Gson
import com.inyongtisto.myhelper.base.BaseActivity
import com.inyongtisto.myhelper.extension.showErrorDialog
import com.inyongtisto.myhelper.extension.showSuccessDialog
import com.inyongtisto.myhelper.extension.toGone
import com.inyongtisto.myhelper.extension.toMultipartBody
import com.kibo.skripsilagi.MainActivity
import com.kibo.skripsilagi.R
import com.kibo.skripsilagi.adapter.AdapterProdukTransaksi
import com.kibo.skripsilagi.app.ApiConfig
import com.kibo.skripsilagi.helper.Helper
import com.kibo.skripsilagi.model.DetailTransaksi
import com.kibo.skripsilagi.model.ResponModel
import com.kibo.skripsilagi.model.Transaksi
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail_transaksi.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.toolbar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class DetailTransaksiActivity : BaseActivity() {

    var transaksi = Transaksi()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_transaksi)
        Helper().setToolbar(this, toolbar, "Detail Transaksi")

        val json = intent.getStringExtra("transaksi")
        transaksi = Gson().fromJson(json, Transaksi::class.java)

        setData(transaksi)
        displayProduk(transaksi.details)
        mainButton()
    }

    private fun mainButton(){
        btn_batal.setOnClickListener {

            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Apakakah anda yakin?")
                .setContentText("Transaksi dibatalkan dan tidak bisa dikembalikan")
                .setConfirmText("Yes, Batalkan")
                .setConfirmClickListener {
                        it.dismissWithAnimation()
                        batalTransaksi()
                }

                .setCancelText("Tutup")
                .setCancelClickListener {
                    it.dismissWithAnimation()
                }
                .show()
        }

        btn_bayar.setOnClickListener {
            imagePic()
        }
    }

    private fun imagePic(){
        ImagePicker.with(this)
            .crop()
            .maxResultSize(512 , 512)
            .createIntentFromDialog { launcher.launch(it) }
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val uri = it.data?.data!!
            // Use the uri to load the image
            Log.d("TAG", "URI IMAGE: "+uri)
            val fileUri: Uri = uri
            dialogUpload(File(fileUri.path!!))
        }
    }

    var alertDialog : AlertDialog? = null

    @SuppressLint("InflateParams")
    private fun dialogUpload(file:File){

        val view = layoutInflater
        val layout = view.inflate(R.layout.view_upload, null)

        val imageView : ImageView = layout.findViewById(R.id.image)
        val btnUpload : Button = layout.findViewById(R.id.btn_upload)
        val btnImage : Button = layout.findViewById(R.id.btn_image)

        Picasso.get()
            .load(file)
            .into(imageView)

        btnUpload.setOnClickListener {
            upload(file)
        }

        btnImage.setOnClickListener {
            imagePic()
        }
        alertDialog = AlertDialog.Builder(this).create()
        alertDialog!!.setView(layout)
        alertDialog!!.setCancelable(true)
        alertDialog!!.show()

    }

    private fun upload(file: File){

        progress.show()
        val fileImage = file.toMultipartBody()
        ApiConfig.instanceRetrofit.uploadBukti(transaksi.id, fileImage!!).enqueue(object : Callback<ResponModel> {
            override fun onFailure(call: Call<ResponModel>, t: Throwable) {
                progress.dismiss()
                showErrorDialog(t.message!!)
            }

            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                progress.dismiss()
                if (response.isSuccessful){
                    if (response.body()!!.success == 1){
                        showSuccessDialog("Upload bukti berhasil"){
                            alertDialog!!.dismiss()
                            btn_bayar.toGone()
                            tv_status.text = "DIBAYAR"
                            onBackPressed()
                        }
                    } else {
                        showErrorDialog(response.body()!!.message)
                    }

                } else{
                    showErrorDialog(response.message())
                }
            }
        })
    }

    fun batalTransaksi(){
        val loading = SweetAlertDialog(this@DetailTransaksiActivity, SweetAlertDialog.PROGRESS_TYPE)
            loading.setTitleText("Loading....").show()
        ApiConfig.instanceRetrofit.batalCheckout(transaksi.id).enqueue(object :
            Callback<ResponModel> {

            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                loading.dismiss()
                val res = response.body()!!
                if (res.success == 1) {

                    SweetAlertDialog(this@DetailTransaksiActivity, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Sukses...")
                        .setContentText("Teransaksi berhasil dibatalkan")
                        .setConfirmClickListener {
                            it.dismissWithAnimation()
                            onBackPressed()
                        }
                        .show()

//                    Toast.makeText(this@DetailTransaksiActivity, "Transaksi berhasil di batalkan", Toast.LENGTH_SHORT).show()
//                    onBackPressed()
//                    displayRiwayat(res.transaksis)
                } else {
                    loading.dismiss()
                    error(res.message)
                }
            }

            override fun onFailure(call: Call<ResponModel>, t: Throwable) {
                error(t.message.toString())
            }
        })
    }

    fun error(pesan:String){
        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
            .setTitleText("Oops...")
            .setContentText("pesan")
            .show()
    }

    fun setData(t: Transaksi) {
        tv_status.text = t.status


        val formatBaru = "dd MMMM yyy, kk:mm:ss"
        tv_tgl.text = Helper().convertTanggal(t.created_at, formatBaru)

        tv_penerima.text = t.name + " - " + t.phone
        tv_alamat.text = t.detail_lokasi
        tv_kodeUnik.text = Helper().gantiRupiah(t.kode_unik)
        tv_totalBelanja.text = Helper().gantiRupiah(t.total_harga)
        tv_ongkir.text = Helper().gantiRupiah(t.ongkir)
        tv_total.text = Helper().gantiRupiah(t.total_transfer)

        if (t.status != "MENUGGU") div_footer.visibility = View.GONE

        var color = getColor(R.color.menunggu)
        if (t.status == "SELESAI") color = getColor(R.color.selesai)
        else if (t.status == "BATAL") color = getColor(R.color.batal)

        tv_status.setTextColor(color)
    }

    fun displayProduk(transaksis: ArrayList<DetailTransaksi>) {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        rv_produk.adapter = AdapterProdukTransaksi(transaksis)
        rv_produk.layoutManager = layoutManager
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}