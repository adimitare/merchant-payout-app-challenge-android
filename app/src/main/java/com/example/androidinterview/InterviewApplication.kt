package com.example.androidinterview

import android.app.Application
import com.example.androidinterview.mock.MockServerManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
@HiltAndroidApp
class InterviewApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        CoroutineScope(IO).launch {
            MockServerManager.start()
        }
    }
}
