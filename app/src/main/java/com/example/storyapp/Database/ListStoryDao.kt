package com.example.storyapp.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.storyapp.Model.ListStoryUserPage


@Dao
interface ListStoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addListStory(stories: List<ListStoryUserPage>)

    @Query("SELECT * FROM story")
    fun getAllListStoryUserPage(): PagingSource<Int, ListStoryUserPage>

    @Query("SELECT * FROM story")
    fun getAllListStoryUser(): List<ListStoryUserPage>

    @Query("DELETE FROM story")
    suspend fun deleteAllStoryUser()
}