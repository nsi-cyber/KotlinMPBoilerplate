package com.nsicyber.composeboilerplatform.sharedlogic.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AppLaunchInfoDao {
    @Query("SELECT * FROM app_launch_info WHERE id = 1 LIMIT 1")
    suspend fun getOnce(): AppLaunchInfoEntity?

    @Query("SELECT * FROM app_launch_info WHERE id = 1 LIMIT 1")
    fun observe(): Flow<AppLaunchInfoEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: AppLaunchInfoEntity)
}
