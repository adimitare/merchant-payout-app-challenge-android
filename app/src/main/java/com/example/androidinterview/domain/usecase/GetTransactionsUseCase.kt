package com.example.androidinterview.domain.usecase

import androidx.paging.PagingData
import androidx.paging.insertSeparators
import androidx.paging.map
import com.example.androidinterview.domain.repository.ActivityRepository
import com.example.androidinterview.ui.transactions.TransactionListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

sealed interface TransactionDateLabel {

    data object Today : TransactionDateLabel

    data object Yesterday : TransactionDateLabel

    data class Date(
        val value: LocalDate
    ) : TransactionDateLabel
}

class GetTransactionsUseCase @Inject constructor(
    private val repository: ActivityRepository
) {
    operator fun invoke(): Flow<PagingData<TransactionListItem>> {
        return repository
            .getActivities()
            .map { pagingData ->
                pagingData
                    .map { activity ->
                        TransactionListItem.Transaction(activity)
                    }
                    .insertSeparators { before, after ->
                        if (after == null) {
                            return@insertSeparators null
                        }
                        val beforeDate = before
                            ?.let {
                                transactionLocalDate(
                                    it.activity.date
                                )
                            }
                        val afterDate = transactionLocalDate(
                            after.activity.date
                        )
                        if (beforeDate != afterDate) {
                            TransactionListItem.Header(
                                date = transactionDateLabel(afterDate)
                            )
                        } else {
                            null
                        }
                    }
            }
    }
}

fun transactionLocalDate(
    isoDate: String
): LocalDate {
    return Instant.parse(isoDate)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
}

fun transactionDateLabel(
    date: LocalDate
): TransactionDateLabel {
    val today = LocalDate.now()
    return when (date) {
        today -> TransactionDateLabel.Today
        today.minusDays(1) -> TransactionDateLabel.Yesterday
        else -> TransactionDateLabel.Date(date)
    }
}