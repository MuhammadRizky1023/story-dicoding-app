package com.example.storyapp.UI.ViewModel

import androidx.lifecycle.*
import com.example.storyapp.Data.ListStoryRepository
import com.example.storyapp.Model.LoginRequest
import com.example.storyapp.Model.LoginResponse

class LoginUserViewModel(private var storyRepository: ListStoryRepository) : ViewModel() {

    val message: LiveData<String> = storyRepository.messages

    var loadingIsActive: LiveData<Boolean> = storyRepository.loadingIsActive

    var accountUser: LiveData<LoginResponse> = storyRepository.accountUser

    fun getLoginAccountUser(loginRequest: LoginRequest) {
        storyRepository.getLoginAccountUser(loginRequest)
    }

}