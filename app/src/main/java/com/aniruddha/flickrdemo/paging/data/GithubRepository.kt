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

package com.aniruddha.flickrdemo.paging.data

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.aniruddha.flickrdemo.paging.api.ApiService
import com.aniruddha.flickrdemo.paging.api.Photo
import com.aniruddha.flickrdemo.paging.db.RepoDataBase
import com.aniruddha.flickrdemo.paging.model.Repo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

/**
 * Repository class that works with local and remote data sources.
 */
@ExperimentalCoroutinesApi
class GithubRepository(private val service: ApiService,
                       private val repoDataBase: RepoDataBase) {

    /**
     * Search photos whose which might match the query, exposed as a stream of data that will emit
     * every time we get more data from the network.
     */
    fun getSearchResultStream(query: String): Flow<PagingData<Photo>> {
        Log.d(TAG, "NEW QUERY: $query")
        val pagingSourceFactory = { repoDataBase.photosDao().getReposByName(query) }

        return Pager(config = PagingConfig(pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false),
                remoteMediator = GithubRemoteMediator(
                        query,
                        service,
                        repoDataBase
                ),
                pagingSourceFactory = pagingSourceFactory).flow
    }

    companion object {
        private val TAG = GithubRepository::class.java.simpleName
        private const val NETWORK_PAGE_SIZE = 20
    }
}
