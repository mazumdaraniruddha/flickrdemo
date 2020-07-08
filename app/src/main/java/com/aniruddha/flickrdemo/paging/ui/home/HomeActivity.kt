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

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import com.aniruddha.flickrdemo.paging.FlickrDemoApp
import com.aniruddha.flickrdemo.paging.databinding.ActivityHomeBinding
import com.aniruddha.flickrdemo.paging.ui.ViewModelFactory
import com.aniruddha.flickrdemo.paging.ui.custom.debounce
import com.aniruddha.flickrdemo.paging.ui.custom.onTextChanged
import com.aniruddha.flickrdemo.paging.ui.fullscreen.FullScreenImageActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@ExperimentalCoroutinesApi
class HomeActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var binding: ActivityHomeBinding
    private lateinit var viewModel: HomeViewModel
    private val adapter = PhotosAdapter { photoPosition ->
        val intent = Intent(this, FullScreenImageActivity::class.java)
        intent.putExtra(LAST_SEARCH_QUERY, viewModel.currentQueryValue)
        intent.putExtra(CLICKED_POSITION, photoPosition)
        startActivity(intent)
    }

    private var searchJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as FlickrDemoApp).appComponent.inject(this)

        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // get the view model
        viewModel = ViewModelProvider(this, viewModelFactory)
                .get(HomeViewModel::class.java)

        // add dividers between RecyclerView's row items
        val decoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        binding.rvPhotos.addItemDecoration(decoration)

        initAdapter()

        binding.btnRetry.setOnClickListener { adapter.retry() }

        val query = savedInstanceState?.getString(LAST_SEARCH_QUERY) ?: DEFAULT_QUERY
        search(query)
        initSearch(query)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(LAST_SEARCH_QUERY, binding.etSearch.text.trim().toString())
    }

    private fun search(query: String) {
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            viewModel.searchRepo(query)?.collectLatest {
                adapter.submitData(it.map {
                    it as UiModel.PhotoItem
                })
            }
        }
    }

    private fun initAdapter() {
        binding.rvPhotos.adapter = adapter.withLoadStateHeaderAndFooter(
                header = PhotosLoadStateAdapter { adapter.retry() },
                footer = PhotosLoadStateAdapter { adapter.retry() }
        )

        adapter.addLoadStateListener {loadState ->
            binding.rvPhotos.isVisible = loadState.refresh is LoadState.NotLoading
            binding.pbProgress.isVisible = loadState.refresh is LoadState.Loading
            binding.btnRetry.isVisible = loadState.refresh is LoadState.Error

            val errorState = loadState.source.append as? LoadState.Error
                    ?: loadState.source.prepend as? LoadState.Error
                    ?: loadState.append as? LoadState.Error
                    ?: loadState.prepend as? LoadState.Error
            errorState?.let {
                Toast.makeText(
                        this,
                        "Error: ${it.error}",
                        Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    private fun initSearch(query: String) {
        binding.etSearch.setText(query)

        lifecycleScope.launch {
            binding.etSearch
                    .onTextChanged()
                    .debounce(this, 300, TimeUnit.MILLISECONDS)
                    .consumeEach {
                        Log.d("SEARCH", "initSearch: $it")
                        updateRepoListFromInput()
                    }
        }
    }

    private fun updateRepoListFromInput() {
        binding.etSearch.text.trim().let {
            binding.rvPhotos.scrollToPosition(0)
            search(it.toString())
        }
    }

    companion object {
        private const val DEFAULT_QUERY = ""

        const val LAST_SEARCH_QUERY: String = "last_search_query"
        const val CLICKED_POSITION: String = "clicked_position"
    }
}
