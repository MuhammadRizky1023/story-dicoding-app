package com.example.storyapp.UI.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.Data.ListStoryRepository
import com.example.storyapp.Model.ListStoryUserPage

class MapUserViewModel (private var storyRepository: ListStoryRepository) : ViewModel() {

    var listStoryUser: LiveData<List<ListStoryUserPage>> = storyRepository.listStoryUser

    var message: LiveData<String> = storyRepository.messages

    var loadingIsActive: LiveData<Boolean> = storyRepository.loadingIsActive

    fun getMapUser(token: String) {
        storyRepository.getMapUser(token)
    }

}