package com.example.androidinterview.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun MainScaffold(
    currentRoute: AppDestination,
    onNavigate: (AppDestination) -> Unit,
    content: @Composable () -> Unit
) {
    Scaffold(
        bottomBar = {
            NavigationBar {

                NavigationBarItem(
                    selected = currentRoute == AppDestination.Home,
                    onClick = {
                        onNavigate(AppDestination.Home)
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home"
                        )
                    },
                    label = {
                        Text("Home")
                    }
                )

                NavigationBarItem(
                    selected = currentRoute == AppDestination.Payouts,
                    onClick = {
                        onNavigate(AppDestination.Payouts)
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Payments,
                            contentDescription = "Payouts"
                        )
                    },
                    label = {
                        Text("Payouts")
                    }
                )
            }
        }
    ) {
        content()
    }
}