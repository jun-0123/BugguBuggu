package com.example.buggubuggu.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buggubuggu.api.RetrofitClient
import com.example.buggubuggu.model.ApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    val items = MutableLiveData<ApiResponse>()


    fun getItems(num:Int) = viewModelScope.launch(Dispatchers.IO){
        RetrofitClient.itemsApi.getItemByFirstPage(num).enqueue(object: Callback<ApiResponse>{
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful && response.code() == 200) {
                    items.postValue(response.body())
                    Log.d("MainViewModel", "Items loaded successfully: ${response.body()}")
                }else {
                    Log.e("MainViewModel", "Failed to load items: ${response.code()} ${response.message()}")
                }
            }
            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
            }
        })
    }


}