package com.josh25.vorsprungone.data.datasource

/*
val missionNumber = 0
class MissionPlannerDataSource {
    fun getMission(): String {
        //return predefinedMissions.random()
        return predefinedMissions.get(missionNumber)
    }
    fun getNewMission(): String {
        //return predefinedMissions.random()
        missionNumber = random(1..predefinedMissions.size)
        return predefinedMissions.get(missionNumber)
    }
}
 */
import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import androidx.datastore.preferences.core.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.random.Random
import kotlin.random.nextInt

//class MissionPlannerDataSource(private val context: Context) {
class MissionPlannerDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun getMission(): String {
        val missionNumber = runBlocking {
            context.dataStore.data.map { prefs ->
                prefs[MISSION_NUMBER_KEY] ?: 0
            }.first()
        }
        Log.e("joshtag", "getMission: ${predefinedMissions[missionNumber]}, index: $missionNumber")
        return predefinedMissions[missionNumber]
    }

    fun getNewMission(): String {
        val newIndex = Random.nextInt(predefinedMissions.indices)

        runBlocking {
            context.dataStore.edit { prefs ->
                prefs[MISSION_NUMBER_KEY] = newIndex
            }
        }
        Log.e("joshtag", "getNewMission: ${predefinedMissions[newIndex]}, index: $newIndex")
        return predefinedMissions[newIndex]
    }
}



/*
0: Downstairs 8x8
1: Horizontal Spiral 6x3
2: J
3: T
4: INWARD SPIRAL 7x7
5: Corridor In And Out
6: Into the Sunset
7: SNAKE 12X4
 */