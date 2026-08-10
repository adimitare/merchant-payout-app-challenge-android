package com.example.androidinterview

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.FragmentActivity
import com.example.androidinterview.domain.biometric.BiometricAuthenticator
import com.example.androidinterview.ui.home.HomeScreen
import com.example.androidinterview.ui.navigation.AppNavigation
import com.example.androidinterview.ui.theme.AndroidInterviewTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    @Inject
    lateinit var biometricAuthenticator: BiometricAuthenticator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidInterviewTheme {
                AppNavigation(biometricAuthenticator)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AndroidInterviewTheme {
        HomeScreen(
            onOpenTransactions = {}
        )
    }
}