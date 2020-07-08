package com.aniruddha.flickrdemo.paging.api

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "photos_table")
data class Photo(
        @PrimaryKey @SerializedName("id") val id: String,
        @SerializedName("farm") val farm: Int,
        @SerializedName("isfamily") val isFamily: Int,
        @SerializedName("isfriend") val isFriend: Int,
        @SerializedName("ispublic") val isPublic: Int,
        @SerializedName("owner") val owner: String,
        @SerializedName("secret") val secret: String,
        @SerializedName("server") val server: String,
        @SerializedName("title") val title: String
)

fun Photo.getImageUrl() = "https://farm$farm.staticflickr.com/$server/${id}_${secret}_m.jpg"