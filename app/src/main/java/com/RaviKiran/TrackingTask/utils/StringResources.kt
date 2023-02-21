package com.RaviKiran.TrackingTask.utils

import androidx.annotation.StringRes

class StringResources(
    private val activityEngine: ActivityEngine,
) {

    fun getString(@StringRes resId: Int): String =
        activityEngine.activity?.getString(resId) ?: ""
}