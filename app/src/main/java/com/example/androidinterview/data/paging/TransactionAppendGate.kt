package com.example.androidinterview.data.paging

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionAppendGate @Inject constructor() {
    @Volatile
    private var userHasScrolled: Boolean = false

    fun reset() {
        userHasScrolled = false
    }

    fun markUserScrolled() {
        userHasScrolled = true
    }

    fun isAppendAllowed(): Boolean = userHasScrolled
}
