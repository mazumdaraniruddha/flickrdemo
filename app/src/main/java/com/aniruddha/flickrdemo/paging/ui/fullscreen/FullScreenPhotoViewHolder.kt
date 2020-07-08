package com.aniruddha.flickrdemo.paging.ui.fullscreen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aniruddha.flickrdemo.paging.R
import com.aniruddha.flickrdemo.paging.databinding.PhotoFullscreenViewholderBinding
import com.aniruddha.flickrdemo.paging.model.getImageUrl
import com.aniruddha.flickrdemo.paging.ui.home.UiModel
import com.bumptech.glide.Glide

class FullScreenPhotoViewHolder(private val binding: PhotoFullscreenViewholderBinding) : RecyclerView.ViewHolder(binding.root) {
    private var photo: UiModel.PhotoItem? = null

    fun bind(photo: UiModel.PhotoItem?) {
        if (photo == null) {
            // TODO: Show loading shimmer?
        } else {
            this.photo = photo
            // Load Image
            Glide.with(itemView)
                    .load(photo.photo.getImageUrl())
                    .fitCenter()
                    .into(binding.imgPhotoFullscreen)
        }
    }

    companion object {
        fun create(parent: ViewGroup): FullScreenPhotoViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.photo_fullscreen_viewholder, parent, false)
            val binding: PhotoFullscreenViewholderBinding = PhotoFullscreenViewholderBinding.bind(view)
            return FullScreenPhotoViewHolder(binding)
        }
    }
}
