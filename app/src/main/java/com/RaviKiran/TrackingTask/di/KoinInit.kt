package com.RaviKiran.TrackingTask.di

import android.app.Application
import com.RaviKiran.TrackingTask.utils.ActivityEngine
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

// TODO: migrate to Hilt
object KoinInit {

    fun init(application: Application) {
        startKoin {
            androidContext(application)
            modules(
                dbModule,
                fitModule,
                presentationModule,
                utilsModule,
            )
            koin.get<ActivityEngine>()
        }
    }
}