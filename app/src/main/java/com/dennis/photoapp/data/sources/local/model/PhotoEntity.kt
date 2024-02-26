package com.dennis.photoapp.data.sources.local.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "Photos")
data class PhotoEntity(

    @PrimaryKey
    val id: String = "0",

    val author: String? = null,

    val width: Int? = null,

    val downloadUrl: String? = null,

    val url: String? = null,

    val height: Int? = null,

    var likesCount: Int? = 0,

    var lastViewedTimeStamp: Long? = 0
) : Parcelable
