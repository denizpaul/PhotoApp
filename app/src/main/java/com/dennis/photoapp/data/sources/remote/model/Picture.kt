package com.dennis.photoapp.data.sources.remote.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Picture(

    @field:SerializedName("author")
    val author: String? = null,

    @field:SerializedName("width")
    val width: Int? = null,

    @field:SerializedName("download_url")
    val downloadUrl: String? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("url")
    val url: String? = null,

    @field:SerializedName("height")
    val height: Int? = null
) : Parcelable
