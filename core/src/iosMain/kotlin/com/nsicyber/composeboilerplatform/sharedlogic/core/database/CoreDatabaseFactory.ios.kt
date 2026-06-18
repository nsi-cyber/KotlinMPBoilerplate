@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.nsicyber.composeboilerplatform.sharedlogic.core.database

import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask
import platform.posix.time

actual class CoreDatabaseFactory actual constructor(
    context: Any?
) {
    actual fun createBuilder(): RoomDatabase.Builder<CoreDatabase> {
        val dbPath = documentDirectoryPath() + "/${CoreDatabase.DB_NAME}"
        return Room.databaseBuilder<CoreDatabase>(
            name = dbPath
        )
    }

    private fun documentDirectoryPath(): String {
        val directoryUrl = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null
        )
        return requireNotNull(directoryUrl?.path)
    }
}

internal actual fun currentEpochMillis(): Long = time(null).toLong() * 1000L
