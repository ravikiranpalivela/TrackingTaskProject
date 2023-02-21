@file:Suppress("RemoveExplicitTypeArguments")

package com.RaviKiran.TrackingTask.di

import com.RaviKiran.TrackingTask.fit.usecase.GetFitDataUseCase
import com.RaviKiran.TrackingTask.ui.base.EmptyViewModel
import com.RaviKiran.TrackingTask.ui.base.StatusBarColorModifier
import com.RaviKiran.TrackingTask.ui.base.StatusBarColorModifierImpl
import com.RaviKiran.TrackingTask.ui.statistic.StatisticViewModel
import com.RaviKiran.TrackingTask.utils.ActivityEngine
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    factory<StatusBarColorModifier> {
        StatusBarColorModifierImpl(get<ActivityEngine>())
    }
    viewModel {
        EmptyViewModel()
    }
    viewModel {
        StatisticViewModel(androidContext(), get<GetFitDataUseCase>())
    }
}