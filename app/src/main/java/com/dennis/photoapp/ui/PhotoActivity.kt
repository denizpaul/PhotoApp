package com.dennis.photoapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.dennis.photoapp.R
import com.dennis.photoapp.data.sources.remote.api.PictureApi
import com.dennis.photoapp.databinding.ActivityPhotoBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PhotoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPhotoBinding

    @Inject
    lateinit var pictureApi: PictureApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Setup Tabs
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)

        binding.bottomNavigationView.setOnItemSelectedListener() { item ->
            // Clear the back stack whenever a tab is selected
            navController.popBackStack(navController.graph.startDestinationId, false)
            // Navigate to the selected destination
            navController.navigate(item.itemId)
            true
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}