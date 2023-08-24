package com.example.mystories.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mystories.AddStoriesActivity
import com.example.mystories.MainActivity
import com.example.mystories.MapsActivity
import com.example.mystories.api.ListStoryItem
import com.example.mystories.databinding.ActivityHomeBinding
import com.example.mystories.preferences.UserPreference
import com.example.mystories.viewmodel.HomeViewModel
import com.example.mystories.viewmodel.ViewModelFactory

class HomeActivity: AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var recyclerAdapter: StoryAdapter
    private lateinit var userPreference: UserPreference
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreference = UserPreference(this)

        val token = userPreference.getToken()
        val viewModelFactory = ViewModelFactory(this)

        homeViewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)
        homeViewModel.getListStory("Bearer $token")
        homeViewModel.story.observe(this){ story ->
            setListStory(story)
        }

        homeViewModel.isLoading.observe(this){
            showLoading(it)
        }

        homeViewModel.isShowData.observe(this){
            showNoData(it)
        }

        showRecyclerview()
        setAction()

    }

    private fun setListStory(story: List<ListStoryItem>) {
        recyclerAdapter = StoryAdapter(story)
        binding.rvItem.adapter = recyclerAdapter
        homeViewModel.storyPaging.observe(this){
            recyclerAdapter.submitData(lifecycle, it)
        }
    }

    private fun showRecyclerview() {
        binding.rvItem.layoutManager = LinearLayoutManager(this)
        binding.rvItem.setHasFixedSize(true)
    }

    private fun moveIntentLogout(){
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun setAction() {
        binding.toolbar.imgAdd.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, AddStoriesActivity::class.java))
        })

        binding.toolbar.imgLocatiion.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
        })
        binding.toolbar.imgLogout.setOnClickListener{clickLogout()}

    }

    private fun clickLogout() {
        userPreference.clearToken()
        moveIntentLogout()
    }

    private fun showLoading(isLoading: Boolean){
        if(isLoading){
            binding.progressBar.visibility = View.VISIBLE
        }else{
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun showNoData(state: Boolean){
        if(state){
            binding.imgNoDataId.visibility = View.VISIBLE
        }else{
            binding.imgNoDataId.visibility = View.GONE
        }
    }
}
