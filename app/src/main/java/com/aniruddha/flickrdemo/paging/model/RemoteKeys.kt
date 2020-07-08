package com.aniruddha.flickrdemo.paging.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeys(
        @PrimaryKey
        val photoId: String,
        val prevIndex: Int?,
        val nextIndex: Int?
)