package com.aniruddha.flickrdemo.paging.api

import com.google.gson.annotations.SerializedName

data class Photos(
        @SerializedName("page") val page: Int,
        @SerializedName("pages") val pages: Int,
        @SerializedName("perpage") val perPage: Int,
        @SerializedName("photo") val photo: List<Photo>,
        @SerializedName("total") val total: String
)