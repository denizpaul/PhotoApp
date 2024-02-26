package com.dennis.photoapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dennis.photoapp.R
import com.dennis.photoapp.databinding.FragmentPhotoListBinding
import com.dennis.photoapp.ui.adapter.HomePageAdapter
import com.dennis.photoapp.ui.viewmodel.PhotoViewModel
import com.dennis.photoapp.ui.viewmodel.PhotoViewModelProviderFactory
import com.dennis.photoapp.util.ScreenState
import dagger.hilt.android.AndroidEntryPoint
import koleton.api.hideSkeleton
import koleton.api.loadSkeleton
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_photo_list) {

    private var isLoading: Boolean = true
    private var isError: Boolean = false
    private lateinit var photoAdapter: HomePageAdapter
    lateinit var binding: FragmentPhotoListBinding

    @Inject
    lateinit var viewModelFactory: PhotoViewModelProviderFactory
    private val photoViewModel: PhotoViewModel by viewModels() {viewModelFactory}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPhotoListBinding.bind(view)
        (activity as AppCompatActivity).supportActionBar?.title = "Home"
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)

        // Set up recycler view
        photoAdapter = HomePageAdapter()
        binding.recyclerPhotos.apply {
            adapter = photoAdapter
            layoutManager = LinearLayoutManager(activity).apply {
                // Apply linear layout manager with vertical orientation
                orientation = LinearLayoutManager.VERTICAL
            }

            // Apply grid layout manager with 2 columns
            layoutManager = GridLayoutManager(activity, 1).apply {
            }
        }

        // Navigate to photo detail fragment
        photoAdapter.setOnItemClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToPhotoDetailFragment(it)
            findNavController().navigate(action)
        }

        // Observe picturesLastViewedLiveData data
        photoViewModel.picturesLastViewedLiveData.observe(viewLifecycleOwner) { screenState ->
            when (screenState) {
                is ScreenState.Success<*> -> {
                    binding.recyclerPhotos.hideSkeleton()
                    hideErrorMessage()
                    screenState.data?.let {
                        photoAdapter.differ.submitList(screenState.data)
                    }
                }

                is ScreenState.Loading<*> -> {
                    binding.recyclerPhotos.loadSkeleton()
                }

                is ScreenState.Error<*> -> {
                    binding.recyclerPhotos.hideSkeleton()
                    showErrorMessage("Photos fetching failed")
                }

                else -> {}
            }

        }

        //get all pictures last viewed and all list
        photoViewModel.getAllPicturesLastViewed()

        // retry button
        binding.retryButton.setOnClickListener {
            photoViewModel.getAllPicturesLastViewed()
        }
    }

    private fun hideErrorMessage(){
        binding.photosError.visibility = View.INVISIBLE
        binding.recyclerPhotos.visibility = View.VISIBLE
        isError = false
    }

    private fun showErrorMessage(message: String){
        binding.photosError.visibility = View.VISIBLE
        binding.errorText.text = message
        binding.recyclerPhotos.visibility = View.INVISIBLE
        isError = true
    }
}