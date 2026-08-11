package com.example.androidinterview.ui.security

import android.app.Activity
import android.os.Build
import android.view.WindowManager
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

@Composable
fun ScreenshotProtection() {
    val context = LocalContext.current
    val activity = context as? Activity
    DisposableEffect(activity) {
        if (activity == null) {
            onDispose { }
        } else {
            // Prevent screenshots and screen recordings.
            activity.window.addFlags(
                WindowManager.LayoutParams.FLAG_SECURE
            )
            val callback =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    Activity.ScreenCaptureCallback {
                        Toast.makeText(
                            context,
                            "Please keep your financial data private. " +
                                    "Screenshots may contain sensitive information.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    null
                }
            if (callback != null) {
                activity.registerScreenCaptureCallback(
                    activity.mainExecutor,
                    callback
                )
            }
            onDispose {
                if (callback != null) {
                    activity.unregisterScreenCaptureCallback(
                        callback
                    )
                }
                activity.window.clearFlags(
                    WindowManager.LayoutParams.FLAG_SECURE
                )
            }
        }
    }
}