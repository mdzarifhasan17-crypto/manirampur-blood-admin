package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.model.AmbulanceContact
import com.example.data.model.AvailabilityStatus
import com.example.data.model.BroadcastLog
import com.example.data.model.Donor
import com.example.data.model.EmergencyRequest
import com.example.data.model.RequestStatus
import com.example.data.model.UnionCoordinator
import com.example.data.model.VerificationStatus
import kotlinx.coroutines.flow.Flow
import com.example.data.sync.FirebaseSync

class BloodNetworkRepository(private val database: AppDatabase) {
    private val donorDao = database.donorDao()
    private val emergencyDao = database.emergencyRequestDao()
    private val directoryDao = database.directoryDao()

    // Donors
    val allDonors: Flow<List<Donor>> = donorDao.getAllDonors()

    fun getDonorsForUnion(union: String): Flow<List<Donor>> =
        if (union.equals("All Unions", ignoreCase = true) || union.isBlank()) {
            donorDao.getAllDonors()
        } else {
            donorDao.getDonorsByUnion(union)
        }

    suspend fun insertDonor(donor: Donor): Long { val id = donorDao.insertDonor(donor); FirebaseSync.uploadDonor(donor); return id }
    suspend fun updateDonor(donor: Donor) { donorDao.updateDonor(donor); FirebaseSync.uploadDonor(donor) }
    suspend fun deleteDonor(donor: Donor) { donorDao.deleteDonor(donor); FirebaseSync.deleteDonor(donor.donorCode) }
    suspend fun deleteDonorById(id: Int) = donorDao.deleteDonorById(id)
    suspend fun updateVerification(id: Int, status: VerificationStatus) { donorDao.updateVerificationStatus(id, status); donorDao.getDonorById(id)?.let { FirebaseSync.uploadDonor(it) } }
    suspend fun updateAvailability(id: Int, status: AvailabilityStatus) { donorDao.updateAvailabilityStatus(id, status); donorDao.getDonorById(id)?.let { FirebaseSync.uploadDonor(it) } }
    suspend fun recordDonation(id: Int, date: String) { donorDao.recordDonation(id, date); donorDao.getDonorById(id)?.let { FirebaseSync.uploadDonor(it) } }

    suspend fun getMatchingAvailableDonors(bloodGroup: String, union: String?): List<Donor> {
        return if (union == null || union.equals("All Unions", ignoreCase = true) || union.isBlank()) {
            donorDao.getAvailableDonorsByBloodGroup(bloodGroup)
        } else {
            donorDao.getAvailableDonorsByBloodGroupAndUnion(bloodGroup, union)
        }
    }

    // Emergency Requests
    val allEmergencyRequests: Flow<List<EmergencyRequest>> = emergencyDao.getAllRequests()

    fun getEmergencyRequestsForUnion(union: String): Flow<List<EmergencyRequest>> =
        if (union.equals("All Unions", ignoreCase = true) || union.isBlank()) {
            emergencyDao.getAllRequests()
        } else {
            emergencyDao.getRequestsByUnion(union)
        }

    suspend fun insertEmergencyRequest(request: EmergencyRequest): Long { val id = emergencyDao.insertRequest(request); FirebaseSync.uploadRequest(request); return id }
    suspend fun updateEmergencyRequest(request: EmergencyRequest) { emergencyDao.updateRequest(request); FirebaseSync.uploadRequest(request) }
    suspend fun deleteEmergencyRequest(request: EmergencyRequest) { emergencyDao.deleteRequest(request); FirebaseSync.deleteRequest(request.requestCode) }
    suspend fun updateRequestStatus(id: Int, status: RequestStatus) { emergencyDao.updateRequestStatus(id, status); emergencyDao.getAllRequestsOnce().firstOrNull { it.id == id }?.let { FirebaseSync.uploadRequest(it.copy(status = status)) } }
    suspend fun markRequestResolved(id: Int) { emergencyDao.markRequestResolved(id); emergencyDao.getAllRequestsOnce().firstOrNull { it.id == id }?.let { FirebaseSync.uploadRequest(it.copy(status = RequestStatus.RESOLVED, resolvedTimestamp = System.currentTimeMillis())) } }
    suspend fun markBroadcastSent(id: Int, recipientCount: Int) { emergencyDao.markBroadcastSent(id, recipientCount); emergencyDao.getAllRequestsOnce().firstOrNull { it.id == id }?.let { FirebaseSync.uploadRequest(it.copy(status = RequestStatus.BROADCAST_SENT, broadcastRecipientsCount = recipientCount)) } }

    // Directory
    val allAmbulances: Flow<List<AmbulanceContact>> = directoryDao.getAllAmbulances()
    suspend fun insertAmbulance(ambulance: AmbulanceContact): Long =
        directoryDao.insertAmbulance(ambulance)
    suspend fun updateAmbulance(ambulance: AmbulanceContact) =
        directoryDao.updateAmbulance(ambulance)
    suspend fun deleteAmbulance(ambulance: AmbulanceContact) =
        directoryDao.deleteAmbulance(ambulance)

    val allUnionCoordinators: Flow<List<UnionCoordinator>> =
        directoryDao.getAllUnionCoordinators()
    suspend fun insertUnionCoordinator(coordinator: UnionCoordinator): Long =
        directoryDao.insertUnionCoordinator(coordinator)
    suspend fun updateUnionCoordinator(coordinator: UnionCoordinator) =
        directoryDao.updateUnionCoordinator(coordinator)
    suspend fun deleteUnionCoordinator(coordinator: UnionCoordinator) =
        directoryDao.deleteUnionCoordinator(coordinator)

    // Broadcast Logs
    val allBroadcastLogs: Flow<List<BroadcastLog>> = directoryDao.getAllBroadcastLogs()
    suspend fun logBroadcast(log: BroadcastLog): Long =
        directoryDao.insertBroadcastLog(log)
}
