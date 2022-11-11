package com.example.storyapp.Adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.Helper.LocationConvertData
import com.example.storyapp.Helper.MapConvertData
import com.example.storyapp.Model.ListStoryUserPage
import com.example.storyapp.R
import com.example.storyapp.UI.DetailListStoryActivity
import com.example.storyapp.databinding.ItemListStoryBinding

class ListStoryUserAdapter(var adapterContext: Context) :
    PagingDataAdapter<ListStoryUserPage, ListStoryUserAdapter.ListStoryViewHold>(LIST_STORY_USER_CALLBACK) {
    private lateinit var listStoryUserClickCallback: ListStoryUserClickCallback
    fun listStoryItemClickCallback(listStoryClickCallback: ListStoryUserClickCallback) {
        this.listStoryUserClickCallback= listStoryClickCallback
    }

    override fun onCreateViewHolder(listStoryGroup: ViewGroup, listStoryType: Int): ListStoryViewHold {
        val itemListStory =
            ItemListStoryBinding.inflate(LayoutInflater.from(listStoryGroup.context), listStoryGroup, false)

        return ListStoryViewHold(itemListStory, adapterContext)
    }

    override fun onBindViewHolder(holdListStry: ListStoryViewHold, position: Int) {
        val listStory = getItem(position)
        if (listStory != null) {
            holdListStry.bind(listStory)

            holdListStry.itemView.setOnClickListener {
                listStoryUserClickCallback.onListStoryUserItemClicked(listStory)

                val adapter = Intent(it.context, DetailListStoryActivity::class.java)
                adapter.putExtra(DetailListStoryActivity.LIST_EXTRA_STORIES, listStory)
                it.context.startActivity(adapter,
                    ActivityOptionsCompat
                        .makeSceneTransitionAnimation(it.context as Activity)
                        .toBundle()
                )
            }

        } else {
            holdListStry.itemView.setOnClickListener {
                listStoryUserClickCallback
                val adapter = Intent(it.context, DetailListStoryActivity::class.java)
                it.context.startActivity(adapter,
                    ActivityOptionsCompat
                        .makeSceneTransitionAnimation(it.context as Activity)
                        .toBundle()
                )
            }
        }
    }

    class ListStoryViewHold(private var itemListStory: ItemListStoryBinding, val context: Context) :
        RecyclerView.ViewHolder(itemListStory.root) {
        fun bind(listStory: ListStoryUserPage) {
            itemListStory.tvItemName.text = listStory.name
            itemListStory.tvLocation.text = LocationConvertData.getLocationConvert(
                MapConvertData.mapConvertData(listStory.lat, listStory.lon),
                context
            )
            Glide.with(itemView.context)
                .load(listStory.photoUrl)
                .placeholder(R.drawable.story)
                .error(R.drawable.story)
                .fallback(R.drawable.story)
                .into(itemListStory.ivItemPhoto)
        }
    }

    interface ListStoryUserClickCallback {
        fun onListStoryUserItemClicked(listStory: ListStoryUserPage)
    }

    companion object {
        var LIST_STORY_USER_CALLBACK = object : DiffUtil.ItemCallback<ListStoryUserPage>() {
            override fun areItemsTheSame(
                listerStoryOlder: ListStoryUserPage,
                listStoryBegin: ListStoryUserPage
            ): Boolean {
                return listerStoryOlder == listStoryBegin
            }

            override fun areContentsTheSame(
                listerStoryOlder: ListStoryUserPage,
                listStoryBegin: ListStoryUserPage
            ): Boolean {
                return listerStoryOlder.id == listStoryBegin.id
            }
        }
    }


}