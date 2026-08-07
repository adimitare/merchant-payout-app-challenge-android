package com.example.androidinterview.domain.model

data class Merchant(
    val availableBalance: Int,
    val pendingBalance: Int,
    val currency: Currency,
    val activity: List<Activity>
)