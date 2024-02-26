package com.dennis.photoapp.ui.viewmodel

import android.app.Application
import android.content.res.Resources
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dennis.photoapp.domain.usecases.PictureUseCase
import javax.inject.Inject

class PhotoViewModelProviderFactory @Inject constructor(val app: Application, val pictureUseCase: PictureUseCase, val res: Resources): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PhotoViewModel(app, pictureUseCase, res) as T
    }
}