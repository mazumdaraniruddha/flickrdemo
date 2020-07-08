package com.aniruddha.flickrdemo.paging.ui.fullscreen

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.aniruddha.flickrdemo.paging.data.FlickrRepository
import com.aniruddha.flickrdemo.paging.data.IFlickerRepository
import com.aniruddha.flickrdemo.paging.ui.home.UiModel
import kotlinx.coroutines.flow.Flow

class FullScreenViewModel(private val repository: IFlickerRepository) : ViewModel() {

    fun searchRepo(): Flow<PagingData<UiModel>>?
            = repository.getCurrentSearchResults()
}