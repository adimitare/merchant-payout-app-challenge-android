package com.example.androidinterview.data.repository

import androidx.paging.PagingData
import androidx.paging.testing.asSnapshot
import com.example.androidinterview.data.local.entity.ActivityEntity
import com.example.androidinterview.data.local.mapper.toDomain
import com.example.androidinterview.data.paging.ActivityPagerFactory
import com.example.androidinterview.domain.model.ActivityItem
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ActivityRepositoryImplTest {
    private lateinit var pagerFactory: ActivityPagerFactory
    private lateinit var repository: ActivityRepositoryImpl

    @Before
    fun setup() {
        pagerFactory = mockk()
        repository = ActivityRepositoryImpl(
            pagerFactory = pagerFactory
        )
    }

    @Test
    fun `getActivities maps entities to domain models`() = runTest {
        val entities = listOf(
            ActivityEntity(
                id = "1",
                type = "PAYOUT",
                amount = 100,
                currency = "GBP",
                date = "2024-01-01",
                description = "Test payout",
                status = "COMPLETED"
            ),
            ActivityEntity(
                id = "2",
                type = "REFUND",
                amount = 50,
                currency = "GBP",
                date = "2024-01-02",
                description = "Test refund",
                status = "PENDING"
            )
        )
        every {
            pagerFactory.create()
        } returns flowOf(
            PagingData.from(entities)
        )
        val result = repository
            .getActivities()
            .asSnapshot()
        assertEquals(2, result.size)
        assertEquals(
            entities[0].toDomain(),
            result[0]
        )
        assertEquals(
            entities[1].toDomain(),
            result[1]
        )
    }

    @Test
    fun `getActivities returns empty PagingData when factory returns empty data`() = runTest {
        every {
            pagerFactory.create()
        } returns flowOf(
            PagingData.empty()
        )
        val result = repository
            .getActivities()
            .asSnapshot()
        assertEquals(
            emptyList<ActivityItem>(),
            result
        )
    }
}