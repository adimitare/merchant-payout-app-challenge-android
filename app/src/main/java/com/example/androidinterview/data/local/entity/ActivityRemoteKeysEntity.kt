package com.example.androidinterview.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activity_remote_keys")
data class ActivityRemoteKeysEntity(
    @PrimaryKey
    val activityId: String,
    val nextCursor: String?
)