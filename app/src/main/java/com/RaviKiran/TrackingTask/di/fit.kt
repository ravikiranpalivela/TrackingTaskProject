@file:Suppress("RemoveExplicitTypeArguments")

package com.RaviKiran.TrackingTask.di

import com.RaviKiran.TrackingTask.data.db.dao.StepsDao
import com.RaviKiran.TrackingTask.data.repository.*
import com.RaviKiran.TrackingTask.fit.usecase.GetFitDataUseCase
import org.koin.dsl.module

val fitModule = module {
    factory<GetFitDataUseCase> {
        GetFitDataUseCase(
            get<StepsDataRepository>()
        )
    }
    factory<StepsDataRepository> {
        StepsDataRepository(
            get<StepsDao>()
        )
    }
}