package com.example.androidinterview.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.androidinterview.data.local.entity.ActivityRemoteKeysEntity

@Dao
interface ActivityRemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(keys: List<ActivityRemoteKeysEntity>)

    @Query(
        """
        SELECT * FROM activity_remote_keys
        WHERE activityId = :activityId
        """
    )
    suspend fun remoteKeys(activityId: String): ActivityRemoteKeysEntity?

    @Query("DELETE FROM activity_remote_keys")
    suspend fun clearRemoteKeys()
}