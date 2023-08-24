package com.example.mystories.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mystories.api.ApiConfig
import com.example.mystories.api.ListStoryItem
import com.example.mystories.api.ListStoryResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModel: ViewModel() {
    private val _maps = MutableLiveData<List<ListStoryItem>>()
    val maps: LiveData<List<ListStoryItem>> = _maps

    companion object{
        private const val TAG = "HomeActivity"
    }

    fun getMaps(token: String){
        val client = ApiConfig.getApiService().getStories("$token")
        client.enqueue(object : Callback<ListStoryResponse> {
            override fun onResponse(call: Call<ListStoryResponse>,response: Response<ListStoryResponse>
            ) {
                if (response.isSuccessful){
                    _maps.value = response.body()?.listStory
                    Log.e(TAG, "Success: ${response.body()}")

                }else{
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<ListStoryResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }
}