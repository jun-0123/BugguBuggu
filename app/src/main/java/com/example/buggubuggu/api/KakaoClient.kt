package com.example.buggubuggu.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class KakaoClient {
    companion object{
        private val client = Retrofit.Builder()   // Retrofit 구성
            .baseUrl("https://dapi.kakao.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val kakaoapi = client.create(KakaoAPI::class.java)
    }
}