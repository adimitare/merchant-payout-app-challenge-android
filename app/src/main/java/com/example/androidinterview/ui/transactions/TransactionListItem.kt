package com.example.androidinterview.ui.transactions

import com.example.androidinterview.domain.model.ActivityItem
import com.example.androidinterview.domain.usecase.TransactionDateLabel

sealed interface TransactionListItem {
    data class Header(
        val date: TransactionDateLabel
    ) : TransactionListItem

    data class Transaction(
        val activity: ActivityItem
    ) : TransactionListItem
}