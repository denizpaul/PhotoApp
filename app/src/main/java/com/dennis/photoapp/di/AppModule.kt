package com.dennis.photoapp.di

import android.app.Application
import android.content.Context
import android.content.res.Resources
import com.dennis.photoapp.data.sources.remote.api.PictureApi
import com.dennis.photoapp.data.repository.PhotoRepository
import com.dennis.photoapp.domain.usecases.PictureUseCase
import com.dennis.photoapp.util.ApiConstants.Companion.PHOTO_API_BASE_URL
import com.dennis.photoapp.data.sources.local.database.PhotoDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    fun provideResources(context: Context): Resources {
        return context.resources
    }

    @Provides
    fun providesBaseUrl(): String {
        return PHOTO_API_BASE_URL;
    }

    @Provides
    fun providesHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Provides
    fun provideCacheInterceptor(): Interceptor {
        return Interceptor { chain ->
            val originalRequest = chain.request()
            val cacheControl = CacheControl.Builder()
                .maxAge(5, TimeUnit.MINUTES) // Set your desired cache duration
                .build()

            val request = originalRequest.newBuilder()
                .cacheControl(cacheControl)
                .build()

            val response = chain.proceed(request)

            response.newBuilder()
                .header("Cache-Control", cacheControl.toString())
                .build()
        }
    }

    @Provides
    fun providesOkhttpClient(interceptor: HttpLoggingInterceptor, cacheInterceptor: Interceptor, @ApplicationContext applicationContext: Context): OkHttpClient {
        val okHttpClient = OkHttpClient.Builder()
            .cache(Cache(File(applicationContext.cacheDir, "http-cache"), 10L * 1024L * 1024L)) // 10 MiB
            .addInterceptor(interceptor)
            .addNetworkInterceptor(cacheInterceptor)
            .callTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
        return okHttpClient.build()
    }


    @Provides
    fun providesConverterFactory(): Converter.Factory {
        return GsonConverterFactory.create()
    }

    @Provides
    fun providesRetrofit(
        baseUrl: String,
        converterFactory: Converter.Factory,
        client: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(converterFactory)
            .client(client)
            .build()
    }

    @Provides
    fun providesRetrofitInstance(retrofit: Retrofit): PictureApi {
        return retrofit.create(PictureApi::class.java)
    }

    @Provides
    fun providesPhotoDatabaseInstance(context: Context): PhotoDatabase {
        return PhotoDatabase.invoke(context)
    }

    @Provides
    @Singleton
    fun providesPhotoRepository(api: PictureApi, database: PhotoDatabase): PhotoRepository {
        return PhotoRepository(api, database)
    }

    @Provides
    fun providesPictureUseCase(repository: PhotoRepository): PictureUseCase {
        return PictureUseCase(repository)
    }


}
