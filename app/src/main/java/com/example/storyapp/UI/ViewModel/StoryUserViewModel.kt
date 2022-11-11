package com.example.storyapp.UI.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.storyapp.Preference.StoryUserPreference
import kotlinx.coroutines.launch

class StoryUserViewModel(private  var preferences:  StoryUserPreference): ViewModel() {
    fun getUserLoginAuth(): LiveData<Boolean> {
        return preferences.getLoginAuth().asLiveData()
    }

    fun saveUserLoginAuth(isLogin: Boolean) {
        viewModelScope.launch {
            preferences.savingLoginAuth(isLogin)
        }
    }


    fun getTokenAuth(): LiveData<String> {
        return preferences.getTokenAuth().asLiveData()
    }

    fun saveTokenAuth(token: String) {
        viewModelScope.launch {
            preferences.savingTokenAuth(token)
        }
    }

    fun getUserName(): LiveData<String> {
        return preferences.getUserName().asLiveData()
    }

    fun saveUserName(name: String) {
        viewModelScope.launch {
            preferences.saveUser(name)
        }
    }
}