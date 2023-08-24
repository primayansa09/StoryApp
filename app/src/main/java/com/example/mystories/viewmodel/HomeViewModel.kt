package com.example.mystories.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.mystories.api.ApiConfig
import com.example.mystories.api.ListStoryItem
import com.example.mystories.api.ListStoryResponse
import com.example.mystories.di.Injection
import com.example.mystories.repository.StoryRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel(storyRepository: StoryRepository): ViewModel() {

    val storyPaging: LiveData<PagingData<ListStoryItem>> by lazy {
        storyRepository.getStory().cachedIn(viewModelScope)}

    private val _story = MutableLiveData<List<ListStoryItem>>()
    val story: LiveData<List<ListStoryItem>> = _story

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isShowData = MutableLiveData<Boolean>()
    val isShowData: LiveData<Boolean> = _isShowData

    companion object{
        private const val TAG = "HomeActivity"
    }

    fun getListStory(token: String) {
        _isLoading.value = true
        _isShowData.value = true
        val client = ApiConfig.getApiService().getStories("$token")
        client.enqueue(object : Callback<ListStoryResponse> {
            override fun onResponse(call: Call<ListStoryResponse>, response: Response<ListStoryResponse>) {
                _isLoading.value = false
                _isShowData.value = false
                if (response.isSuccessful){
                    _story.value = response.body()?.listStory
                }else{
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ListStoryResponse>, t: Throwable) {
                _isLoading.value = false
                _isShowData.value = true
                Log.e(TAG, "onFailure: ${t.message}")
            }

        })
    }
}

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(Injection.provideRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}