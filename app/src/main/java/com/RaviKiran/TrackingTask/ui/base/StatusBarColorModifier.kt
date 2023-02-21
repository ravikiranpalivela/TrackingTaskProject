package com.RaviKiran.TrackingTask.ui.base

import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.RaviKiran.TrackingTask.R
import com.RaviKiran.TrackingTask.utils.ActivityEngine

interface StatusBarColorModifier {

    fun setColor(@ColorRes resId: Int)

    fun setDefaultColor()
}

class StatusBarColorModifierImpl(
    private val activityEngine: ActivityEngine
) : StatusBarColorModifier {

    override fun setColor(@ColorRes resId: Int) {
        val activity = activityEngine.activity ?: return
        activity.window?.statusBarColor = ContextCompat.getColor(activity, resId)
    }

    override fun setDefaultColor() {
        setColor(R.color.statusBar)
    }
}