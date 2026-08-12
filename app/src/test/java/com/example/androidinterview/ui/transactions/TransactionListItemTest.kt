package com.example.androidinterview.ui.transactions

import com.example.androidinterview.domain.model.ActivityItem
import com.example.androidinterview.domain.model.Currency
import com.example.androidinterview.domain.usecase.TransactionDateLabel
import org.junit.Assert.assertEquals
import org.junit.Test

class TransactionListItemTest {

    @Test
    fun header_storesDate() {
        val item = TransactionListItem.Header(
            date = TransactionDateLabel.Today
        )
        assertEquals(
            TransactionDateLabel.Today,
            item.date
        )
    }

    @Test
    fun transaction_storesActivity() {
        val activity = ActivityItem(
            id = "1",
            description = "Coffee",
            type = "Card payment",
            amount = 1000,
            currency = Currency.GBP,
            status = "COMPLETED",
            date = "2026-08-12T10:00:00Z"
        )
        val item = TransactionListItem.Transaction(
            activity = activity
        )
        assertEquals(
            activity,
            item.activity
        )
    }
}