package com.example.androidinterview.domain.biometric

import androidx.fragment.app.FragmentActivity

interface BiometricAuthenticator {
    fun authenticate(
        activity: FragmentActivity,
        onResult: (BiometricResult) -> Unit,
        onFailed: () -> Unit
    )
}