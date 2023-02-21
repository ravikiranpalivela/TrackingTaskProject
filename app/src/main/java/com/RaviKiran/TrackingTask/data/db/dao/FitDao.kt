package com.RaviKiran.TrackingTask.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.RaviKiran.TrackingTask.data.db.entity.StepsEntity
import com.RaviKiran.TrackingTask.data.repository.DataSource
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import java.util.*

@Dao
interface StepsDao : DataSource<StepsEntity> {

    @Query("SELECT * FROM steps WHERE date >= :startTime AND date <= :endTime")
    override fun getAll(startTime: Date, endTime: Date): Single<List<StepsEntity>>

    @Query("DELETE FROM steps")
    override fun delete(): Completable

    @Insert
    override fun saveSteps(stepsList: List<StepsEntity>)
}