package com.dennis.photoapp.domain.usecases

import com.dennis.photoapp.data.repository.PhotoRepository
import com.dennis.photoapp.data.sources.local.model.PhotoEntity
import javax.inject.Inject

class PictureUseCase @Inject constructor(private val photoRepository: PhotoRepository) {

    suspend fun getAllPictures(): List<PhotoEntity> {
        return photoRepository.getAllPictures()
    }

    suspend fun getAllPicturesLastViewed(): List<PhotoEntity> {
        return photoRepository.getAllPicturesLastViewed()
    }

    suspend fun savePhotoLike(photo: PhotoEntity) {
        photo.likesCount = photo.likesCount?.plus(1)
        photoRepository.upsert(photo)
    }

    suspend fun saveLastViewedTimestamp(photo: PhotoEntity, timestamp: Long) {
        photo.lastViewedTimeStamp = timestamp
        photoRepository.upsert(photo)
    }
}
