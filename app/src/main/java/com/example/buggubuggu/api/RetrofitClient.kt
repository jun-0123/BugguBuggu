package com.example.buggubuggu.api

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {
    companion object{
        private val gson = GsonBuilder()
            .setLenient()
            .create()

        private val client = Retrofit.Builder()
            .baseUrl("https://apis.data.go.kr/3450000/safeRestDesigStatService_new/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val itemsApi:ItemApi = client.create(ItemApi::class.java)
    }
}