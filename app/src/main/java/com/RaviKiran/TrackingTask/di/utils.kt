@file:Suppress("RemoveExplicitTypeArguments")

package com.RaviKiran.TrackingTask.di

import android.app.NotificationManager
import androidx.core.content.getSystemService
import androidx.work.WorkManager
import com.RaviKiran.TrackingTask.utils.ActivityEngine
import com.RaviKiran.TrackingTask.utils.ActivityEngineImpl
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val utilsModule = module {
    single<ActivityEngine> {
        ActivityEngineImpl(androidApplication())
    }
    single<WorkManager> {
        WorkManager.getInstance(androidContext())
    }
}