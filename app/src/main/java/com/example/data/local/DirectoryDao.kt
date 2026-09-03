package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AmbulanceContact
import com.example.data.model.BroadcastLog
import com.example.data.model.UnionCoordinator
import kotlinx.coroutines.flow.Flow

@Dao
interface DirectoryDao {
    // Ambulance Contacts
    @Query("SELECT * FROM ambulance_contacts ORDER BY id ASC")
    fun getAllAmbulances(): Flow<List<AmbulanceContact>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAmbulance(ambulance: AmbulanceContact): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAmbulances(ambulances: List<AmbulanceContact>)

    @Update
    suspend fun updateAmbulance(ambulance: AmbulanceContact)

    @Delete
    suspend fun deleteAmbulance(ambulance: AmbulanceContact)

    @Query("DELETE FROM ambulance_contacts WHERE id = :id")
    suspend fun deleteAmbulanceById(id: Int)

    // Union Coordinators
    @Query("SELECT * FROM union_coordinators ORDER BY unionName ASC")
    fun getAllUnionCoordinators(): Flow<List<UnionCoordinator>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUnionCoordinator(coordinator: UnionCoordinator): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUnionCoordinators(coordinators: List<UnionCoordinator>)

    @Update
    suspend fun updateUnionCoordinator(coordinator: UnionCoordinator)

    @Delete
    suspend fun deleteUnionCoordinator(coordinator: UnionCoordinator)

    // Broadcast Logs
    @Query("SELECT * FROM broadcast_logs ORDER BY timestamp DESC")
    fun getAllBroadcastLogs(): Flow<List<BroadcastLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBroadcastLog(log: BroadcastLog): Long
}
