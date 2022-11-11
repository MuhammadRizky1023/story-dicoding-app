package com.example.storyapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.storyapp.Model.UserControllerKey

@Dao
interface UserControllerKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAllUserControllerKey(remoteKey: List<UserControllerKey>)

    @Query("SELECT * FROM user_controller_key WHERE id = :id")
    suspend fun getAllUserControllerById(id: String): UserControllerKey?

    @Query("DELETE FROM user_controller_key")
    suspend fun deleteAllUserControllerKey()
}