package com.example.androidinterview.domain.biometric
sealed interface BiometricResult {
    data object Success : BiometricResult
    data object Cancelled : BiometricResult
    data object NotEnrolled : BiometricResult
    data object Unavailable : BiometricResult
    data object Failed : BiometricResult
}