package com.aniruddha.flickrdemo.main.data

import androidx.paging.PagingData
import com.aniruddha.flickrdemo.main.model.Photo
import com.aniruddha.flickrdemo.main.ui.home.UiModel
import kotlinx.coroutines.flow.Flow

interface IFlickerRepository {
    fun setCurrentSearchResults(result: Flow<PagingData<UiModel>>?)
    fun getCurrentSearchResults(): Flow<PagingData<UiModel>>?
    fun getSearchResultStream(query: String): Flow<PagingData<Photo>>?
}