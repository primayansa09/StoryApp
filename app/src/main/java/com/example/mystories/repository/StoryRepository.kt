package com.example.mystories.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.mystories.api.ApiService
import com.example.mystories.api.ListStoryItem
import com.example.mystories.data.StoryPagingSource

class StoryRepository(private val apiService: ApiService) {
    fun getStory(): LiveData<PagingData<ListStoryItem>>{
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService)
            }
        ).liveData
    }
}