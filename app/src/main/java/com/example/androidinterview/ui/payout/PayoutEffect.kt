package com.example.androidinterview.ui.payout

sealed interface PayoutEffect {
    data object AuthenticateBiometric : PayoutEffect
}