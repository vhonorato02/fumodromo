package com.fumodromo.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SmokeLogDao {
    @Insert
    suspend fun insert(item: SmokeLogEntity): Long

    @Delete
    suspend fun delete(item: SmokeLogEntity)

    @Query("DELETE FROM smoke_logs WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM smoke_logs")
    suspend fun deleteAll()

    @Query("SELECT * FROM smoke_logs ORDER BY timestampMillis DESC")
    fun observeAll(): Flow<List<SmokeLogEntity>>

    @Query("SELECT * FROM smoke_logs ORDER BY timestampMillis DESC LIMIT 1")
    fun observeLast(): Flow<SmokeLogEntity?>
}
