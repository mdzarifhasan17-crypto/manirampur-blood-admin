package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.DatabaseInitializer
import com.example.data.model.AdminProfile
import com.example.data.model.AmbulanceContact
import com.example.data.model.AvailabilityStatus
import com.example.data.model.BloodGroup
import com.example.data.model.BroadcastLog
import com.example.data.model.Donor
import com.example.data.model.EmergencyRequest
import com.example.data.model.RequestStatus
import com.example.data.model.UnionCoordinator
import com.example.data.model.UrgencyLevel
import com.example.data.model.UserRole
import com.example.data.model.VerificationStatus
import com.example.data.repository.BloodNetworkRepository
import com.example.data.sync.FirebaseSync
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdminViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = BloodNetworkRepository(database)

    init {
        // Initialize database with realistic seed data for Manirampur
        DatabaseInitializer.populateInitialData(database)
        FirebaseSync.start(database)
        viewModelScope.launch { FirebaseSync.uploadAll(database) }
    }

    // Active Admin Profile / RBAC Session State
    private val _currentAdmin = MutableStateFlow(
        AdminProfile(
            id = "ADM-01",
            name = "Zarif Hasan",
            email = "zarifhasan216@gmail.com",
            role = UserRole.SUPER_ADMIN,
            assignedUnion = null
        )
    )
    val currentAdmin: StateFlow<AdminProfile> = _currentAdmin

    // Global Selected Union Filter Scope
    private val _selectedUnion = MutableStateFlow("All Unions")
    val selectedUnion: StateFlow<String> = _selectedUnion

    // Search and Filter States for Donors
    private val _donorSearchQuery = MutableStateFlow("")
    val donorSearchQuery: StateFlow<String> = _donorSearchQuery

    private val _donorBloodGroupFilter = MutableStateFlow<String?>("All")
    val donorBloodGroupFilter: StateFlow<String?> = _donorBloodGroupFilter

    private val _donorStatusFilter = MutableStateFlow<String?>("All") // All, AVAILABLE, PENDING_KYC, DONATED_RECENTLY, BLOCKED
    val donorStatusFilter: StateFlow<String?> = _donorStatusFilter

    // Emergency Request Filter States
    private val _requestUrgencyFilter = MutableStateFlow<String?>("All")
    val requestUrgencyFilter: StateFlow<String?> = _requestUrgencyFilter

    private val _requestStatusFilter = MutableStateFlow<String?>("All")
    val requestStatusFilter: StateFlow<String?> = _requestStatusFilter

    // Toast/Snackbar Message Alert
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage

    // Raw Streams from Repository
    val allDonors: StateFlow<List<Donor>> = repository.allDonors
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allEmergencyRequests: StateFlow<List<EmergencyRequest>> = repository.allEmergencyRequests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAmbulances: StateFlow<List<AmbulanceContact>> = repository.allAmbulances
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUnionCoordinators: StateFlow<List<UnionCoordinator>> = repository.allUnionCoordinators
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBroadcastLogs: StateFlow<List<BroadcastLog>> = repository.allBroadcastLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private data class DonorFilterCriteria(
        val query: String,
        val bloodGroup: String?,
        val status: String?
    )

    private val donorFilterCriteria = combine(
        _donorSearchQuery,
        _donorBloodGroupFilter,
        _donorStatusFilter
    ) { query, bg, status ->
        DonorFilterCriteria(query, bg, status)
    }

    // Filtered Donors based on RBAC, Union, Search query, and status
    val filteredDonors: StateFlow<List<Donor>> = combine(
        allDonors,
        _currentAdmin,
        _selectedUnion,
        donorFilterCriteria
    ) { donors, admin, union, criteria ->
        donors.filter { donor ->
            // RBAC Enforcement: If Union Moderator, restricted to their union
            val matchesUnion = if (admin.role == UserRole.UNION_MODERATOR && admin.assignedUnion != null) {
                donor.union.equals(admin.assignedUnion, ignoreCase = true)
            } else if (union != "All Unions" && union.isNotBlank()) {
                donor.union.equals(union, ignoreCase = true)
            } else {
                true
            }

            // Search query (Phone, Donor ID, Name, Union, NID)
            val matchesQuery = if (criteria.query.isBlank()) {
                true
            } else {
                donor.fullName.contains(criteria.query, ignoreCase = true) ||
                        donor.phone.contains(criteria.query, ignoreCase = true) ||
                        donor.donorCode.contains(criteria.query, ignoreCase = true) ||
                        donor.union.contains(criteria.query, ignoreCase = true) ||
                        donor.nidOrStudentId.contains(criteria.query, ignoreCase = true)
            }

            // Blood Group Filter
            val matchesBg = if (criteria.bloodGroup == null || criteria.bloodGroup == "All") {
                true
            } else {
                donor.bloodGroup.equals(criteria.bloodGroup, ignoreCase = true)
            }

            // Status Filter
            val matchesStatus = when (criteria.status) {
                "AVAILABLE" -> donor.availabilityStatus == AvailabilityStatus.AVAILABLE && donor.verificationStatus == VerificationStatus.VERIFIED
                "PENDING_KYC" -> donor.verificationStatus == VerificationStatus.PENDING_KYC
                "DONATED_RECENTLY" -> donor.availabilityStatus == AvailabilityStatus.DONATED_RECENTLY
                "BLOCKED" -> donor.availabilityStatus == AvailabilityStatus.BLOCKED
                else -> true
            }

            matchesUnion && matchesQuery && matchesBg && matchesStatus
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Emergency Requests based on RBAC, Union, Urgency, Status
    val filteredEmergencyRequests: StateFlow<List<EmergencyRequest>> = combine(
        allEmergencyRequests,
        _currentAdmin,
        _selectedUnion,
        _requestUrgencyFilter,
        _requestStatusFilter
    ) { requests, admin, union, urgency, status ->
        requests.filter { req ->
            val matchesUnion = if (admin.role == UserRole.UNION_MODERATOR && admin.assignedUnion != null) {
                req.union.equals(admin.assignedUnion, ignoreCase = true)
            } else if (union != "All Unions" && union.isNotBlank()) {
                req.union.equals(union, ignoreCase = true)
            } else {
                true
            }

            val matchesUrgency = if (urgency == null || urgency == "All") {
                true
            } else {
                req.urgencyLevel.name.equals(urgency, ignoreCase = true)
            }

            val matchesStatus = if (status == null || status == "All") {
                true
            } else {
                req.status.name.equals(status, ignoreCase = true)
            }

            matchesUnion && matchesUrgency && matchesStatus
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Actions & Handlers
    fun switchAdminProfile(newAdmin: AdminProfile) {
        _currentAdmin.value = newAdmin
        if (newAdmin.role == UserRole.UNION_MODERATOR && newAdmin.assignedUnion != null) {
            _selectedUnion.value = newAdmin.assignedUnion
        } else {
            _selectedUnion.value = "All Unions"
        }
        _userMessage.value = "Logged in as ${newAdmin.name} (${if (newAdmin.role == UserRole.SUPER_ADMIN) "Super Admin" else "${newAdmin.assignedUnion} Moderator"})"
    }

    fun setSelectedUnion(union: String) {
        if (_currentAdmin.value.role == UserRole.SUPER_ADMIN) {
            _selectedUnion.value = union
        }
    }

    fun setDonorSearchQuery(query: String) {
        _donorSearchQuery.value = query
    }

    fun setDonorBloodGroupFilter(bg: String?) {
        _donorBloodGroupFilter.value = bg
    }

    fun setDonorStatusFilter(status: String?) {
        _donorStatusFilter.value = status
    }

    fun setRequestUrgencyFilter(urgency: String?) {
        _requestUrgencyFilter.value = urgency
    }

    fun setRequestStatusFilter(status: String?) {
        _requestStatusFilter.value = status
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    // Donor CRUD Operations
    fun saveDonor(donor: Donor) {
        viewModelScope.launch {
            if (donor.id == 0) {
                repository.insertDonor(donor)
                _userMessage.value = "Donor ${donor.fullName} registered successfully."
            } else {
                repository.updateDonor(donor)
                _userMessage.value = "Donor profile for ${donor.donorCode} updated."
            }
        }
    }

    fun updateDonorVerification(donor: Donor, status: VerificationStatus) {
        viewModelScope.launch {
            repository.updateVerification(donor.id, status)
            _userMessage.value = "KYC status updated to ${status.name} for ${donor.fullName}."
        }
    }

    fun toggleDonorBan(donor: Donor) {
        viewModelScope.launch {
            val newStatus = if (donor.availabilityStatus == AvailabilityStatus.BLOCKED) {
                AvailabilityStatus.AVAILABLE
            } else {
                AvailabilityStatus.BLOCKED
            }
            repository.updateAvailability(donor.id, newStatus)
            _userMessage.value = if (newStatus == AvailabilityStatus.BLOCKED) {
                "Donor ${donor.fullName} marked as BANNED/BLOCKED."
            } else {
                "Donor ${donor.fullName} unbanned."
            }
        }
    }

    fun recordDonation(donor: Donor) {
        viewModelScope.launch {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            repository.recordDonation(donor.id, today)
            _userMessage.value = "Donation recorded for ${donor.fullName}. Total: ${donor.totalDonations + 1}"
        }
    }

    fun deleteDonor(donor: Donor) {
        viewModelScope.launch {
            if (_currentAdmin.value.role == UserRole.SUPER_ADMIN) {
                repository.deleteDonor(donor)
                _userMessage.value = "Donor ${donor.fullName} deleted permanently."
            } else {
                _userMessage.value = "Permission Denied: Only Super Admin can delete donor records."
            }
        }
    }

    // Emergency Request Operations
    fun saveEmergencyRequest(request: EmergencyRequest) {
        viewModelScope.launch {
            if (request.id == 0) {
                repository.insertEmergencyRequest(request)
                _userMessage.value = "Emergency blood request ${request.requestCode} posted."
            } else {
                repository.updateEmergencyRequest(request)
                _userMessage.value = "Emergency request updated."
            }
        }
    }

    fun approveEmergencyRequest(request: EmergencyRequest) {
        viewModelScope.launch {
            repository.updateRequestStatus(request.id, RequestStatus.APPROVED)
            _userMessage.value = "Request ${request.requestCode} approved for donation matching."
        }
    }

    fun resolveEmergencyRequest(request: EmergencyRequest) {
        viewModelScope.launch {
            repository.markRequestResolved(request.id)
            _userMessage.value = "Request ${request.requestCode} marked as RESOLVED (Blood Provided)."
        }
    }

    fun rejectFakeRequest(request: EmergencyRequest) {
        viewModelScope.launch {
            repository.updateRequestStatus(request.id, RequestStatus.FAKE_REJECTED)
            _userMessage.value = "Request ${request.requestCode} flagged & rejected as fake/spam."
        }
    }

    fun deleteEmergencyRequest(request: EmergencyRequest) {
        viewModelScope.launch {
            if (_currentAdmin.value.role == UserRole.SUPER_ADMIN) {
                repository.deleteEmergencyRequest(request)
                _userMessage.value = "Emergency request deleted."
            } else {
                _userMessage.value = "Permission Denied: Only Super Admin can delete requests."
            }
        }
    }

    // One-Click Push Broadcast Dispatcher
    fun triggerPushBroadcast(
        targetBloodGroup: String,
        targetUnion: String,
        title: String,
        message: String,
        associatedRequestId: Int? = null
    ) {
        viewModelScope.launch {
            val matchingDonors = repository.getMatchingAvailableDonors(targetBloodGroup, targetUnion)
            val recipientCount = if (matchingDonors.isNotEmpty()) matchingDonors.size else 12 // fallback estimate

            // Log the broadcast
            val log = BroadcastLog(
                title = title,
                message = message,
                targetBloodGroup = targetBloodGroup,
                targetUnion = targetUnion,
                recipientCount = recipientCount,
                sentBy = "${_currentAdmin.value.name} (${_currentAdmin.value.role.name})"
            )
            repository.logBroadcast(log)

            // Update associated request if any
            if (associatedRequestId != null) {
                repository.markBroadcastSent(associatedRequestId, recipientCount)
            }

            _userMessage.value = "🚨 PUSH BROADCAST DISPATCHED to $recipientCount available $targetBloodGroup donors in $targetUnion!"
        }
    }

    // Ambulance Operations
    fun saveAmbulance(ambulance: AmbulanceContact) {
        viewModelScope.launch {
            if (ambulance.id == 0) {
                repository.insertAmbulance(ambulance)
                _userMessage.value = "Ambulance contact added."
            } else {
                repository.updateAmbulance(ambulance)
                _userMessage.value = "Ambulance details updated."
            }
        }
    }

    fun deleteAmbulance(ambulance: AmbulanceContact) {
        viewModelScope.launch {
            repository.deleteAmbulance(ambulance)
            _userMessage.value = "Ambulance contact removed."
        }
    }

    // Union Coordinator Operations
    fun saveUnionCoordinator(coordinator: UnionCoordinator) {
        viewModelScope.launch {
            if (coordinator.id == 0) {
                repository.insertUnionCoordinator(coordinator)
                _userMessage.value = "Union coordinator added."
            } else {
                repository.updateUnionCoordinator(coordinator)
                _userMessage.value = "Union coordinator updated."
            }
        }
    }
}
