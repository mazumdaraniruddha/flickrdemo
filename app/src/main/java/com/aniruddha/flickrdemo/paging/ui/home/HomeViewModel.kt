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

package com.aniruddha.flickrdemo.paging.ui.home

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.aniruddha.flickrdemo.paging.model.Photo
import com.aniruddha.flickrdemo.paging.data.FlickrRepository
import com.aniruddha.flickrdemo.paging.data.IFlickerRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * ViewModel for the [HomeActivity] screen.
 * The ViewModel works with the [FlickrRepository] to get the data.
 */
@ExperimentalCoroutinesApi
class HomeViewModel(private val repository: IFlickerRepository) : ViewModel() {

    var currentQueryValue: String? = null
        private set

    fun searchRepo(queryString: String): Flow<PagingData<UiModel>>? {
        val lastResult = repository.getCurrentSearchResults()
        if (queryString == currentQueryValue && lastResult != null) {
            return lastResult
        }
        currentQueryValue = queryString
        val newResult: Flow<PagingData<UiModel>>? = repository.getSearchResultStream(queryString)
                ?.map {pagingData -> pagingData.map { photo ->
                    UiModel.PhotoItem(photo) as UiModel
                }}
                ?.cachedIn(viewModelScope)
        repository.setCurrentSearchResults(newResult)
        return newResult
    }
}

sealed class UiModel {
    data class PhotoItem(val photo: Photo): UiModel()
}