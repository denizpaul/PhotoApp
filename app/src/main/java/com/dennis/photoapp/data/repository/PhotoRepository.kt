package com.dennis.photoapp.data.repository

import com.dennis.photoapp.data.sources.local.model.PhotoEntity
import com.dennis.photoapp.data.sources.remote.api.PictureApi
import com.dennis.photoapp.data.sources.local.database.PhotoDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

open class PhotoRepository @Inject constructor(private val pictureApi: PictureApi, private val database: PhotoDatabase) {
    /**
     * Get all pictures from database or from api if database is empty
     */
    suspend fun getAllPictures(): List<PhotoEntity> {
        return withContext(Dispatchers.IO) {
            val picturesFromDb = database.getPhotoDAO().getAllPhotosWithoutLiveData()
            if (picturesFromDb.isEmpty()) {
                val picturesFromApi = pictureApi.getAllPictures()

                val photoEntities = picturesFromApi.map { picture ->
                    PhotoEntity(
                        id = picture.id ?: "",
                        author = picture.author,
                        width = picture.width,
                        downloadUrl = picture.downloadUrl,
                        url = picture.url,
                        height = picture.height,
                        likesCount = 0, // Default value for likesCount
                        lastViewedTimeStamp = 0 // Default value for lastViewedTimeStamp
                    )
                }

                database.getPhotoDAO().insertAll(photoEntities)
                return@withContext database.getPhotoDAO().getAllPhotosWithoutLiveData() // Return photos from database after insertion
            } else {
                return@withContext picturesFromDb // Return photos from database if it's not empty
            }
        }
    }

    /**
     * Get all pictures from database or from api if database is empty
     * Sort by last viewed timestamp
     */
    suspend fun getAllPicturesLastViewed(): List<PhotoEntity> {
        return withContext(Dispatchers.IO) {
            val picturesFromDb = database.getPhotoDAO().getAllPhotosWithLastViewed()
            if (picturesFromDb.isEmpty()) {
                val picturesFromApi = pictureApi.getAllPictures()

                val photoEntities = picturesFromApi.map { picture ->
                    PhotoEntity(
                        id = picture.id ?: "",
                        author = picture.author,
                        width = picture.width,
                        downloadUrl = picture.downloadUrl,
                        url = picture.url,
                        height = picture.height,
                        likesCount = 0, // Default value for likesCount
                        lastViewedTimeStamp = 0 // Default value for lastViewedTimeStamp
                    )
                }

                database.getPhotoDAO().insertAll(photoEntities)
                return@withContext database.getPhotoDAO().getAllPhotosWithoutLiveData() // Return photos from database after insertion
            } else {
                return@withContext picturesFromDb // Return photos from database if it's not empty
            }
        }
    }

    /**
     * Insert or update photo details to database
     */
    suspend fun upsert(photo: PhotoEntity) =
        database.getPhotoDAO().upsert(photo)

}