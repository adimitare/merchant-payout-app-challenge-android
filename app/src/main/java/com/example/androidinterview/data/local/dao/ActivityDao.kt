package com.example.androidinterview.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.androidinterview.data.local.entity.ActivityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {

    @Query(
        """
        SELECT * FROM activities
        ORDER BY date DESC
        """
    )
    fun pagingSource(): PagingSource<Int, ActivityEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ActivityEntity>)

    @Query("DELETE FROM activities")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM activities")
    suspend fun count(): Int
}