package com.example.storyapp.UI.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.Data.ListStoryRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddListStoryUserViewModel(private val  storyRepository: ListStoryRepository) : ViewModel() {

    var messages: LiveData<String> = storyRepository.messages

    var loadingIsActive: LiveData<Boolean> = storyRepository.loadingIsActive

    fun uploadStory(
        photo: MultipartBody.Part,
        description: RequestBody,
        token: String,
        latitude: Double?,
        longitude: Double?
    ) {
        storyRepository.uploadStory(photo, description, token, latitude, longitude)
    }

}