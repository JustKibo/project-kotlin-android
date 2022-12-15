package com.kibo.skripsilagi.model.rajaongkir

import com.kibo.skripsilagi.model.ModelAlamat

class ResponOngkir {
    val rajaongkir = Rajaongkir()

    class Rajaongkir{
        val results = ArrayList<Result>()
    }

}