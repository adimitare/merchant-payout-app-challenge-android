package com.example.androidinterview.domain.usecase

import androidx.paging.PagingData
import androidx.paging.testing.asSnapshot
import com.example.androidinterview.domain.model.ActivityItem
import com.example.androidinterview.domain.repository.ActivityRepository
import com.example.androidinterview.ui.transactions.TransactionListItem
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetTransactionsUseCaseTest {
    private lateinit var repository: ActivityRepository
    private lateinit var useCase: GetTransactionsUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetTransactionsUseCase(repository)
    }

    @Test
    fun `invoke maps activities to transaction items`() = runTest {
        // Given
        val activity1 = activityItem("2026-08-11T10:00:00Z")
        val activity2 = activityItem("2026-08-11T12:00:00Z")
        every {
            repository.getActivities()
        } returns flowOf(
            PagingData.from(listOf(activity1, activity2))
        )
        // When
        val result = useCase().asSnapshot()
        // Then
        assertEquals(3, result.size)
        assertEquals(
            TransactionListItem.Header(TransactionDateLabel.Today),
            result[0]
        )
        assertEquals(
            TransactionListItem.Transaction(activity1),
            result[1]
        )
        assertEquals(
            TransactionListItem.Transaction(activity2),
            result[2]
        )
    }

    @Test
    fun `invoke inserts header when transaction date changes`() = runTest {
        // Given
        val firstActivity = activityItem("2026-08-11T10:00:00Z")
        val secondActivity = activityItem("2026-08-10T10:00:00Z")
        every {
            repository.getActivities()
        } returns flowOf(
            PagingData.from(
                listOf(firstActivity, secondActivity)
            )
        )

        // When
        val result = useCase().asSnapshot()

        // Then
        assertEquals(4, result.size)

        assertEquals(
            TransactionListItem.Header(TransactionDateLabel.Today),
            result[0]
        )

        assertEquals(
            TransactionListItem.Transaction(firstActivity),
            result[1]
        )

        assertEquals(
            TransactionListItem.Header(TransactionDateLabel.Yesterday),
            result[2]
        )

        assertEquals(
            TransactionListItem.Transaction(secondActivity),
            result[3]
        )
    }

    @Test
    fun `invoke does not insert header when transactions have same date`() = runTest {
        // Given
        val activity1 = activityItem("2026-08-11T10:00:00Z")
        val activity2 = activityItem("2026-08-11T12:00:00Z")

        every {
            repository.getActivities()
        } returns flowOf(
            PagingData.from(
                listOf(activity1, activity2)
            )
        )

        // When
        val result = useCase().asSnapshot()

        // Then
        assertEquals(3, result.size)
        assertEquals(
            TransactionListItem.Header(TransactionDateLabel.Today),
            result[0]
        )
        assertEquals(
            TransactionListItem.Transaction(activity1),
            result[1]
        )
        assertEquals(
            TransactionListItem.Transaction(activity2),
            result[2]
        )
    }

    @Test
    fun `invoke returns empty list when repository returns empty paging data`() = runTest {
        // Given
        every {
            repository.getActivities()
        } returns flowOf(
            PagingData.from(emptyList())
        )
        // When
        val result = useCase().asSnapshot()
        // Then
        assertEquals(
            emptyList<TransactionListItem>(),
            result
        )
    }

    private fun activityItem(date: String): ActivityItem {
        return mockk {
            every { this@mockk.date } returns date
        }
    }
}