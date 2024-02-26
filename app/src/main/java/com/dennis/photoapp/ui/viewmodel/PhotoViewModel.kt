package com.dennis.photoapp.ui.viewmodel

import android.app.Application
import android.content.res.Resources
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dennis.photoapp.R
import com.dennis.photoapp.data.sources.local.model.PhotoEntity
import com.dennis.photoapp.domain.usecases.PictureUseCase
import com.dennis.photoapp.util.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.DecimalFormat
import javax.inject.Inject

@HiltViewModel
class PhotoViewModel @Inject constructor(
    application: Application,
    private val pictureUseCase: PictureUseCase,
    private val resources: Resources
) : AndroidViewModel(application) {

    private var picturesMutableLiveData: MutableLiveData<ScreenState<List<PhotoEntity>>> = MutableLiveData()
    val picturesLiveData : LiveData<ScreenState<List<PhotoEntity>>> = picturesMutableLiveData;

    private var picturesLastViewedMutableLiveData: MutableLiveData<ScreenState<List<PhotoEntity>>> = MutableLiveData()
    val picturesLastViewedLiveData : LiveData<ScreenState<List<PhotoEntity>>> = picturesLastViewedMutableLiveData;


    fun getAllPictures() = viewModelScope.launch {
        getAllPicturesData()
    }

    fun getAllPicturesLastViewed() = viewModelScope.launch {
        getAllPicturesLastViewedData()
    }

    fun savePhotoLike(photo: PhotoEntity) = viewModelScope.launch {
        pictureUseCase.savePhotoLike(photo)
    }

    fun saveLastViewedTimestamp(photo: PhotoEntity, timestamp: Long) = viewModelScope.launch {
        try {
            pictureUseCase.saveLastViewedTimestamp(photo, timestamp)
        } catch (e: Exception) {
            Log.e("PhotoViewModel", "Exception: ", e)
        }
    }

    private suspend fun getAllPicturesData() {
        picturesMutableLiveData.postValue(ScreenState.Loading())
        try {
            val response: List<PhotoEntity> = pictureUseCase.getAllPictures()
            picturesMutableLiveData.postValue(handlePicturesResponse(response))
        } catch (t: Throwable) {
            when(t) {
                is IOException -> picturesMutableLiveData.postValue(ScreenState.Error(resources.getString(
                    R.string.error_could_not_connect)))
                else -> picturesMutableLiveData.postValue(ScreenState.Error(resources.getString(
                    R.string.error_no_signal)))
            }
        }

    }

    private fun handlePicturesResponse(response: List<PhotoEntity>): ScreenState<List<PhotoEntity>> {
        if(response.isNotEmpty()) {
            response.let { list ->
                return ScreenState.Success(list!!)
            }
        }
        return ScreenState.Error("Error")
    }

    private suspend fun getAllPicturesLastViewedData() {
        picturesLastViewedMutableLiveData.postValue(ScreenState.Loading())
        try {
            val response: List<PhotoEntity> = pictureUseCase.getAllPicturesLastViewed()
            picturesLastViewedMutableLiveData.postValue(handlePicturesResponse(response))
        } catch (t: Throwable) {
            when(t) {
                is IOException -> picturesLastViewedMutableLiveData.postValue(ScreenState.Error(resources.getString(
                    R.string.error_could_not_connect)))
                else -> picturesLastViewedMutableLiveData.postValue(ScreenState.Error(resources.getString(
                    R.string.error_no_signal)))
            }
        }

    }

}