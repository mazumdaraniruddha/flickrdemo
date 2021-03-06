package com.aniruddha.flickrdemo.main.ui.fullscreen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.aniruddha.flickrdemo.main.FlickrDemoApp
import com.aniruddha.flickrdemo.main.databinding.ActivityFullScreenImageBinding
import com.aniruddha.flickrdemo.main.ui.ViewModelFactory
import com.aniruddha.flickrdemo.main.ui.custom.RVPagerSnapHelperListenable
import com.aniruddha.flickrdemo.main.ui.home.HomeActivity
import com.aniruddha.flickrdemo.main.ui.home.UiModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Actvity to allow browsing the images in Fullscreen and Zoom in and out of images too
 * */
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
        binding.rvPhotosFullscreen.layoutManager = LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL,
                false)
        binding.rvPhotosFullscreen.adapter = adapter

        RVPagerSnapHelperListenable().attachToRecyclerView(binding.rvPhotosFullscreen, null)

        lifecycleScope.launch {
            viewModel.searchRepo()?.collectLatest {
                adapter.submitData(it.map {
                    it as UiModel.PhotoItem
                })
            }
        }
        binding.rvPhotosFullscreen.scrollToPosition(clickedPosition)
    }
}