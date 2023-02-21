package com.RaviKiran.TrackingTask.ui.statistic

import android.widget.TextView
import androidx.core.content.ContextCompat
import com.RaviKiran.TrackingTask.R

fun TextView.updateTabSelection(isSelected: Boolean) {
    val textColor = if (isSelected) R.color.white else R.color.gunmetal
    setTextColor(ContextCompat.getColor(context, textColor))

    if (isSelected) {
        setBackgroundResource(R.drawable.rounded_button)
    } else {
        background = null
    }
}