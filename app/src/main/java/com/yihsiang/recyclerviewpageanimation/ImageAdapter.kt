package com.yihsiang.recyclerviewpageanimation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.yihsiang.recyclerviewpageanimation.databinding.ItemGalleryImageBinding

class ImageAdapter : ListAdapter<String, ImageViewHolder>(ImageDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            ItemGalleryImageBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

object ImageDiffCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}

class ImageViewHolder(private val binding: ItemGalleryImageBinding) :
    RecyclerView.ViewHolder(binding.root) {

        fun bind(imageUrl: String) {
            val transformations =
                MultiTransformation(CenterCrop(), RoundedCorners(40))
            Glide.with(binding.image)
                .load(imageUrl)
                .transform(transformations)
                .into(binding.image)
        }
}