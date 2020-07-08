package com.aniruddha.flickrdemo.main.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aniruddha.flickrdemo.main.model.Photo

@Dao
interface PhotosDao {

    @Query("SELECT * FROM photos_table " +
            "WHERE title LIKE :queryString " +
            "ORDER BY indexInResponse ASC")
    fun getReposByName(queryString: String): PagingSource<Int, Photo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(repsList: List<Photo>)

    @Query("DELETE FROM photos_table")
    suspend fun clearDB()
}