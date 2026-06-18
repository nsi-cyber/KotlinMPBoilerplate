package com.nsicyber.composeboilerplatform.sharedlogic.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Stores immutable first app-open timestamp for bootstrap diagnostics.
 */
@Entity(tableName = "app_launch_info")
data class AppLaunchInfoEntity(
    @PrimaryKey val id: Int = 1,
    val firstOpenedAtEpochMillis: Long
)
