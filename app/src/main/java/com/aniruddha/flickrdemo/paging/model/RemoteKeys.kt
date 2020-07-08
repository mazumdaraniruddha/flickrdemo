package com.aniruddha.flickrdemo.paging.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
/**
 * An entity to keep track of the previous/next page indices w.r.t a photo entity
 * */
data class RemoteKeys(
        @PrimaryKey
        val photoId: String,
        val prevIndex: Int?,
        val nextIndex: Int?
)