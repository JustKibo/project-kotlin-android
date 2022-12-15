package com.kibo.skripsilagi.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kibo.skripsilagi.R
import com.kibo.skripsilagi.activity.MasukActivity
import com.kibo.skripsilagi.activity.PengirimanActivity
import com.kibo.skripsilagi.adapter.AdapterKeranjang
import com.kibo.skripsilagi.helper.Helper
import com.kibo.skripsilagi.helper.SharedPref
import com.kibo.skripsilagi.model.Produk
import com.kibo.skripsilagi.room.MyDatabase
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class KeranjangFragment : Fragment() {

    lateinit var myDb : MyDatabase
    lateinit var s : SharedPref

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_keranjang, container, false)
        init(view)
        myDb = MyDatabase.getInstance(requireActivity())!!
        s = SharedPref(requireActivity())

        mainButton()

        return view
    }

    lateinit var adapter : AdapterKeranjang
    var listProduk = ArrayList<Produk>()
    private fun displayProduk(){
        listProduk = myDb.daoKeranjang().getAll() as ArrayList
        val layoutManager = LinearLayoutManager (activity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL

        adapter = AdapterKeranjang(requireActivity(), listProduk, object : AdapterKeranjang.Listeners{
            override fun onUpdate() {
                hitungTotal()
            }

            override fun onDelete(position: Int) {
                listProduk.removeAt(position)
                adapter.notifyDataSetChanged()
                hitungTotal()
            }

        })
        rvProduk.adapter = adapter
        rvProduk.layoutManager = layoutManager
    }

    var totalHarga = 0
    fun hitungTotal(){
        val listProduk = myDb.daoKeranjang().getAll() as ArrayList
        totalHarga = 0
        var isSelectedAll = true
        for (produk in listProduk){
            if (produk.selected){
                val harga = Integer.valueOf(produk.harga)
                totalHarga += (harga * produk.jumlah)
            } else {
                isSelectedAll = false
            }
        }

        cbAll.isChecked = isSelectedAll
        tvTotal.text = Helper().gantiRupiah(totalHarga)
    }

    private fun mainButton(){
        btnDelete.setOnClickListener {
            val listDelete = ArrayList<Produk>()
            for (p in listProduk){
                if (p.selected) listDelete.add(p)
            }

            delete(listDelete)
        }

        btnBayar.setOnClickListener {

            if (s.getStatusLogin()){
                var isThereProduk = false
                for (p in listProduk){
                    if (p.selected) isThereProduk = true
                }

                if (isThereProduk) {
                    val intent = Intent(requireActivity(), PengirimanActivity::class.java)
                    intent.putExtra("extra", ""+ totalHarga)
                    startActivity(intent)
                } else{
                    Toast.makeText(requireContext(), "Tidak ada produk terpilih", Toast.LENGTH_SHORT).show()
                }
            } else {
                requireActivity().startActivity(Intent(requireActivity(), MasukActivity::class.java))
            }
        }

        cbAll.setOnClickListener {
            for (i in listProduk.indices){
                val produk = listProduk[i]
                produk.selected = cbAll.isChecked
                listProduk[i] = produk
            }
            adapter.notifyDataSetChanged()
        }
    }

    private fun delete(data: ArrayList<Produk>){
        CompositeDisposable().add(Observable.fromCallable { myDb.daoKeranjang().delete(data) }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                listProduk.clear()
                listProduk.addAll(myDb.daoKeranjang().getAll() as ArrayList)
                adapter.notifyDataSetChanged()
            })
    }

    lateinit var btnDelete : ImageView
    lateinit var rvProduk : RecyclerView
    lateinit var tvTotal : TextView
    lateinit var btnBayar : TextView
    lateinit var cbAll : CheckBox
    private fun init (view: View) {
            btnDelete = view.findViewById(R.id.btn_delete)
            rvProduk = view.findViewById(R.id.rv_produk)
            tvTotal = view.findViewById(R.id.tv_total)
            btnBayar = view.findViewById(R.id.btn_bayar)
        cbAll = view.findViewById(R.id.cb_all)
    }

    override fun onResume() {
        displayProduk()
        hitungTotal()
        super.onResume()
    }
}