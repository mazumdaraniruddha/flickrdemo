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

import android.content.Intent
import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.aniruddha.flickrdemo.paging.R
import com.aniruddha.flickrdemo.paging.api.getImageUrl
import com.aniruddha.flickrdemo.paging.databinding.RepoViewItemBinding
import com.aniruddha.flickrdemo.paging.model.Repo
import com.bumptech.glide.Glide

/**
 * View Holder for a [Repo] RecyclerView list item.
 */
class RepoViewHolder(private val binding: RepoViewItemBinding) : RecyclerView.ViewHolder(binding.root) {
    private var repo: Repo? = null

    init {
        itemView.setOnClickListener {
            repo?.url?.let { url ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                itemView.context.startActivity(intent)
            }
        }
    }

    fun bind(photo: UiModel.PhotoItem?) {
        if (photo == null) {
            // TODO: Show loading shimmer?
        } else {
            this.repo = repo
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
        fun create(parent: ViewGroup): RepoViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.repo_view_item, parent, false)
            val binding: RepoViewItemBinding = RepoViewItemBinding.bind(view)
            return RepoViewHolder(binding)
        }
    }
}
