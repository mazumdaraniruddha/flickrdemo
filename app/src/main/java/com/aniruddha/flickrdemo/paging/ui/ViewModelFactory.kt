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

package com.aniruddha.flickrdemo.paging.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aniruddha.flickrdemo.paging.data.FlickrRepository
import com.aniruddha.flickrdemo.paging.data.IFlickerRepository
import com.aniruddha.flickrdemo.paging.ui.fullscreen.FullScreenViewModel
import com.aniruddha.flickrdemo.paging.ui.home.HomeViewModel
import javax.inject.Inject

/**
 * Factory for ViewModels
 */
class ViewModelFactory @Inject constructor(val repository: IFlickerRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        } else if (modelClass.isAssignableFrom(FullScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FullScreenViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
