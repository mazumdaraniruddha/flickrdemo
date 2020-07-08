package com.aniruddha.flickrdemo.paging.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.aniruddha.flickrdemo.paging.api.Photo
import com.aniruddha.flickrdemo.paging.model.RemoteKeys
import com.aniruddha.flickrdemo.paging.model.Repo

@Database(entities = [Photo::class, RemoteKeys::class], version = 1, exportSchema = false)
abstract class RepoDataBase : RoomDatabase() {

    abstract fun photosDao(): RepoDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {

        @Volatile
        private var INSTANCE: RepoDataBase? = null

        fun getInstance(context: Context): RepoDataBase =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: buildDatabase(context)
                }

        private fun buildDatabase(context: Context) =
                Room.databaseBuilder(context.applicationContext,
                        RepoDataBase::class.java,
                        "aniruddha.flickr.db")
                        .build()
    }
}