package com.example.androidinterview.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activities")
data class ActivityEntity(
    @PrimaryKey
    val id: String,
    val type: String,
    val amount: Int,
    val currency: String,
    val date: String,
    val description: String,
    val status: String
)