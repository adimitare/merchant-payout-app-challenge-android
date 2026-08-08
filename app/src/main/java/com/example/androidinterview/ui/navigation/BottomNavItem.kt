package com.example.androidinterview.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val destination: AppDestination,
    val label: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(
        destination = AppDestination.Home,
        label = "Home",
        icon = Icons.Outlined.Home
    ),
    BottomNavItem(
        destination = AppDestination.Payouts,
        label = "Payouts",
        icon = Icons.Outlined.Payments
    )
)