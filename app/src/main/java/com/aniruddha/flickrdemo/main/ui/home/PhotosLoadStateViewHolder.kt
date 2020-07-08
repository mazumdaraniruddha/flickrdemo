package com.aniruddha.flickrdemo.main.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.aniruddha.flickrdemo.main.R
import com.aniruddha.flickrdemo.main.databinding.PhotosLoadStateFooterViewItemBinding

class PhotosLoadStateViewHolder(private val binding: PhotosLoadStateFooterViewItemBinding,
                                retry: () -> Unit): RecyclerView.ViewHolder(binding.root) {
    init {
        binding.btnRetryFooter.setOnClickListener { retry.invoke() }
    }

    fun bind(loadState: LoadState) {
        if (loadState is LoadState.Error) {
            binding.tvErrorMsg.text = loadState.error.localizedMessage
        }
        binding.pbFooterProgress.isVisible = loadState is LoadState.Loading
        binding.tvErrorMsg.isVisible = loadState !is LoadState.Loading
        binding.btnRetryFooter.isVisible = loadState !is LoadState.Loading
    }

    companion object {
        fun create(parent: ViewGroup, retry: () -> Unit): PhotosLoadStateViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.photos_load_state_footer_view_item, parent, false)
            val binding = PhotosLoadStateFooterViewItemBinding.bind(view)
            return PhotosLoadStateViewHolder(binding, retry)
        }
    }
}