package com.example.androidinterview.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface AppDestination : NavKey {
    @Serializable
    data object Home : AppDestination

    @Serializable
    data object Payouts : AppDestination

    @Serializable
    data object Transactions : AppDestination
}