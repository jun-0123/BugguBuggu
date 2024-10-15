package com.example.buggubuggu.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buggubuggu.api.KakaoClient
import com.example.buggubuggu.api.RetrofitClient
import com.example.buggubuggu.model.ApiResponse
import com.example.buggubuggu.model.ResultSearchKeyword
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailViewModel : ViewModel() {
    companion object {
        const val API_KEY = "카카오 API 인증키"
    }

    val items = MutableLiveData<ApiResponse>()
    val results = MutableLiveData<ResultSearchKeyword>()

    fun getItem(num:Int) = viewModelScope.launch(Dispatchers.IO){
        RetrofitClient.itemsApi.getItemByFirstPage(num).enqueue(object: Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful && response.code() == 200) {
                    items.postValue(response.body())
                    Log.d("DetailViewModel", "Items loaded successfully: ${response.body()}")
                }else {
                    Log.e("DetailViewModel", "Failed to load items: ${response.code()} ${response.message()}")
                }
            }
            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
            }
        })
    }

    fun getSearch(keyword:String) {
        KakaoClient.kakaoapi.getSearchKeyword(API_KEY,keyword).enqueue(object: Callback<ResultSearchKeyword> {
            override fun onResponse(call: Call<ResultSearchKeyword>, response: Response<ResultSearchKeyword>
            ) {
                if (response.isSuccessful && response.code() == 200){
                    results.postValue(response.body())
                    Log.d("DetailViewModel", "ResultSearchKeword loaded successfully: ${response.body()}")
                }
                // 통신 성공 (검색 결과는 response.body()에 담겨있음)
                Log.d("Test", "Raw: ${response.raw()}")
                Log.d("Test", "Body: ${response.body()}")
            }

            override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
                // 통신 실패
                Log.w("MainActivity", "통신 실패: ${t.message}")
            }
        })

    }

}