package com.RaviKiran.TrackingTask.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.RaviKiran.TrackingTask.data.db.dao.*
import com.RaviKiran.TrackingTask.data.db.entity.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers

@Database(
    version = 1,
    entities = [
        StepsEntity::class,
    ],
    exportSchema = false,
)
@TypeConverters(
    DateConverter::class,
    DurationConverter::class
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun stepsDao(): StepsDao

    fun clear(): Completable =
        Completable.complete()
            .andThen(stepsDao().delete())
            .subscribeOn(Schedulers.io())
}