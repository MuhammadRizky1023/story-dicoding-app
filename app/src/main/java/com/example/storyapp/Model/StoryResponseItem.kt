package com.example.storyapp.Model

import com.google.gson.annotations.SerializedName


data class StoryResponseItem(

    @field:SerializedName("error")
    var error: Boolean,

    @field:SerializedName("message")
    var message: String? = null,

    @field:SerializedName("listStory")
    var listStory: List<ListStoryUserPage>
)