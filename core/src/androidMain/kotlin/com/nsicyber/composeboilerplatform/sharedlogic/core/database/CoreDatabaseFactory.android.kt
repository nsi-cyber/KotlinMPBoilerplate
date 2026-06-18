package com.nsicyber.composeboilerplatform.sharedlogic.core.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

actual class CoreDatabaseFactory actual constructor(
    context: Any?
) {
    private val appContext: Context = checkNotNull(context as? Context) {
        "CoreDatabaseFactory on Android requires applicationContext."
    }.applicationContext

    actual fun createBuilder(): RoomDatabase.Builder<CoreDatabase> {
        val dbFile = appContext.getDatabasePath(CoreDatabase.DB_NAME)
        return Room.databaseBuilder<CoreDatabase>(
            context = appContext,
            name = dbFile.absolutePath
        )
    }
}

internal actual fun currentEpochMillis(): Long = System.currentTimeMillis()
