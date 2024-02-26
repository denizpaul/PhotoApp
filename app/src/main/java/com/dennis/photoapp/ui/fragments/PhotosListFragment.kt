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
import com.dennis.photoapp.ui.adapter.PhotoAdapter
import com.dennis.photoapp.ui.viewmodel.PhotoViewModel
import com.dennis.photoapp.ui.viewmodel.PhotoViewModelProviderFactory
import com.dennis.photoapp.util.ScreenState
import dagger.hilt.android.AndroidEntryPoint
import koleton.api.hideSkeleton
import koleton.api.loadSkeleton
import javax.inject.Inject

@AndroidEntryPoint
class PhotosListFragment : Fragment(R.layout.fragment_photo_list) {

    private var isLoading: Boolean = true
    private var isError: Boolean = false
    private lateinit var photoAdapter: PhotoAdapter
    lateinit var binding: FragmentPhotoListBinding

    @Inject
    lateinit var viewModelFactory: PhotoViewModelProviderFactory
    private val photoViewModel: PhotoViewModel by viewModels() {viewModelFactory}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPhotoListBinding.bind(view)
        (activity as AppCompatActivity).supportActionBar?.title = "Photos"
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)

        // Initialize recycler view
        photoAdapter = PhotoAdapter()
        binding.recyclerPhotos.apply {
            adapter = photoAdapter
            layoutManager = LinearLayoutManager(activity).apply {
                // Apply linear layout manager with vertical orientation
                orientation = LinearLayoutManager.VERTICAL
            }

            // Apply grid layout manager with 2 columns
            layoutManager = GridLayoutManager(activity, 2).apply {
            }
        }

        //Navigate to photo detail fragment
        photoAdapter.setOnItemClickListener {
            val action = PhotosListFragmentDirections.actionPhotoListFragmentToPhotoDetailFragment(it)
            findNavController().navigate(action)
        }

        // Observe photos live data
        photoViewModel.picturesLiveData.observe(viewLifecycleOwner) { screenState ->
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

        //Get photos from DB if it is not empty or from API
        photoViewModel.getAllPictures()

        binding.retryButton.setOnClickListener {
            photoViewModel.getAllPictures()
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