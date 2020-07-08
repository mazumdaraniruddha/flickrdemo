/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aniruddha.flickrdemo.paging.api

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import androidx.core.content.ContextCompat.getSystemService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


/**
 * API communication setup via Retrofit.
 */
interface ApiService {

    @GET("/services/rest/")
    suspend fun searchRepos(
            @Query("text") query: String,
            @Query("page") page: Int,
            @Query("per_page") itemsPerPage: Int,
            @Query("method") method: String,
            @Query("api_key") apiKey: String = API_KEY,
            @Query("format") format: String = "json",
            @Query("nojsoncallback") noJsonCallback: String = "nojsoncallback"
    ): FlickrResponse

    companion object {
        private const val BASE_URL = "https://api.flickr.com"
        private const val API_KEY = "062a6c0c49e4de1d78497d13a7dbb360"

        const val METHOD_SEARCH_QUERY = "flickr.photos.search"
        const val METHOD_GET_RECENT = "flickr.photos.getRecent"

        fun create(): ApiService {
            val logger = HttpLoggingInterceptor()
            logger.level = Level.BASIC

            val client = OkHttpClient.Builder()
                    .addInterceptor(logger)
                    .build()
            return Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ApiService::class.java)
        }

        fun isNetworkConnected(context: Context): Boolean {
            val cm = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager?
            return cm!!.activeNetworkInfo != null && cm.activeNetworkInfo.isConnected
        }
    }
}