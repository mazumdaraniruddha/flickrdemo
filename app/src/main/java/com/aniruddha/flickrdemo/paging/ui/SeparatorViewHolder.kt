package com.aniruddha.flickrdemo.paging.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aniruddha.flickrdemo.paging.R
import com.aniruddha.flickrdemo.paging.databinding.SeparatorViewItemBinding

class SeparatorViewHolder(private val binding: SeparatorViewItemBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(text: String) {
        binding.separatorDescription.text = text
    }

    companion object {
        fun create(parent: ViewGroup): SeparatorViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.separator_view_item, parent, false)
            val binding = SeparatorViewItemBinding.bind(view)
            return SeparatorViewHolder(binding)
        }
    }
}