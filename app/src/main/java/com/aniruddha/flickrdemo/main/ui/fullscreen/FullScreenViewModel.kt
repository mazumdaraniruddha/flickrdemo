package com.aniruddha.flickrdemo.main.ui.fullscreen

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.aniruddha.flickrdemo.main.data.IFlickerRepository
import com.aniruddha.flickrdemo.main.ui.home.UiModel
import kotlinx.coroutines.flow.Flow

class FullScreenViewModel(private val repository: IFlickerRepository) : ViewModel() {

    fun searchRepo(): Flow<PagingData<UiModel>>?
            = repository.getCurrentSearchResults()
}