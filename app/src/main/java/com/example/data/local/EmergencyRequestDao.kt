package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.EmergencyRequest
import com.example.data.model.RequestStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface EmergencyRequestDao {
    @Query("SELECT * FROM emergency_requests ORDER BY createdTimestamp DESC")
    suspend fun getAllRequestsOnce(): List<EmergencyRequest>
    fun getAllRequests(): Flow<List<EmergencyRequest>>

    @Query("SELECT * FROM emergency_requests WHERE `union` = :union ORDER BY createdTimestamp DESC")
    fun getRequestsByUnion(union: String): Flow<List<EmergencyRequest>>

    @Query("DELETE FROM emergency_requests")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: EmergencyRequest): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequests(requests: List<EmergencyRequest>)

    @Update
    suspend fun updateRequest(request: EmergencyRequest)

    @Delete
    suspend fun deleteRequest(request: EmergencyRequest)

    @Query("DELETE FROM emergency_requests WHERE id = :id")
    suspend fun deleteRequestById(id: Int)

    @Query("UPDATE emergency_requests SET status = :status WHERE id = :id")
    suspend fun updateRequestStatus(id: Int, status: RequestStatus)

    @Query("UPDATE emergency_requests SET status = 'RESOLVED', resolvedTimestamp = :timestamp WHERE id = :id")
    suspend fun markRequestResolved(id: Int, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE emergency_requests SET status = 'BROADCAST_SENT', broadcastRecipientsCount = :recipientCount WHERE id = :id")
    suspend fun markBroadcastSent(id: Int, recipientCount: Int)
}
