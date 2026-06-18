package com.nsicyber.composeboilerplatform.sharedlogic.core.database

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private val databaseInitLock = Mutex()
private var cachedDatabase: CoreDatabase? = null

/**
 * Returns a singleton CoreDatabase and ensures first-open metadata row exists.
 */
suspend fun coreDatabase(
    factory: CoreDatabaseFactory
): CoreDatabase {
    cachedDatabase?.let { return it }
    return databaseInitLock.withLock {
        cachedDatabase?.let { return it }
        val database = factory.createBuilder()
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
        ensureAppLaunchInfoSeeded(database)
        cachedDatabase = database
        database
    }
}

/**
 * Ensures app_launch_info table starts with one row.
 */
suspend fun ensureAppLaunchInfoSeeded(
    database: CoreDatabase
) {
    val existing = database.appLaunchInfoDao().getOnce()
    if (existing == null) {
        database.appLaunchInfoDao().upsert(
            AppLaunchInfoEntity(
                firstOpenedAtEpochMillis = currentEpochMillis()
            )
        )
    }
}

internal expect fun currentEpochMillis(): Long
