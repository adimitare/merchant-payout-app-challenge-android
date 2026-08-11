package com.example.androidinterview.data.local.dao

import android.content.Context
import androidx.paging.PagingSource
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.androidinterview.data.local.AppDatabase
import com.example.androidinterview.data.local.entity.ActivityEntity
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ActivityDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var activityDao: ActivityDao

    @Before
    fun setup() {
        val context =
            ApplicationProvider.getApplicationContext<Context>()

        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        activityDao = database.activityDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `insertAll inserts activities`() = runTest {
        val activities = listOf(
            activity(id = "1"),
            activity(id = "2")
        )

        activityDao.insertAll(activities)

        assertEquals(
            2,
            activityDao.count()
        )
    }

    @Test
    fun `insertAll replaces activity with same id`() = runTest {
        val original = activity(
            id = "1",
            description = "Original"
        )

        val replacement = activity(
            id = "1",
            description = "Replacement"
        )

        activityDao.insertAll(listOf(original))
        activityDao.insertAll(listOf(replacement))

        assertEquals(
            1,
            activityDao.count()
        )

        val result = activityDao
            .pagingSource()
            .load(
                PagingSource.LoadParams.Refresh(
                    key = null,
                    loadSize = 10,
                    placeholdersEnabled = false
                )
            )

        assertTrue(result is PagingSource.LoadResult.Page)

        val page = result as PagingSource.LoadResult.Page

        assertEquals(
            "Replacement",
            page.data.first().description
        )
    }

    @Test
    fun `clearAll removes all activities`() = runTest {
        activityDao.insertAll(
            listOf(
                activity(id = "1"),
                activity(id = "2")
            )
        )

        activityDao.clearAll()

        assertEquals(
            0,
            activityDao.count()
        )
    }

    @Test
    fun `count returns number of activities`() = runTest {
        activityDao.insertAll(
            listOf(
                activity(id = "1"),
                activity(id = "2"),
                activity(id = "3")
            )
        )

        assertEquals(
            3,
            activityDao.count()
        )
    }

    @Test
    fun `pagingSource returns activities ordered by date descending`() =
        runTest {

            activityDao.insertAll(
                listOf(
                    activity(
                        id = "1",
                        date = "2024-01-01"
                    ),
                    activity(
                        id = "2",
                        date = "2024-03-01"
                    ),
                    activity(
                        id = "3",
                        date = "2024-02-01"
                    )
                )
            )

            val result = activityDao
                .pagingSource()
                .load(
                    PagingSource.LoadParams.Refresh(
                        key = null,
                        loadSize = 10,
                        placeholdersEnabled = false
                    )
                )

            assertTrue(result is PagingSource.LoadResult.Page)

            val page = result as PagingSource.LoadResult.Page

            assertEquals(
                listOf("2", "3", "1"),
                page.data.map { it.id }
            )
        }

    private fun activity(
        id: String,
        date: String = "2024-01-01",
        description: String = "Test activity"
    ): ActivityEntity {
        return ActivityEntity(
            id = id,
            type = "PAYMENT",
            amount = 100,
            currency = "GBP",
            date = date,
            description = description,
            status = "COMPLETED"
        )
    }
}