package com.example.androidinterview.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.androidinterview.data.local.dao.ActivityDao
import com.example.androidinterview.data.local.dao.ActivityRemoteKeysDao
import com.example.androidinterview.data.local.entity.ActivityEntity
import com.example.androidinterview.data.local.entity.ActivityRemoteKeysEntity

@Database(
    entities = [
        ActivityEntity::class,
        ActivityRemoteKeysEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun activityDao(): ActivityDao
    abstract fun activityRemoteKeysDao(): ActivityRemoteKeysDao
}