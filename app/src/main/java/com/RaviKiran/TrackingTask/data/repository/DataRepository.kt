@file:Suppress("HasPlatformType")

package com.RaviKiran.TrackingTask.data.repository

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.RawQuery
import com.RaviKiran.TrackingTask.data.db.entity.StepsEntity
import com.RaviKiran.TrackingTask.fit.model.StepsInfo
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import timber.log.Timber

interface DataSource<T> {

    @RawQuery
    fun getAll(startTime: Date, endTime: Date): Single<List<T>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(list: List<T>): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveSteps(stepsList: List<StepsEntity>)

    @RawQuery
    fun delete(): Completable
}

abstract class DataRepository<L, T>(
    private val dataSource: String,
    private val localDataSource: DataSource<L>,
) {

    fun get(startTime: Date, endTime: Date): Observable<List<T>> = Observable.merge(
        localDataSource.getAll(startTime, endTime).map {
            localDataSource.saveSteps(getDates()!!)
            it.toModels()
        }.toObservable()
            .subscribeOn(Schedulers.io())
            .doOnNext {
                Timber.d("[$dataSource] local read $it")
            },
        localDataSource.getAll(startTime, endTime).map {
            it.toModels()
        }.toObservable()
            .subscribeOn(Schedulers.io())
            .doOnNext {
                Timber.d("[$dataSource] local read $it")
            }
    )

    fun saveDates(startTime: Date, endTime: Date) {
        localDataSource.saveSteps(getDates()!!)
    }

    abstract fun localToModel(local: L): T

    abstract fun modelToLocal(model: T): L

    private fun List<L>.toModels() = map { localToModel(it) }
    private fun List<T>.toLocals() = map { modelToLocal(it) }


    fun getDates(): List<StepsEntity>? {
        val dateFromAPI = "2023-01-01"
        val dateToAPI = "2023-12-31"
        val dates = ArrayList<StepsEntity>()
        try {
            val fromDate: Date = SimpleDateFormat("yyyy-MM-dd").parse(dateFromAPI)
            val toDate: Date = SimpleDateFormat("yyyy-MM-dd").parse(dateToAPI)
            val cal1 = Calendar.getInstance()
            cal1.time = fromDate
            val time1 = Calendar.getInstance()
            time1.time = toDate
            val cal2 = Calendar.getInstance()
            cal2.time = toDate
            var mul: Int = 1
            while (!cal1.after(cal2)) {
                dates.add(StepsEntity(cal1.time, 100*mul))
                mul++
                cal1.add(Calendar.DATE, 1)
            }
        } catch (exception: Exception) {

        }
        return dates
    }
}

// region FitData
class StepsDataRepository(
    localDataSource: DataSource<StepsEntity>,
) : DataRepository<StepsEntity, StepsInfo>("steps", localDataSource) {

    override fun localToModel(local: StepsEntity) = with(local) {
        StepsInfo(date, count)
    }

    override fun modelToLocal(model: StepsInfo) = with(model) {
        StepsEntity(date, count)
    }
}
// endregion