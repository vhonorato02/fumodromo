package com.fumodromo

import android.content.Context
import androidx.room.Room
import com.fumodromo.data.datastore.SettingsDataStore
import com.fumodromo.data.local.AppDatabase
import com.fumodromo.repository.FumoRepository

class AppContainer(context: Context) {
    private val db = Room.databaseBuilder(context, AppDatabase::class.java, "fumodromo.db").build()
    private val settings = SettingsDataStore(context)

    val repository = FumoRepository(db.smokeLogDao(), settings)
}
