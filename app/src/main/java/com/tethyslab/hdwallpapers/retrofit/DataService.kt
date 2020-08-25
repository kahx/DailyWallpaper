package com.tethyslab.hdwallpapers.retrofit

import retrofit2.Call
import retrofit2.http.GET

interface DataService {
    @GET(" ")
    fun getData(): Call<DataClass>
}