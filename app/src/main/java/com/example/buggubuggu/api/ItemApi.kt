package com.example.buggubuggu.api

import com.example.buggubuggu.model.ApiResponse
import com.example.buggubuggu.model.Item
import com.example.buggubuggu.model.Items
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ItemApi {
    companion object {
        const val SERVICE_KEY = "공공포탈인증키"
    }

    @GET("getSafeRestDesigStat_v2")
    fun getItemByFirstPage(@Query("currentPage") num: Int,
                           @Query("serviceKey") serviceKey: String = SERVICE_KEY): Call<ApiResponse>

    @GET("getSafeRestDesigStat_v2")
    fun getItem(@Query("NO") num:Int,
                @Query("serviceKey") serviceKey: String= SERVICE_KEY):Call<ApiResponse>
}