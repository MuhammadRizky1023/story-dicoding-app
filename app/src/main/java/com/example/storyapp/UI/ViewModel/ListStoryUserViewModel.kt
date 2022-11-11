package com.example.storyaipp.ui.ViewModel
import androidx.lifecycle.*
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapp.Data.ListStoryRepository
import com.example.storyapp.Model.ListStoryUserPage


class ListStoryUserViewModel(private var storyRepository: ListStoryRepository) : ViewModel() {

    @ExperimentalPagingApi
    fun getListStoryUserPage(token: String): LiveData<PagingData<ListStoryUserPage>> {
        return storyRepository.getListStoryUserPage(token).cachedIn(viewModelScope)
    }

}