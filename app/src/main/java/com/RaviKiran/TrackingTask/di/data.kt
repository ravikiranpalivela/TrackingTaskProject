@file:Suppress("RemoveExplicitTypeArguments")

package com.RaviKiran.TrackingTask.di

import androidx.room.Room
import com.RaviKiran.TrackingTask.data.db.AppDatabase
import com.RaviKiran.TrackingTask.data.db.dao.*
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dbModule = module {
    single<AppDatabase> {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "taskDataBase.db")
            .fallbackToDestructiveMigration()
            .build()
    }
    factory<StepsDao> {
        get<AppDatabase>().stepsDao()
    }
}