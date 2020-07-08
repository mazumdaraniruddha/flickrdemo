package com.aniruddha.flickrdemo.paging

import android.app.Application
import com.aniruddha.flickrdemo.paging.di.AppComponent
import com.aniruddha.flickrdemo.paging.di.DaggerAppComponent
import com.facebook.stetho.Stetho

class FlickrDemoApp: Application() {

    // Instance of the AppComponent that will be used by all the Activities in the project
    val appComponent: AppComponent by lazy {
        initializeComponent()
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this);
        }
    }

    open fun initializeComponent(): AppComponent {
        // Creates an instance of AppComponent using its Factory constructor
        // We pass the applicationContext that will be used as Context in the graph
        return DaggerAppComponent.factory().create(applicationContext)
    }
}