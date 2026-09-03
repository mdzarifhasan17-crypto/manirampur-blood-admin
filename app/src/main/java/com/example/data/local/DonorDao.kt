package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AvailabilityStatus
import com.example.data.model.Donor
import com.example.data.model.VerificationStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface DonorDao {
    @Query("SELECT * FROM donors ORDER BY id DESC")
    suspend fun getAllDonorsOnce(): List<Donor>
    fun getAllDonors(): Flow<List<Donor>>

    @Query("SELECT * FROM donors WHERE `union` = :union ORDER BY id DESC")
    fun getDonorsByUnion(union: String): Flow<List<Donor>>

    @Query("SELECT * FROM donors WHERE id = :id")
    suspend fun getDonorById(id: Int): Donor?

    @Query("SELECT * FROM donors WHERE donorCode = :code LIMIT 1")
    suspend fun getDonorByCode(code: String): Donor?

    @Query("SELECT * FROM donors WHERE bloodGroup = :bloodGroup AND availabilityStatus = 'AVAILABLE' AND verificationStatus = 'VERIFIED'")
    suspend fun getAvailableDonorsByBloodGroup(bloodGroup: String): List<Donor>

    @Query("SELECT * FROM donors WHERE bloodGroup = :bloodGroup AND `union` = :union AND availabilityStatus = 'AVAILABLE' AND verificationStatus = 'VERIFIED'")
    suspend fun getAvailableDonorsByBloodGroupAndUnion(bloodGroup: String, union: String): List<Donor>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDonor(donor: Donor): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDonors(donors: List<Donor>)

    @Update
    suspend fun updateDonor(donor: Donor)

    @Delete
    suspend fun deleteDonor(donor: Donor)

    @Query("DELETE FROM donors WHERE id = :id")
    suspend fun deleteDonorById(id: Int)

    @Query("UPDATE donors SET verificationStatus = :status WHERE id = :id")
    suspend fun updateVerificationStatus(id: Int, status: VerificationStatus)

    @Query("UPDATE donors SET availabilityStatus = :status WHERE id = :id")
    suspend fun updateAvailabilityStatus(id: Int, status: AvailabilityStatus)

    @Query("UPDATE donors SET totalDonations = totalDonations + 1, lastDonationDate = :date, availabilityStatus = 'DONATED_RECENTLY' WHERE id = :id")
    suspend fun recordDonation(id: Int, date: String)

    @Query("DELETE FROM donors")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM donors")
    fun getDonorsCount(): Flow<Int>
}
