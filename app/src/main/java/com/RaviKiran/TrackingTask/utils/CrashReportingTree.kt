@file:Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE", "LogNotTimber")

package com.RaviKiran.TrackingTask.utils

import android.util.Log
import timber.log.Timber

class CrashReportingTree : Timber.Tree() {

    override fun log(
        priority: Int,
        tag: String?,
        message: String,
        throwable: Throwable?
    ) {
        if (priority == Log.ERROR) {
            Log.e("CrashReportingTree", "[$tag] $message")
        } else {
            Log.d("CrashReportingTree", "[$tag] $message")
        }
    }
}