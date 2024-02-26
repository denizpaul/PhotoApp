package com.dennis.photoapp.data.sources.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dennis.photoapp.data.sources.local.model.PhotoEntity

@Database(entities = [PhotoEntity::class], version = 1)
abstract class PhotoDatabase: RoomDatabase() {

    abstract fun getPhotoDAO(): PhotoDAO

    companion object {
        @Volatile
        private var dbInstance: PhotoDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = dbInstance ?: synchronized(LOCK) {
            dbInstance ?: createDatabase(context)
        }

        private fun createDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            PhotoDatabase::class.java,
            "photo_db.db"
        ).build()
    }
}