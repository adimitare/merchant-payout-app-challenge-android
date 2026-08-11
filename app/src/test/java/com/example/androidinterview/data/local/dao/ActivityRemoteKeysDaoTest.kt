package com.example.androidinterview.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.androidinterview.data.local.AppDatabase
import com.example.androidinterview.data.local.entity.ActivityRemoteKeysEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ActivityRemoteKeysDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: ActivityRemoteKeysDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        dao = database.activityRemoteKeysDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `insertAll inserts remote keys`() = runTest {
        val keys = listOf(
            remoteKey(
                activityId = "activity-1",
                nextCursor = "cursor-1"
            )
        )
        dao.insertAll(keys)
        assertEquals(
            keys.first(),
            dao.remoteKeys("activity-1")
        )
    }

    @Test
    fun `remoteKeys returns null when activity does not exist`() = runTest {
        val result = dao.remoteKeys("unknown-id")
        assertNull(result)
    }

    @Test
    fun `remoteKeys returns correct key for activity`() = runTest {
        dao.insertAll(
            listOf(
                remoteKey(
                    activityId = "activity-1",
                    nextCursor = "cursor-1"
                ),
                remoteKey(
                    activityId = "activity-2",
                    nextCursor = "cursor-2"
                )
            )
        )
        val result = dao.remoteKeys("activity-2")
        assertEquals(
            ActivityRemoteKeysEntity(
                activityId = "activity-2",
                nextCursor = "cursor-2"
            ),
            result
        )
    }

    @Test
    fun `insertAll replaces remote key with same activity id`() = runTest {
        dao.insertAll(
            listOf(
                remoteKey(
                    activityId = "activity-1",
                    nextCursor = "cursor-1"
                )
            )
        )
        dao.insertAll(
            listOf(
                remoteKey(
                    activityId = "activity-1",
                    nextCursor = "cursor-2"
                )
            )
        )
        assertEquals(
            ActivityRemoteKeysEntity(
                activityId = "activity-1",
                nextCursor = "cursor-2"
            ),
            dao.remoteKeys("activity-1")
        )
    }

    @Test
    fun `clearRemoteKeys removes all remote keys`() = runTest {
        dao.insertAll(
            listOf(
                remoteKey(
                    activityId = "activity-1",
                    nextCursor = "cursor-1"
                ),
                remoteKey(
                    activityId = "activity-2",
                    nextCursor = "cursor-2"
                )
            )
        )
        dao.clearRemoteKeys()
        assertNull(dao.remoteKeys("activity-1"))
        assertNull(dao.remoteKeys("activity-2"))
    }

    private fun remoteKey(
        activityId: String,
        nextCursor: String?
    ) = ActivityRemoteKeysEntity(
        activityId = activityId,
        nextCursor = nextCursor
    )
}