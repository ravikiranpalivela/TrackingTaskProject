package com.RaviKiran.TrackingTask.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.RaviKiran.TrackingTask.fit.model.Duration
import java.util.*

@Entity(
    tableName = "steps"
)
data class StepsEntity(
    @PrimaryKey
    val date: Date,
    val count: Int,
)