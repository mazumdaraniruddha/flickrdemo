package com.aniruddha.flickrdemo.paging.di

import android.content.Context
import com.aniruddha.flickrdemo.paging.api.ApiService
import com.aniruddha.flickrdemo.paging.data.FlickrRepository
import com.aniruddha.flickrdemo.paging.data.IFlickerRepository
import com.aniruddha.flickrdemo.paging.db.FlickrDataBase
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Singleton
    @Provides
    fun provideRepository(apiService: ApiService, flickrDataBase: FlickrDataBase): IFlickerRepository {
        return FlickrRepository(apiService, flickrDataBase)
    }

    @Singleton
    @Provides
    fun provideApiService(): ApiService {
        return ApiService.create()
    }

    @Singleton
    @Provides
    fun providePhotoDatabase(context: Context): FlickrDataBase {
        return FlickrDataBase.getInstance(context)
    }
}