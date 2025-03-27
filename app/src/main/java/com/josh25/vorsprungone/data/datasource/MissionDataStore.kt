package com.josh25.vorsprungone.data.datasource

import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore


val Context.dataStore by preferencesDataStore(name = "mission_store")
val MISSION_NUMBER_KEY = intPreferencesKey("mission_number")