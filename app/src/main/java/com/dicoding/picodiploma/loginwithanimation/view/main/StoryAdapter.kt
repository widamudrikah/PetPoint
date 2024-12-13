package com.dicoding.picodiploma.loginwithanimation.view.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.databinding.ItemStoryBinding
import com.dicoding.picodiploma.loginwithanimation.retrofit.ListStoryItem

class StoryAdapter(
    private val stories: List<ListStoryItem>,
    private val onItemClick: (ListStoryItem) -> Unit
) : RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryAdapter.StoryViewHolder, position: Int) {
        val story = stories[position]
        holder.bind(story)
    }

    override fun getItemCount(): Int = stories.size

    inner class StoryViewHolder(private val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem) {
            binding.tvName.text = story.name
            binding.tvDetail.text = story.description

            Glide.with(binding.root.context)
                .load(story.photoUrl)
                .into(binding.imgStory)

            binding.root.setOnClickListener {
                onItemClick(story)
            }
        }
    }
}