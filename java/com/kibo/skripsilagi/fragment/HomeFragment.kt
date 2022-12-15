package com.kibo.skripsilagi.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.kibo.skripsilagi.MainActivity
import com.kibo.skripsilagi.R
import com.kibo.skripsilagi.adapter.AdapterProduk
import com.kibo.skripsilagi.adapter.AdapterSlider
import com.kibo.skripsilagi.app.ApiConfig
import com.kibo.skripsilagi.model.Produk
import com.kibo.skripsilagi.model.ResponModel
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    lateinit var vpSlider: ViewPager
    lateinit var rvProduk: RecyclerView
    lateinit var rvTerlaris: RecyclerView
    lateinit var rvBaju: RecyclerView



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        val View = inflater.inflate(R.layout.fragment_home, container, false)
        init(View)
        getproduk()

        return View
    }

    fun displayProduk(){
        val arrSlider = ArrayList<Int>()
        arrSlider.add(R.drawable.slider1)
//        arrSlider.add(R.drawable.slider2)
//        arrSlider.add(R.drawable.slider3)

        val adapterSlider = AdapterSlider(arrSlider, activity)
        vpSlider.adapter = adapterSlider

        val layoutManager = LinearLayoutManager (activity)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL

        val layoutManager2 = LinearLayoutManager (activity)
        layoutManager2.orientation = LinearLayoutManager.HORIZONTAL

        val layoutManager3 = LinearLayoutManager (activity)
        layoutManager3.orientation = LinearLayoutManager.HORIZONTAL

        rvProduk.adapter = AdapterProduk (requireActivity(),listProduk)
        rvProduk.layoutManager = layoutManager

        rvTerlaris.adapter = AdapterProduk (requireActivity(),listProduk)
        rvTerlaris.layoutManager = layoutManager2

        rvBaju.adapter = AdapterProduk (requireActivity(),listProduk)
        rvBaju.layoutManager = layoutManager3
    }


    private var listProduk:ArrayList<Produk> = ArrayList()
    fun getproduk (){
        ApiConfig.instanceRetrofit.getProduk().enqueue(object :
            Callback<ResponModel> {

            override fun onResponse(call: Call<ResponModel>, response: Response<ResponModel>) {
                val res = response.body()!!
                if (res.success == 1){

                    val arrayProduk = ArrayList<Produk>()
                    for (p in res.produks) {
                        arrayProduk.add(p)
                    }
                    listProduk = arrayProduk
                    displayProduk()
                }
            }
            override fun onFailure(call: Call<ResponModel>, t: Throwable) {
            }

        })
    }

    fun init(view: View){
        vpSlider = view.findViewById(R.id.vp_slider)
        rvProduk = view.findViewById(R.id.rv_produk)
        rvTerlaris = view.findViewById(R.id.rv_terlaris)
        rvBaju = view.findViewById(R.id.rv_baju)
    }

//    val arrProduk : ArrayList<Produk>get() {
//        val arr = ArrayList<Produk>()
//        val p1 = Produk()
//        p1.nama = "Baju Wanita"
//        p1.harga = "200.000"
//        p1.gambar = R.drawable.baju1

//        val p2 = Produk()
//        p2.nama = "Baju Wanita Biasa"
//        p2.harga = "100.000"
//        p2.gambar = R.drawable.baju2

//        val p3 = Produk()
//        p3.nama = "Baju Wanita Aja"
//        p3.harga = "300.000"
//        p3.gambar = R.drawable.baju3

//        arr.add(p1)
//        arr.add(p2)
//        arr.add(p3)

//        return arr
    }

//    val arrTerlaris : ArrayList<Produk>get() {
//        val arr = ArrayList<Produk>()
//        val p1 = Produk()
//        p1.nama = "Baju Wanita"
//        p1.harga = "200.000"
//        p1.gambar = R.drawable.baju1

//        val p2 = Produk()
//        p2.nama = "Baju Wanita Biasa"
//        p2.harga = "100.000"
//        p2.gambar = R.drawable.baju2

//        val p3 = Produk()
 //       p3.nama = "Baju Wanita Aja"
 //       p3.harga = "300.000"
//        p3.gambar = R.drawable.baju3

//        arr.add(p1)
//        arr.add(p2)
//        arr.add(p3)

 //       return arr
//}

//    val arrBaju : ArrayList<Produk>get() {
//        val arr = ArrayList<Produk>()
//        val p1 = Produk()
//        p1.nama = "Baju Wanita"
 //       p1.harga = "200.000"
//        p1.gambar = R.drawable.baju1

//        val p2 = Produk()
//        p2.nama = "Baju Wanita Biasa"
//        p2.harga = "100.000"
//        p2.gambar = R.drawable.baju2

//        val p3 = Produk()
//        p3.nama = "Baju Wanita Aja"
 //       p3.harga = "300.000"
//        p3.gambar = R.drawable.baju3

 //       arr.add(p1)
//        arr.add(p2)
//        arr.add(p3)

//        return arr
    //}
//}