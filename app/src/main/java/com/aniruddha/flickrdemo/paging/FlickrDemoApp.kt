package com.aniruddha.flickrdemo.paging

import android.app.Application
import com.facebook.stetho.Stetho

class FlickrDemoApp: Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this);
        }
    }
}