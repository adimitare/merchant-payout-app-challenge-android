package com.example.androidinterview.data.biometric

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.androidinterview.R
import com.example.androidinterview.domain.biometric.BiometricAuthenticator
import com.example.androidinterview.domain.biometric.BiometricResult
import javax.inject.Inject

class AndroidBiometricAuthenticator @Inject constructor() :
    BiometricAuthenticator {

    override fun authenticate(
        activity: FragmentActivity,
        onResult: (BiometricResult) -> Unit,
        onFailed: () -> Unit
    ) {
        val biometricManager = BiometricManager.from(activity)
        val authenticators =
            BiometricManager.Authenticators.BIOMETRIC_STRONG
        when (
            biometricManager.canAuthenticate(authenticators)
        ) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                showPrompt(
                    activity = activity,
                    onResult = onResult
                )
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                onResult(BiometricResult.NotEnrolled)
            }
            else -> {
                onResult(BiometricResult.Unavailable)
            }
        }
    }

    private fun showPrompt(
        activity: FragmentActivity,
        onResult: (BiometricResult) -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)
        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    onResult(BiometricResult.Success)
                }
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    onResult(
                        if (
                            errorCode ==
                            BiometricPrompt.ERROR_NEGATIVE_BUTTON ||
                            errorCode ==
                            BiometricPrompt.ERROR_USER_CANCELED
                        ) {
                            BiometricResult.Cancelled
                        } else {
                            BiometricResult.Failed
                        }
                    )
                }

                override fun onAuthenticationFailed() {
                    // The user can retry within the prompt.
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(activity.getString(R.string.biometric_authentication))
            .setSubtitle(activity.getString(R.string.authenticate_to_complete_the_payout))
            .setNegativeButtonText(activity.getString(R.string.cancel_biometric))
            .build()
        biometricPrompt.authenticate(promptInfo)
    }
}