package com.kibo.skripsilagi.app

import com.kibo.skripsilagi.model.Checkout
import com.kibo.skripsilagi.model.ResponModel
import com.kibo.skripsilagi.model.rajaongkir.ResponOngkir
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name :String,
        @Field("email") email :String,
        @Field("phone") noTLP :String,
        @Field("password") password :String,
        @Field("fcm") fcm: String
    ) :Call<ResponModel>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("fcm") fcm: String
    ) :Call<ResponModel>

    @POST("checkout")
    fun checkout(
        @Body data :Checkout
    ) :Call<ResponModel>

    @GET("produk")
    fun getProduk() :Call<ResponModel>

    @GET("province")
    fun getProvinsi(
        @Header("key") key :String
    ) :Call<ResponModel>

    @GET("city")
    fun getKota(
        @Header("key") key :String,
        @Query("province") id: String
    ) :Call<ResponModel>

    @GET("kecamatan")
    fun getKecamatan(
        @Query("id_kota") id: Int
    ) :Call<ResponModel>

    @FormUrlEncoded
    @POST("cost")
    fun ongkir(
        @Header("key") key :String,
        @Field("origin") origin :String,
        @Field("destination") destination :String,
        @Field("weight") weight :Int,
        @Field("courier") courier :String,
    ) :Call<ResponOngkir>

    @GET("checkout/user/{id}")
    fun getRiwayat(
        @Path("id") id: Int
    ) :Call<ResponModel>

    @POST("checkout/batal/{id}")
    fun batalCheckout(
        @Path("id") id: Int
    ) :Call<ResponModel>

    @Multipart
    @POST("checkout/upload/{id}")
    fun uploadBukti(
        @Path("id") id: Int,
        @Part image: MultipartBody.Part
    ) :Call<ResponModel>
}