package com.aniruddha.flickrdemo.main.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.aniruddha.flickrdemo.main.model.Photo
import com.aniruddha.flickrdemo.main.model.RemoteKeys

@Database(entities = [Photo::class, RemoteKeys::class], version = 1, exportSchema = false)
abstract class FlickrDataBase : RoomDatabase() {

    abstract fun photosDao(): PhotosDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {

        @Volatile
        private var INSTANCE: FlickrDataBase? = null

        fun getInstance(context: Context): FlickrDataBase =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: buildDatabase(context)
                }

        private fun buildDatabase(context: Context) =
                Room.databaseBuilder(context.applicationContext,
                        FlickrDataBase::class.java,
                        "aniruddha.flickr.db")
                        .build()
    }
}