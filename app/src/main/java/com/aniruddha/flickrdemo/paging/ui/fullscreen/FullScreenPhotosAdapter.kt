package com.aniruddha.flickrdemo.paging.ui.fullscreen

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aniruddha.flickrdemo.paging.ui.home.PhotosAdapter.Companion.UIMODEL_COMPARATOR
import com.aniruddha.flickrdemo.paging.ui.home.UiModel

class FullScreenPhotosAdapter: PagingDataAdapter<UiModel.PhotoItem, RecyclerView.ViewHolder>(UIMODEL_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return FullScreenPhotoViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val uiModel: UiModel.PhotoItem? = getItem(position)
        (holder as FullScreenPhotoViewHolder).bind(uiModel)
    }
}