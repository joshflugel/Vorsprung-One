package com.josh25.vorsprungone.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.josh25.vorsprungone.data.datasource.MISSION_NUMBER_KEY
import com.josh25.vorsprungone.data.datasource.MissionPlannerDataSource
import com.josh25.vorsprungone.data.datasource.dataStore
import com.josh25.vorsprungone.data.datasource.predefinedMissions
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MissionPlannerDataSourceTest {

    private lateinit var context: Context
    private lateinit var dataSource: MissionPlannerDataSource

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        dataSource = MissionPlannerDataSource(context)
    }

    @Test
    fun testGetMissionReturnsCurrentIndex() = runBlocking {
        // Manually set a known index
        val prefs = context.dataStore
        val missionIndex = 2
        prefs.edit { it[MISSION_NUMBER_KEY] = missionIndex }

        val mission = dataSource.getMission()
        assertEquals(predefinedMissions[missionIndex], mission)
    }

    @Test
    fun testGetNewMissionUpdatesIndex() = runBlocking {
        val oldIndex = context.dataStore.data.first()[MISSION_NUMBER_KEY] ?: -1
        val newMission = dataSource.getNewMission()
        val newIndex = context.dataStore.data.first()[MISSION_NUMBER_KEY]

        // Should not be the same index as before
        if (predefinedMissions.size > 1) {
            assert(newIndex != oldIndex) {
                "Expected different index but got same: $newIndex"
            }
        }

        assertEquals(predefinedMissions[newIndex!!], newMission)
    }
}
