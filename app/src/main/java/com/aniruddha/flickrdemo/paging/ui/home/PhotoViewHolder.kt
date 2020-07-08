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

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.aniruddha.flickrdemo.paging.R
import com.aniruddha.flickrdemo.paging.databinding.PhotoViewholderItemBinding
import com.aniruddha.flickrdemo.paging.model.getImageUrl
import com.bumptech.glide.Glide

/**
 * View Holder for Photos in the Home screen list.
 */
class PhotoViewHolder(private val binding: PhotoViewholderItemBinding,
                      private val onClicked: (position: Int) -> Unit) : RecyclerView.ViewHolder(binding.root) {
    private var photo: UiModel.PhotoItem? = null

    init {
        itemView.setOnClickListener {
            onClicked.invoke(absoluteAdapterPosition)
        }
    }

    fun bind(photo: UiModel.PhotoItem?) {
        if (photo == null) {
            // TODO: Show loading shimmer?
        } else {
            this.photo = photo
            // Load Image
            Glide.with(itemView)
                    .load(photo.photo.getImageUrl())
                    .centerCrop()
                    .into(binding.imgPhoto)
            // Set title
            binding.tvTitle.text = photo.photo.title
        }
    }

    companion object {
        fun create(parent: ViewGroup, onClicked: (position: Int) -> Unit): PhotoViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.photo_viewholder_item, parent, false)
            val binding: PhotoViewholderItemBinding = PhotoViewholderItemBinding.bind(view)
            return PhotoViewHolder(binding, onClicked)
        }
    }
}
