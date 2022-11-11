package com.example.storyapp.Preference

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.UI.ViewModel.StoryUserViewModel


class ViewModelAccountPreferences(private val preferences:  StoryUserPreference) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("CHECK_THIS_USER")
    override fun <V : ViewModel> create(modelClassesUser: Class<V>): V {
        if (modelClassesUser.isAssignableFrom(StoryUserViewModel::class.java)) {
            return StoryUserViewModel(preferences) as V
        }
        throw IllegalArgumentException("failed to know viewModel class: " + modelClassesUser.name)
    }
}