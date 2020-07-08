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

package com.aniruddha.flickrdemo.main.data

import android.content.Context
import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.aniruddha.flickrdemo.main.api.ApiService
import com.aniruddha.flickrdemo.main.model.Photo
import com.aniruddha.flickrdemo.main.db.FlickrDataBase
import com.aniruddha.flickrdemo.main.ui.home.UiModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Repository class that works with local and remote data sources.
 */
@ExperimentalCoroutinesApi
class FlickrRepository @Inject constructor(
        val context: Context,
        val service: ApiService,
        val flickrDataBase: FlickrDataBase): IFlickerRepository {
    /**
     * Store the last searched snapshot in repo, which may be connected to by another, non-write
     * client
     * */
    var currentResults: Flow<PagingData<UiModel>>? = null

    override fun setCurrentSearchResults(result: Flow<PagingData<UiModel>>?) {
        this.currentResults = result
    }

    override fun getCurrentSearchResults() = currentResults

    /**
     * Search photos whose which might match the query, exposed as a stream of data that will emit
     * every time we get more data from the network.
     */
    override fun getSearchResultStream(query: String): Flow<PagingData<Photo>> {
        Log.d(TAG, "NEW QUERY: $query")
        val pagingSourceFactory = { flickrDataBase.photosDao().getReposByName("%${query.trim()}%" ) }

        return Pager(config = PagingConfig(pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false),
                remoteMediator = FlickrRemoteMediator(
                        context,
                        query,
                        service,
                        flickrDataBase
                ),
                pagingSourceFactory = pagingSourceFactory).flow
    }

    companion object {
        private val TAG = FlickrRepository::class.java.simpleName
        private const val NETWORK_PAGE_SIZE = 20
    }
}
