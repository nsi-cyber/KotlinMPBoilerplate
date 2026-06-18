package com.nsicyber.composeboilerplatform.sharedlogic.core.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor

@Database(
    entities = [AppLaunchInfoEntity::class],
    version = 1,
    exportSchema = true
)
@ConstructedBy(CoreDatabaseConstructor::class)
abstract class CoreDatabase : RoomDatabase() {
    abstract fun appLaunchInfoDao(): AppLaunchInfoDao

    companion object {
        const val DB_NAME: String = "core_database.db"
    }
}

/**
 * Room KSP generates the platform actual implementations for this constructor.
 */
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object CoreDatabaseConstructor : RoomDatabaseConstructor<CoreDatabase> {
    override fun initialize(): CoreDatabase
}
