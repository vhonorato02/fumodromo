package com.fumodromo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [SmokeLogEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun smokeLogDao(): SmokeLogDao
}
