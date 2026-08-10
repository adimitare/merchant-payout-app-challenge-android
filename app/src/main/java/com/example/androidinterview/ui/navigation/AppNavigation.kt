package com.example.androidinterview.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.androidinterview.ui.home.HomeScreen
import com.example.androidinterview.ui.payout.PayoutScreen
import com.example.androidinterview.ui.transactions.TransactionListScreen

@Composable
fun AppNavigation() {
    val backStack = rememberNavBackStack(
        AppDestination.Home
    )
    val currentDestination = backStack.lastOrNull()
    val showBottomBar =
        currentDestination is AppDestination.Home ||
                currentDestination is AppDestination.Payouts

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(
                    currentDestination = currentDestination,
                    onDestinationSelected = { destination ->
                        if (destination == currentDestination) {
                            return@BottomNavigationBar
                        }
                        if (destination == AppDestination.Home) {
                            // Returning to Home should remove the current child/root.
                            if (backStack.size > 1) {
                                backStack.removeLastOrNull()
                            }
                        } else {
                            backStack.add(destination)
                        }
                    }
                )
            }
        }
    ) { _ ->
        NavDisplay(
            backStack = backStack,
            onBack = {
                if (backStack.size > 1) {
                    backStack.removeLastOrNull()
                }
            },
            entryProvider = entryProvider {
                entry<AppDestination.Home> {
                    HomeScreen(
                        onOpenTransactions = {
                            backStack.add(AppDestination.Transactions)
                        }
                    )
                }
                entry<AppDestination.Payouts> {
                    PayoutScreen()
                }
                entry<AppDestination.Transactions> {
                    TransactionListScreen(
                        onClose = {
                            if (backStack.size > 1) {
                                backStack.removeLastOrNull()
                            }
                        }
                    )
                }
            }
        )
    }
}

@Composable
private fun BottomNavigationBar(
    currentDestination: AppDestination?,
    onDestinationSelected: (AppDestination) -> Unit
) {
    NavigationBar {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                selected = currentDestination == item.destination,
                onClick = {
                    onDestinationSelected(item.destination)
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(item.label)
                }
            )
        }
    }
}