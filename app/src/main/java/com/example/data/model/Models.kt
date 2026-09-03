package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class BloodGroup(val display: String) {
    A_POSITIVE("A+"),
    A_NEGATIVE("A-"),
    B_POSITIVE("B+"),
    B_NEGATIVE("B-"),
    AB_POSITIVE("AB+"),
    AB_NEGATIVE("AB-"),
    O_POSITIVE("O+"),
    O_NEGATIVE("O-");

    companion object {
        fun fromString(value: String): BloodGroup {
            return entries.find { it.display.equals(value, ignoreCase = true) || it.name.equals(value, ignoreCase = true) } ?: O_POSITIVE
        }
    }
}

enum class VerificationStatus {
    VERIFIED,
    PENDING_KYC,
    REJECTED
}

enum class AvailabilityStatus {
    AVAILABLE,
    DONATED_RECENTLY,
    INELIGIBLE,
    BLOCKED
}

enum class UrgencyLevel {
    CRITICAL,
    URGENT,
    NORMAL
}

enum class RequestStatus {
    PENDING,
    APPROVED,
    BROADCAST_SENT,
    RESOLVED,
    CANCELLED,
    FAKE_REJECTED
}

enum class UserRole {
    SUPER_ADMIN,
    UNION_MODERATOR
}

@Entity(tableName = "donors")
data class Donor(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val donorCode: String, // e.g. #MNR-BLOOD-1042
    val fullName: String,
    val phone: String,
    val bloodGroup: String, // A+, B+, etc.
    val union: String, // Bhojna, Rohita, Nehalpur, etc.
    val villageAddress: String,
    val nidOrStudentId: String,
    val verificationStatus: VerificationStatus = VerificationStatus.PENDING_KYC,
    val availabilityStatus: AvailabilityStatus = AvailabilityStatus.AVAILABLE,
    val lastDonationDate: String = "", // YYYY-MM-DD
    val totalDonations: Int = 0,
    val age: Int = 24,
    val gender: String = "Male",
    val emergencyContact: String = "",
    val registeredTimestamp: Long = System.currentTimeMillis(),
    val notes: String = ""
)

@Entity(tableName = "emergency_requests")
data class EmergencyRequest(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val requestCode: String, // e.g. #EMR-2024-089
    val patientName: String,
    val bloodGroup: String,
    val bagsNeeded: Int = 1,
    val hospitalLocation: String, // e.g. Manirampur Upazila Health Complex
    val union: String,
    val contactPhone: String,
    val alternatePhone: String = "",
    val urgencyLevel: UrgencyLevel = UrgencyLevel.CRITICAL,
    val status: RequestStatus = RequestStatus.PENDING,
    val reasonOrDiagnosis: String = "Accident & Emergency Trauma Care",
    val createdTimestamp: Long = System.currentTimeMillis(),
    val resolvedTimestamp: Long? = null,
    val broadcastRecipientsCount: Int = 0
)

@Entity(tableName = "ambulance_contacts")
data class AmbulanceContact(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val serviceName: String,
    val operatorName: String,
    val phone: String,
    val alternatePhone: String = "",
    val union: String,
    val ambulanceType: String, // ICU Ambulance, AC Ambulance, Standard
    val isAvailable24x7: Boolean = true,
    val baseLocation: String
)

@Entity(tableName = "union_coordinators")
data class UnionCoordinator(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val unionName: String,
    val leadCoordinatorName: String,
    val phone: String,
    val activeVolunteersCount: Int,
    val assignedModeratorEmail: String,
    val emergencyHelpline: String
)

@Entity(tableName = "broadcast_logs")
data class BroadcastLog(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val message: String,
    val targetBloodGroup: String,
    val targetUnion: String,
    val recipientCount: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val sentBy: String = "Super Admin (Zarif)"
)

data class AdminProfile(
    val id: String,
    val name: String,
    val email: String,
    val role: UserRole,
    val assignedUnion: String? = null, // null for Super Admin
    val token: String = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.mbn_admin_session_token_sec782",
    val loginTime: Long = System.currentTimeMillis()
)

val MANIRAMPUR_UNIONS = listOf(
    "All Unions",
    "Bhojna",
    "Rohita",
    "Nehalpur",
    "Chhagalchhara",
    "Haridaspur",
    "Jhanpa",
    "Kashimpur",
    "Khanpur",
    "Kultia",
    "Manirampur Sadar",
    "Mashwimnagar",
    "Monoharpur",
    "Shyamkur",
    "Durbachhanga",
    "Khedapara"
)
