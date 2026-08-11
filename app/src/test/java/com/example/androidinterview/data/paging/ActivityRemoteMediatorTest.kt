package com.example.androidinterview.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.androidinterview.data.local.AppDatabase
import com.example.androidinterview.data.local.entity.ActivityEntity
import com.example.androidinterview.data.local.entity.ActivityRemoteKeysEntity
import com.example.androidinterview.data.remote.MerchantApi
import com.example.androidinterview.data.remote.dto.ActivityItemDto
import com.example.androidinterview.data.remote.dto.ActivityResponseDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class ActivityRemoteMediatorTest {
    private lateinit var database: AppDatabase
    private lateinit var api: MerchantApi
    private lateinit var mediator: ActivityRemoteMediator

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        api = mockk()

        mediator = ActivityRemoteMediator(
            api = api,
            database = database
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `REFRESH loads first page and stores activities`() = runTest {
        // Given
        val response = activityResponse(
            items = listOf(
                activityDto(id = "1"),
                activityDto(id = "2")
            ),
            nextCursor = "cursor-1",
            hasMore = true
        )

        coEvery {
            api.getActivityResponse(
                cursor = null,
                limit = 20
            )
        } returns response

        // When
        val result = mediator.load(
            LoadType.REFRESH,
            pagingState()
        )

        // Then
        assertTrue(
            result is RemoteMediator.MediatorResult.Success
        )

        val success = result as RemoteMediator.MediatorResult.Success

        assertFalse(success.endOfPaginationReached)

        assertEquals(
            2,
            database.activityDao().count()
        )

        assertEquals(
            "cursor-1",
            database.activityRemoteKeysDao()
                .remoteKeys("1")
                ?.nextCursor
        )

        assertEquals(
            "cursor-1",
            database.activityRemoteKeysDao()
                .remoteKeys("2")
                ?.nextCursor
        )

        coVerify(exactly = 1) {
            api.getActivityResponse(
                cursor = null,
                limit = 20
            )
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `REFRESH clears existing activities and remote keys`() = runTest {
        // Given
        database.activityDao().insertAll(
            listOf(
                activityEntity("old-1"),
                activityEntity("old-2")
            )
        )

        database.activityRemoteKeysDao().insertAll(
            listOf(
                remoteKey("old-1", "old-cursor"),
                remoteKey("old-2", "old-cursor")
            )
        )

        val response = activityResponse(
            items = listOf(
                activityDto("new-1")
            ),
            nextCursor = "new-cursor",
            hasMore = false
        )

        coEvery {
            api.getActivityResponse(
                cursor = null,
                limit = 20
            )
        } returns response

        // When
        val result = mediator.load(
            LoadType.REFRESH,
            pagingState()
        )

        // Then
        assertTrue(
            result is RemoteMediator.MediatorResult.Success
        )

        assertEquals(
            1,
            database.activityDao().count()
        )

        assertEquals(
            null,
            database.activityRemoteKeysDao()
                .remoteKeys("old-1")
        )

        assertEquals(
            null,
            database.activityRemoteKeysDao()
                .remoteKeys("old-2")
        )

        assertEquals(
            "new-cursor",
            database.activityRemoteKeysDao()
                .remoteKeys("new-1")
                ?.nextCursor
        )
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `REFRESH returns end of pagination when hasMore is false`() =
        runTest {
            // Given
            val response = activityResponse(
                items = listOf(
                    activityDto("1")
                ),
                nextCursor = null,
                hasMore = false
            )

            coEvery {
                api.getActivityResponse(
                    cursor = null,
                    limit = 20
                )
            } returns response

            // When
            val result = mediator.load(
                LoadType.REFRESH,
                pagingState()
            )

            // Then
            assertTrue(
                result is RemoteMediator.MediatorResult.Success
            )

            val success =
                result as RemoteMediator.MediatorResult.Success

            assertTrue(success.endOfPaginationReached)
        }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `PREPEND returns end of pagination without calling API`() =
        runTest {
            // When
            val result = mediator.load(
                LoadType.PREPEND,
                pagingState()
            )

            // Then
            assertTrue(
                result is RemoteMediator.MediatorResult.Success
            )

            val success =
                result as RemoteMediator.MediatorResult.Success

            assertTrue(success.endOfPaginationReached)

            coVerify(exactly = 0) {
                api.getActivityResponse(
                    any(),
                    any()
                )
            }
        }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `APPEND returns end of pagination when there is no last item`() =
        runTest {
            // When
            val result = mediator.load(
                LoadType.APPEND,
                pagingState()
            )

            // Then
            assertTrue(
                result is RemoteMediator.MediatorResult.Success
            )

            val success =
                result as RemoteMediator.MediatorResult.Success

            assertTrue(success.endOfPaginationReached)

            coVerify(exactly = 0) {
                api.getActivityResponse(
                    any(),
                    any()
                )
            }
        }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `APPEND returns end of pagination when remote key does not exist`() =
        runTest {
            // Given
            val lastItem = activityEntity("last-item")

            val state = pagingState(
                items = listOf(lastItem)
            )

            // When
            val result = mediator.load(
                LoadType.APPEND,
                state
            )

            // Then
            assertTrue(
                result is RemoteMediator.MediatorResult.Success
            )

            val success =
                result as RemoteMediator.MediatorResult.Success

            assertTrue(success.endOfPaginationReached)

            coVerify(exactly = 0) {
                api.getActivityResponse(
                    any(),
                    any()
                )
            }
        }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `APPEND returns end of pagination when next cursor is null`() =
        runTest {
            // Given
            val lastItem = activityEntity("last-item")

            database.activityRemoteKeysDao().insertAll(
                listOf(
                    remoteKey(
                        activityId = "last-item",
                        nextCursor = null
                    )
                )
            )

            val state = pagingState(
                items = listOf(lastItem)
            )

            // When
            val result = mediator.load(
                LoadType.APPEND,
                state
            )

            // Then
            assertTrue(
                result is RemoteMediator.MediatorResult.Success
            )

            val success =
                result as RemoteMediator.MediatorResult.Success

            assertTrue(success.endOfPaginationReached)

            coVerify(exactly = 0) {
                api.getActivityResponse(
                    any(),
                    any()
                )
            }
        }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `APPEND loads next page using remote key cursor`() =
        runTest {
            // Given
            val lastItem = activityEntity("last-item")

            database.activityDao().insertAll(
                listOf(lastItem)
            )

            database.activityRemoteKeysDao().insertAll(
                listOf(
                    remoteKey(
                        activityId = "last-item",
                        nextCursor = "cursor-2"
                    )
                )
            )

            val response = activityResponse(
                items = listOf(
                    activityDto("next-1"),
                    activityDto("next-2")
                ),
                nextCursor = "cursor-3",
                hasMore = true
            )

            coEvery {
                api.getActivityResponse(
                    cursor = "cursor-2",
                    limit = 20
                )
            } returns response

            // When
            val result = mediator.load(
                LoadType.APPEND,
                pagingState(
                    items = listOf(lastItem)
                )
            )

            // Then
            assertTrue(
                result is RemoteMediator.MediatorResult.Success
            )

            val success =
                result as RemoteMediator.MediatorResult.Success

            assertFalse(success.endOfPaginationReached)

            assertEquals(
                3,
                database.activityDao().count()
            )

            assertEquals(
                "cursor-3",
                database.activityRemoteKeysDao()
                    .remoteKeys("next-1")
                    ?.nextCursor
            )

            assertEquals(
                "cursor-3",
                database.activityRemoteKeysDao()
                    .remoteKeys("next-2")
                    ?.nextCursor
            )

            coVerify(exactly = 1) {
                api.getActivityResponse(
                    cursor = "cursor-2",
                    limit = 20
                )
            }
        }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `APPEND returns end of pagination when API has no more pages`() =
        runTest {
            // Given
            val lastItem = activityEntity("last-item")

            database.activityDao().insertAll(
                listOf(lastItem)
            )

            database.activityRemoteKeysDao().insertAll(
                listOf(
                    remoteKey(
                        activityId = "last-item",
                        nextCursor = "cursor-2"
                    )
                )
            )

            val response = activityResponse(
                items = listOf(
                    activityDto("next-1")
                ),
                nextCursor = null,
                hasMore = false
            )

            coEvery {
                api.getActivityResponse(
                    cursor = "cursor-2",
                    limit = 20
                )
            } returns response

            // When
            val result = mediator.load(
                LoadType.APPEND,
                pagingState(
                    items = listOf(lastItem)
                )
            )

            // Then
            assertTrue(
                result is RemoteMediator.MediatorResult.Success
            )

            val success =
                result as RemoteMediator.MediatorResult.Success

            assertTrue(success.endOfPaginationReached)
        }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `APPEND with empty response does not insert anything`() =
        runTest {
            // Given
            val lastItem = activityEntity("last-item")

            database.activityDao().insertAll(
                listOf(lastItem)
            )

            database.activityRemoteKeysDao().insertAll(
                listOf(
                    remoteKey(
                        activityId = "last-item",
                        nextCursor = "cursor-2"
                    )
                )
            )

            val response = activityResponse(
                items = emptyList(),
                nextCursor = null,
                hasMore = false
            )

            coEvery {
                api.getActivityResponse(
                    cursor = "cursor-2",
                    limit = 20
                )
            } returns response

            // When
            val result = mediator.load(
                LoadType.APPEND,
                pagingState(
                    items = listOf(lastItem)
                )
            )

            // Then
            assertTrue(
                result is RemoteMediator.MediatorResult.Success
            )

            assertEquals(
                1,
                database.activityDao().count()
            )
        }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `REFRESH returns Error when API throws IOException`() =
        runTest {
            // Given
            val exception = IOException("Network error")

            coEvery {
                api.getActivityResponse(
                    cursor = null,
                    limit = 20
                )
            } throws exception

            // When
            val result = mediator.load(
                LoadType.REFRESH,
                pagingState()
            )

            // Then
            assertTrue(
                result is RemoteMediator.MediatorResult.Error
            )

            val error =
                result as RemoteMediator.MediatorResult.Error

            assertEquals(
                exception,
                error.throwable
            )
        }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `REFRESH returns Error when API throws unexpected exception`() =
        runTest {
            // Given
            val exception = IllegalStateException(
                "Unexpected error"
            )

            coEvery {
                api.getActivityResponse(
                    cursor = null,
                    limit = 20
                )
            } throws exception

            // When
            val result = mediator.load(
                LoadType.REFRESH,
                pagingState()
            )

            // Then
            assertTrue(
                result is RemoteMediator.MediatorResult.Error
            )

            val error =
                result as RemoteMediator.MediatorResult.Error

            assertEquals(
                exception,
                error.throwable
            )
        }

    private fun pagingState(
        items: List<ActivityEntity> = emptyList()
    ): PagingState<Int, ActivityEntity> {
        return PagingState(
            pages = listOf(
                PagingSource.LoadResult.Page(
                    data = items,
                    prevKey = null,
                    nextKey = null
                )
            ),
            anchorPosition = null,
            config = PagingConfig(
                pageSize = 20
            ),
            leadingPlaceholderCount = 0
        )
    }

    private fun activityEntity(
        id: String
    ) = ActivityEntity(
        id = id,
        type = "PAYOUT",
        amount = 100,
        currency = "GBP",
        date = "2024-01-15",
        description = "Test activity",
        status = "COMPLETED"
    )

    private fun activityDto(
        id: String
    ) = ActivityItemDto(
        id = id,
        type = "PAYOUT",
        amount = 100,
        currency = "GBP",
        date = "2024-01-15",
        description = "Test activity",
        status = "COMPLETED"
    )

    private fun remoteKey(
        activityId: String,
        nextCursor: String?
    ) = ActivityRemoteKeysEntity(
        activityId = activityId,
        nextCursor = nextCursor
    )

    private fun activityResponse(
        items: List<ActivityItemDto>,
        nextCursor: String?,
        hasMore: Boolean
    ) = ActivityResponseDto(
        items = items,
        nextCursor = nextCursor,
        hasMore = hasMore
    )
}