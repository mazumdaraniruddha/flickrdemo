package com.aniruddha.flickrdemo.main.di

import android.content.Context
import com.aniruddha.flickrdemo.main.api.ApiService
import com.aniruddha.flickrdemo.main.data.FlickrRepository
import com.aniruddha.flickrdemo.main.data.IFlickerRepository
import com.aniruddha.flickrdemo.main.db.FlickrDataBase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Singleton
    @Provides
    fun provideRepository(context: Context,
                          apiService: ApiService,
                          flickrDataBase: FlickrDataBase): IFlickerRepository {
        return FlickrRepository(context, apiService, flickrDataBase)
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