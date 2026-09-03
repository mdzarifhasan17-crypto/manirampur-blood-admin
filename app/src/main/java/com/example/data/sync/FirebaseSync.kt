package com.example.data.sync

import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await

object FirebaseSync {
    private val db by lazy { FirebaseFirestore.getInstance() }
    private var donorsListener: ListenerRegistration? = null
    private var requestsListener: ListenerRegistration? = null

    fun start(database: AppDatabase) {
        donorsListener?.remove(); requestsListener?.remove()
        donorsListener = db.collection("donors").addSnapshotListener { snap, err ->
            if (err != null || snap == null) return@addSnapshotListener
            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                val items = snap.documents.mapNotNull { d ->
                    try { donorFrom(d.data ?: emptyMap()) } catch (_: Exception) { null }
                }
                if (items.isNotEmpty()) { database.donorDao().deleteAll(); database.donorDao().insertDonors(items) }
            }
        }
        requestsListener = db.collection("blood_requests").addSnapshotListener { snap, err ->
            if (err != null || snap == null) return@addSnapshotListener
            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                val items = snap.documents.mapNotNull { d -> try { requestFrom(d.data ?: emptyMap()) } catch (_: Exception) { null } }
                if (items.isNotEmpty()) { database.emergencyRequestDao().deleteAll(); database.emergencyRequestDao().insertRequests(items) }
            }
        }
    }

    suspend fun uploadDonor(d: Donor) {
        db.collection("donors").document(d.donorCode).set(mapOf(
            "donorCode" to d.donorCode, "fullName" to d.fullName, "phone" to d.phone,
            "bloodGroup" to d.bloodGroup, "union" to d.union, "villageAddress" to d.villageAddress,
            "nidOrStudentId" to d.nidOrStudentId, "verificationStatus" to d.verificationStatus.name,
            "availabilityStatus" to d.availabilityStatus.name, "lastDonationDate" to d.lastDonationDate,
            "totalDonations" to d.totalDonations, "age" to d.age, "gender" to d.gender,
            "emergencyContact" to d.emergencyContact, "registeredTimestamp" to d.registeredTimestamp,
            "notes" to d.notes
        )).await()
    }
    suspend fun deleteDonor(code: String) { db.collection("donors").document(code).delete().await() }
    suspend fun deleteRequest(code: String) { db.collection("blood_requests").document(code).delete().await() }
    suspend fun uploadRequest(r: EmergencyRequest) {
        db.collection("blood_requests").document(r.requestCode).set(mapOf(
            "requestCode" to r.requestCode, "patientName" to r.patientName, "bloodGroup" to r.bloodGroup,
            "bagsNeeded" to r.bagsNeeded, "hospitalLocation" to r.hospitalLocation, "union" to r.union,
            "contactPhone" to r.contactPhone, "alternatePhone" to r.alternatePhone,
            "urgencyLevel" to r.urgencyLevel.name, "status" to r.status.name,
            "reasonOrDiagnosis" to r.reasonOrDiagnosis, "createdTimestamp" to r.createdTimestamp,
            "resolvedTimestamp" to r.resolvedTimestamp, "broadcastRecipientsCount" to r.broadcastRecipientsCount
        )).await()
    }
    suspend fun uploadAll(database: AppDatabase) {
        database.donorDao().getAllDonorsOnce().forEach { uploadDonor(it) }
        database.emergencyRequestDao().getAllRequestsOnce().forEach { uploadRequest(it) }
    }
    private fun donorFrom(m: Map<String, Any?>) = Donor(
        donorCode=m["donorCode"] as? String ?: return null!!, fullName=m["fullName"] as? String ?: "",
        phone=m["phone"] as? String ?: "", bloodGroup=m["bloodGroup"] as? String ?: "",
        union=m["union"] as? String ?: "", villageAddress=m["villageAddress"] as? String ?: "",
        nidOrStudentId=m["nidOrStudentId"] as? String ?: "",
        verificationStatus=runCatching { VerificationStatus.valueOf(m["verificationStatus"] as? String ?: "PENDING_KYC") }.getOrDefault(VerificationStatus.PENDING_KYC),
        availabilityStatus=runCatching { AvailabilityStatus.valueOf(m["availabilityStatus"] as? String ?: "AVAILABLE") }.getOrDefault(AvailabilityStatus.AVAILABLE),
        lastDonationDate=m["lastDonationDate"] as? String ?: "", totalDonations=(m["totalDonations"] as? Number)?.toInt() ?: 0,
        age=(m["age"] as? Number)?.toInt() ?: 24, gender=m["gender"] as? String ?: "Male",
        emergencyContact=m["emergencyContact"] as? String ?: "", registeredTimestamp=(m["registeredTimestamp"] as? Number)?.toLong() ?: System.currentTimeMillis(), notes=m["notes"] as? String ?: "")
    private fun requestFrom(m: Map<String, Any?>) = EmergencyRequest(
        requestCode=m["requestCode"] as? String ?: return null!!, patientName=m["patientName"] as? String ?: "",
        bloodGroup=m["bloodGroup"] as? String ?: "", bagsNeeded=(m["bagsNeeded"] as? Number)?.toInt() ?: 1,
        hospitalLocation=m["hospitalLocation"] as? String ?: "", union=m["union"] as? String ?: "",
        contactPhone=m["contactPhone"] as? String ?: "", alternatePhone=m["alternatePhone"] as? String ?: "",
        urgencyLevel=runCatching { UrgencyLevel.valueOf(m["urgencyLevel"] as? String ?: "CRITICAL") }.getOrDefault(UrgencyLevel.CRITICAL),
        status=runCatching { RequestStatus.valueOf(m["status"] as? String ?: "PENDING") }.getOrDefault(RequestStatus.PENDING),
        reasonOrDiagnosis=m["reasonOrDiagnosis"] as? String ?: "", createdTimestamp=(m["createdTimestamp"] as? Number)?.toLong() ?: System.currentTimeMillis(),
        resolvedTimestamp=(m["resolvedTimestamp"] as? Number)?.toLong(), broadcastRecipientsCount=(m["broadcastRecipientsCount"] as? Number)?.toInt() ?: 0)
}
