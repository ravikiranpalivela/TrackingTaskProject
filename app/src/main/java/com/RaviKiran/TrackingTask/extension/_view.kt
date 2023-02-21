package com.RaviKiran.TrackingTask.extension

import android.view.View

inline fun View.onClick(crossinline block: () -> Unit) {
    setOnClickListener { block.invoke() }
}