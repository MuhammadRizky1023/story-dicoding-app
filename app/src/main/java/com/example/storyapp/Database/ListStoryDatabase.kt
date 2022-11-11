package com.example.storyapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.storyapp.Model.ListStoryUserPage
import com.example.storyapp.Model.UserControllerKey



@Database(
    entities = [ListStoryUserPage::class, UserControllerKey::class],
    version = 2,
    exportSchema = false
)
abstract class ListStoryDatabase : RoomDatabase() {

    abstract fun listStoryDao(): ListStoryDao
    abstract fun userControllerKeysDao(): UserControllerKeyDao

    companion object {
        @Volatile
        private var DATABASE: ListStoryDatabase? = null

        @JvmStatic
        fun getListStoryDatabase(database: Context): ListStoryDatabase {
            return DATABASE ?: synchronized(this) {
                DATABASE?: Room.databaseBuilder(
                    database.applicationContext,
                    ListStoryDatabase::class.java, "story_list_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
            }
        }
    }
}