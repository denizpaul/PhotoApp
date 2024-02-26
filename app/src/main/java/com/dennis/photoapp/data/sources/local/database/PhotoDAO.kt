package com.dennis.photoapp.data.sources.local.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dennis.photoapp.data.sources.local.model.PhotoEntity

@Dao
interface PhotoDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(picture: PhotoEntity): Long

    @Query("SELECT * FROM photos")
    fun getAllPhotos():LiveData<List<PhotoEntity>>

    @Query("SELECT * FROM photos ORDER BY lastViewedTimeStamp DESC")
    fun getAllPhotosWithLastViewed(): List<PhotoEntity>

    @Query("SELECT * FROM photos ORDER BY likesCount DESC")
    fun getAllPhotosWithoutLiveData():List<PhotoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(pictures: List<PhotoEntity>)

    @Delete
    suspend fun deletePhotos(picture: PhotoEntity)
}