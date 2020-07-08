package com.aniruddha.flickrdemo.paging.ui.fullscreen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.aniruddha.flickrdemo.paging.FlickrDemoApp
import com.aniruddha.flickrdemo.paging.databinding.ActivityFullScreenImageBinding
import com.aniruddha.flickrdemo.paging.ui.ViewModelFactory
import com.aniruddha.flickrdemo.paging.ui.custom.RVPagerSnapHelperListenable
import com.aniruddha.flickrdemo.paging.ui.home.HomeActivity
import com.aniruddha.flickrdemo.paging.ui.home.UiModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class FullScreenImageActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var binding: ActivityFullScreenImageBinding
    private lateinit var viewModel: FullScreenViewModel

    private var clickedPosition: Int = 0

    private val adapter = FullScreenPhotosAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as FlickrDemoApp).appComponent.inject(this)

        super.onCreate(savedInstanceState)
        binding = ActivityFullScreenImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        clickedPosition = savedInstanceState?.getInt(HomeActivity.CLICKED_POSITION)
                ?: intent.getIntExtra(HomeActivity.CLICKED_POSITION, 0)

        viewModel = ViewModelProvider(this, viewModelFactory)
                .get(FullScreenViewModel::class.java)

        initRecyclerView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(HomeActivity.CLICKED_POSITION, clickedPosition)
    }

    private fun initRecyclerView() {
        binding.list.layoutManager = LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL,
                false)
        binding.list.adapter = adapter

        RVPagerSnapHelperListenable().attachToRecyclerView(binding.list, null)

        lifecycleScope.launch {
            viewModel.searchRepo()?.collectLatest {
                adapter.submitData(it.map {
                    it as UiModel.PhotoItem
                })
            }
        }
        binding.list.scrollToPosition(clickedPosition)
    }
}