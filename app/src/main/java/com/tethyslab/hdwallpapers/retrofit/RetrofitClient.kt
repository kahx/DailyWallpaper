package com.tethyslab.hdwallpapers.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {
    companion object{
        fun getClient():Retrofit{
            return Retrofit.Builder()
                .baseUrl("https://www.json-generator.com/api/json/get/cfByXLchmG/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        }
    }
}