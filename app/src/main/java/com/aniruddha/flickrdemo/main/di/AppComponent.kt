package com.aniruddha.flickrdemo.main.di

import android.content.Context
import com.aniruddha.flickrdemo.main.ui.fullscreen.FullScreenImageActivity
import com.aniruddha.flickrdemo.main.ui.home.HomeActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
// Definition of a Dagger component that adds info from the different modules to the graph
@Component(modules = [AppModule::class])
interface AppComponent {

    // Factory to create instances of the AppComponent
    @Component.Factory
    interface Factory {
        // With @BindsInstance, the Context passed in will be available in the graph
        fun create(@BindsInstance context: Context): AppComponent
    }

    // Inject into components like activity/fragments etc.
    fun inject(homeActivity: HomeActivity)
    fun inject(fullScreenImageActivity: FullScreenImageActivity)
}