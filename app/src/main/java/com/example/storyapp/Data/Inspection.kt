package com.example.storyapp.Data

import android.content.Context
import com.example.storyapp.API.ListStoryApiConfig
import com.example.storyapp.database.ListStoryDatabase

object Inspection {
    fun providerListStoryUserRepository(provider: Context, ): ListStoryRepository {
        val listStoryDatabase = ListStoryDatabase.getListStoryDatabase(provider)
        val listStoryApiService= ListStoryApiConfig.getListStoryApiConfig()
        return ListStoryRepository(listStoryDatabase, listStoryApiService)
    }
}
