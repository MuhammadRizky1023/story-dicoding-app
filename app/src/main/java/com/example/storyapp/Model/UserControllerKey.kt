package com.example.storyapp.Model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


@Entity(tableName = "user_controller_key")
class UserControllerKey(
    @PrimaryKey
    @field:SerializedName("id")
    val id: String,
    @field:SerializedName("prevKe")
    val prevKey: Int?,
    @field:SerializedName("nextKey")
    val nextKey: Int?
)
