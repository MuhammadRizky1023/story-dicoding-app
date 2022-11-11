package com.example.storyapp.Preference

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyaipp.ui.ViewModel.ListStoryUserViewModel
import com.example.storyapp.Data.Inspection
import com.example.storyapp.UI.ViewModel.AddListStoryUserViewModel
import com.example.storyapp.UI.ViewModel.LoginUserViewModel
import com.example.storyapp.UI.ViewModel.MapUserViewModel
import com.example.storyapp.UI.ViewModel.RegisterUserViewModel


class ListStoryRepositoryPrefernces(private var provider: Context) : ViewModelProvider.Factory {
    override fun <V: ViewModel> create(  viewModelClasses: Class<V>): V {
        when {
            viewModelClasses.isAssignableFrom(ListStoryUserViewModel::class.java) -> {
                return ListStoryUserViewModel(Inspection.providerListStoryUserRepository(provider)) as V
            }
            viewModelClasses.isAssignableFrom(LoginUserViewModel::class.java) -> {
                return LoginUserViewModel(Inspection.providerListStoryUserRepository(provider)) as V
            }
            viewModelClasses.isAssignableFrom(AddListStoryUserViewModel::class.java) -> {
                return AddListStoryUserViewModel(Inspection.providerListStoryUserRepository(provider)) as V
            }
            viewModelClasses.isAssignableFrom(MapUserViewModel::class.java) -> {
                return MapUserViewModel(Inspection.providerListStoryUserRepository(provider)) as V
            }
            viewModelClasses.isAssignableFrom(RegisterUserViewModel::class.java) -> {
                return RegisterUserViewModel(Inspection.providerListStoryUserRepository(provider)) as V
            }
        }
        throw IllegalArgumentException("Failed known this viewModel class")
    }
}