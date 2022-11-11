package com.example.storyapp.UI.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.Data.ListStoryRepository
import com.example.storyapp.Model.LoginResponse
import com.example.storyapp.Model.RequestRegister

class RegisterUserViewModel(private val storyRepository: ListStoryRepository) : ViewModel() {

    var messages: LiveData<String> = storyRepository.messages

    var loadingIsActive: LiveData<Boolean> =storyRepository.loadingIsActive

    var accountUser: LiveData<LoginResponse> = storyRepository.accountUser

    fun createAccountUser(requestRegister: RequestRegister) {
        storyRepository.createAccountUser(requestRegister)
    }
}