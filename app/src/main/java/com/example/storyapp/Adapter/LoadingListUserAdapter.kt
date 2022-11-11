package com.example.storyapp.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.storyapp.databinding.ItemLoadingBinding

class LoadingListUserAdapter(private val reload: () -> Unit) : LoadStateAdapter<LoadingListUserAdapter.LoadingStateViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadingStateViewHolder {
        val binding = ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadingStateViewHolder(binding, reload)
    }
    override fun onBindViewHolder(loadingUserHold: LoadingStateViewHolder, loadingUser: LoadState) {
        loadingUserHold.loading(loadingUser)
    }
    class LoadingStateViewHolder(private val itemUserLoadingUser: ItemLoadingBinding, reload: () -> Unit) :
        RecyclerView.ViewHolder(itemUserLoadingUser.root) {
        init {
            itemUserLoadingUser.btnReload.setOnClickListener { reload.invoke() }
        }
        fun loading(loadingUser: LoadState) {
            if (loadingUser is LoadState.Error) {
                itemUserLoadingUser.errorMessage.text = loadingUser.error.localizedMessage
            } else {
                itemUserLoadingUser.loading.isVisible = loadingUser is LoadState.Loading
                itemUserLoadingUser.btnReload.isVisible = loadingUser is LoadState.Error
                itemUserLoadingUser.errorMessage.isVisible = loadingUser is LoadState.Error
                itemUserLoadingUser.ivError.isVisible = loadingUser is LoadState.Error
            }
        }
    }
}