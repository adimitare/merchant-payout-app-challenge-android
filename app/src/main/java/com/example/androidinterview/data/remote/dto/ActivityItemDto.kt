package com.example.androidinterview.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ActivityItemDto(
    val id: String,
    val type: String,
    val amount: Int,
    val currency: String,
    val date: String,
    val description: String,
    val status: String
)