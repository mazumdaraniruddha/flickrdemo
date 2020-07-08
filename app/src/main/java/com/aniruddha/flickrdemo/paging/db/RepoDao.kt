package com.aniruddha.flickrdemo.paging.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aniruddha.flickrdemo.paging.api.Photo
import com.aniruddha.flickrdemo.paging.model.Repo

@Dao
interface RepoDao {

    @Query("SELECT * FROM photos_table " +
            "WHERE query = :queryString " +
            "ORDER BY indexInResponse ASC")
    fun getReposByName(queryString: String): PagingSource<Int, Photo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(repsList: List<Photo>)

    @Query("DELETE FROM photos_table")
    suspend fun clearDB()
}