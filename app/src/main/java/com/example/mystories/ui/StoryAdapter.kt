package com.example.mystories.ui

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mystories.R
import com.example.mystories.api.ListStoryItem

class StoryAdapter(
    private var listStory: List<ListStoryItem>
):PagingDataAdapter<ListStoryItem,StoryAdapter.MyViewHolder>(DIFF_CALLBACK){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_story, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(listStory[position])
    }

    override fun getItemCount(): Int = listStory.size

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private var tvName = itemView.findViewById<TextView>(R.id.tv_item_name)
        private var tvDescription = itemView.findViewById<TextView>(R.id.tv_item_descriptions)
        private var photo = itemView.findViewById<ImageView>(R.id.iv_item_photo)
        private var tvCreated = itemView.findViewById<TextView>(R.id.tv_item_created)

        fun bind(story: ListStoryItem){
            Glide.with(itemView.context)
                .load(story.photoUrl)
                .circleCrop()
                .into(photo)
            tvName.text = story.name
            tvDescription.text = story.description
            tvCreated.text = story.createdAt

            itemView.setOnClickListener{
                val intent = Intent(itemView.context, DetailStoriesActivity::class.java)
                intent.putExtra(DetailStoriesActivity.EXTRA_ITEM, story)

                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(photo, "profile"),
                        Pair(tvName, "name"),
                        Pair(tvCreated, "createdAt"),
                        Pair(tvDescription, "description")
                    )

                itemView.context.startActivity(intent, optionsCompat.toBundle())
            }

        }
    }
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}