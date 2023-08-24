package com.example.mystories.di

import android.content.Context
import com.example.mystories.api.ApiConfig
import com.example.mystories.repository.StoryRepository

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val apiService = ApiConfig.getApiService()
        return StoryRepository(apiService)
    }
}