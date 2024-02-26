package com.dennis.photoapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.dennis.photoapp.R
import com.dennis.photoapp.databinding.FragmentPhotoDetailsBinding
import com.dennis.photoapp.ui.viewmodel.PhotoViewModel
import com.dennis.photoapp.ui.viewmodel.PhotoViewModelProviderFactory
import com.dennis.photoapp.util.ScreenState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class PhotoDetailFragment : Fragment(R.layout.fragment_photo_details)  {

    lateinit var binding: FragmentPhotoDetailsBinding

    @Inject
    lateinit var viewModelFactory: PhotoViewModelProviderFactory
    private val photoViewModel: PhotoViewModel by viewModels() {viewModelFactory}
    private val args: PhotoDetailFragmentArgs by navArgs()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPhotoDetailsBinding.bind(view)
        (activity as AppCompatActivity).supportActionBar?.title = "Photo Details"
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //get argument data and prefill ui
        val picture = args.picture
        Glide.with(this)
            .load(picture.downloadUrl)
            .into(binding.photoImage)
        binding.authorName.text = picture.author
        binding.likeButton.text = picture.likesCount.toString() + " Likes"

        //save last viewed time to db
        photoViewModel.saveLastViewedTimestamp(picture, System.currentTimeMillis())

        //increment like count and save to db
        binding.likeButton.setOnClickListener {
            binding.likeButton.text = (picture.likesCount?.plus(1)).toString() + " Likes"
            photoViewModel.savePhotoLike(picture)
        }
    }

}