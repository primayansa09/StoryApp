package com.example.mystories.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.mystories.api.ListStoryItem
import com.example.mystories.databinding.ActivityDetailStoriesBinding

class DetailStoriesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoriesBinding

    companion object{
        const val EXTRA_ITEM = "extra_item"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarDetail.btnBack.setOnClickListener{
            finish()
        }
        setupDetail()

    }

    private fun setupDetail() {
        val data = intent.getParcelableExtra<ListStoryItem>(EXTRA_ITEM) as ListStoryItem
        Glide.with(applicationContext)
            .load(data.photoUrl)
            .into(binding.detailContent.ivDetailPhoto)
        binding.detailContent.tvDetailName.text = data.name
        binding.detailContent.tvDetailCreated.text = data.createdAt
        binding.detailContent.tvDetailDescriptions.text = data.description
    }
}