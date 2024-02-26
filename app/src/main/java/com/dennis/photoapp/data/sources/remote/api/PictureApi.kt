package com.dennis.photoapp.data.sources.remote.api

import com.dennis.photoapp.data.sources.remote.model.Picture
import retrofit2.http.GET

interface PictureApi {

    @GET("v2/list")
    suspend fun getAllPictures(
    ):List<Picture>

}