package com.nsicyber.composeboilerplatform.sharedlogic.core.database

import androidx.room.RoomDatabase

/**
 * Platform factory that provides Room builder with correct file path handling.
 */
expect class CoreDatabaseFactory {
    constructor(context: Any? = null)

    fun createBuilder(): RoomDatabase.Builder<CoreDatabase>
}
