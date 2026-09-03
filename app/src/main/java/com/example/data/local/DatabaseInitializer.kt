package com.example.data.local

import com.example.data.model.AmbulanceContact
import com.example.data.model.AvailabilityStatus
import com.example.data.model.BroadcastLog
import com.example.data.model.Donor
import com.example.data.model.EmergencyRequest
import com.example.data.model.RequestStatus
import com.example.data.model.UnionCoordinator
import com.example.data.model.UrgencyLevel
import com.example.data.model.VerificationStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

object DatabaseInitializer {

    fun populateInitialData(database: AppDatabase) {
        CoroutineScope(Dispatchers.IO).launch {
            val donorDao = database.donorDao()
            val requestDao = database.emergencyRequestDao()
            val directoryDao = database.directoryDao()

            val existingDonors = donorDao.getAllDonors().first()
            if (existingDonors.isEmpty()) {
                val seedDonors = listOf(
                    Donor(
                        donorCode = "#MNR-BLOOD-1001",
                        fullName = "Zarif Hasan",
                        phone = "01711223344",
                        bloodGroup = "O+",
                        union = "Rohita",
                        villageAddress = "Rohita Purba Para, Ward 03",
                        nidOrStudentId = "NID: 19984128509012",
                        verificationStatus = VerificationStatus.VERIFIED,
                        availabilityStatus = AvailabilityStatus.AVAILABLE,
                        lastDonationDate = "2024-03-12",
                        totalDonations = 8,
                        age = 26,
                        gender = "Male",
                        emergencyContact = "01799887766",
                        notes = "Lead System Developer & Regular Active Donor"
                    ),
                    Donor(
                        donorCode = "#MNR-BLOOD-1002",
                        fullName = "Mahmudur Rahman Sakib",
                        phone = "01812345678",
                        bloodGroup = "A+",
                        union = "Bhojna",
                        villageAddress = "Bhojna Bazar Road",
                        nidOrStudentId = "NID: 19994128503341",
                        verificationStatus = VerificationStatus.VERIFIED,
                        availabilityStatus = AvailabilityStatus.AVAILABLE,
                        lastDonationDate = "2024-04-10",
                        totalDonations = 5,
                        age = 25,
                        gender = "Male",
                        emergencyContact = "01811223344",
                        notes = "Union Youth Volunteer Coordinator"
                    ),
                    Donor(
                        donorCode = "#MNR-BLOOD-1003",
                        fullName = "Nusrat Jahan Tanha",
                        phone = "01934567890",
                        bloodGroup = "B+",
                        union = "Nehalpur",
                        villageAddress = "Nehalpur High School Road",
                        nidOrStudentId = "STU-ID: MM-2023-881",
                        verificationStatus = VerificationStatus.VERIFIED,
                        availabilityStatus = AvailabilityStatus.DONATED_RECENTLY,
                        lastDonationDate = "2024-08-01",
                        totalDonations = 3,
                        age = 22,
                        gender = "Female",
                        emergencyContact = "01933445566",
                        notes = "Eligible again after 90 days"
                    ),
                    Donor(
                        donorCode = "#MNR-BLOOD-1004",
                        fullName = "Ashiqul Islam",
                        phone = "01755443322",
                        bloodGroup = "AB+",
                        union = "Manirampur Sadar",
                        villageAddress = "Manirampur Hospital Gate",
                        nidOrStudentId = "NID: 19954128509822",
                        verificationStatus = VerificationStatus.PENDING_KYC,
                        availabilityStatus = AvailabilityStatus.AVAILABLE,
                        lastDonationDate = "2023-11-20",
                        totalDonations = 2,
                        age = 29,
                        gender = "Male",
                        emergencyContact = "01755001122",
                        notes = "Uploaded NID for verification check"
                    ),
                    Donor(
                        donorCode = "#MNR-BLOOD-1005",
                        fullName = "Kawsar Hossain",
                        phone = "01622334455",
                        bloodGroup = "O-",
                        union = "Jhanpa",
                        villageAddress = "Jhanpa Baor Road",
                        nidOrStudentId = "NID: 19974128504419",
                        verificationStatus = VerificationStatus.VERIFIED,
                        availabilityStatus = AvailabilityStatus.AVAILABLE,
                        lastDonationDate = "2024-02-15",
                        totalDonations = 6,
                        age = 27,
                        gender = "Male",
                        emergencyContact = "01622009988",
                        notes = "Rare Universal Donor"
                    ),
                    Donor(
                        donorCode = "#MNR-BLOOD-1006",
                        fullName = "Tahmina Akter Ritu",
                        phone = "01788776655",
                        bloodGroup = "A-",
                        union = "Kultia",
                        villageAddress = "Kultia Uttor Para",
                        nidOrStudentId = "STU-ID: JCU-2022-104",
                        verificationStatus = VerificationStatus.PENDING_KYC,
                        availabilityStatus = AvailabilityStatus.AVAILABLE,
                        lastDonationDate = "2024-01-05",
                        totalDonations = 1,
                        age = 21,
                        gender = "Female",
                        emergencyContact = "01788002233",
                        notes = "Awaiting Union Moderator approval"
                    ),
                    Donor(
                        donorCode = "#MNR-BLOOD-1007",
                        fullName = "Sharifuzzaman Bappi",
                        phone = "01344556677",
                        bloodGroup = "B-",
                        union = "Shyamkur",
                        villageAddress = "Shyamkur Bazar",
                        nidOrStudentId = "NID: 19924128506117",
                        verificationStatus = VerificationStatus.VERIFIED,
                        availabilityStatus = AvailabilityStatus.AVAILABLE,
                        lastDonationDate = "2024-05-18",
                        totalDonations = 12,
                        age = 32,
                        gender = "Male",
                        emergencyContact = "01344001122",
                        notes = "Senior Donator - Life Saver Medalist"
                    ),
                    Donor(
                        donorCode = "#MNR-BLOOD-1008",
                        fullName = "Enamul Haque",
                        phone = "01877665544",
                        bloodGroup = "AB-",
                        union = "Khanpur",
                        villageAddress = "Khanpur Dakshin",
                        nidOrStudentId = "NID: 19944128507718",
                        verificationStatus = VerificationStatus.VERIFIED,
                        availabilityStatus = AvailabilityStatus.AVAILABLE,
                        lastDonationDate = "2024-03-30",
                        totalDonations = 4,
                        age = 30,
                        gender = "Male",
                        emergencyContact = "01877112233",
                        notes = "Rare AB- Donor"
                    ),
                    Donor(
                        donorCode = "#MNR-BLOOD-1009",
                        fullName = "Fahim Shahriar (Fake Flagged)",
                        phone = "01500000000",
                        bloodGroup = "O+",
                        union = "Haridaspur",
                        villageAddress = "Haridaspur Bridge",
                        nidOrStudentId = "INVALID_9999",
                        verificationStatus = VerificationStatus.REJECTED,
                        availabilityStatus = AvailabilityStatus.BLOCKED,
                        lastDonationDate = "2020-01-01",
                        totalDonations = 0,
                        age = 20,
                        gender = "Male",
                        emergencyContact = "01500000001",
                        notes = "Banned for submitting misleading contact info"
                    ),
                    Donor(
                        donorCode = "#MNR-BLOOD-1010",
                        fullName = "Sumaiya Parvin",
                        phone = "01999881122",
                        bloodGroup = "O+",
                        union = "Kashimpur",
                        villageAddress = "Kashimpur West",
                        nidOrStudentId = "NID: 20014128501192",
                        verificationStatus = VerificationStatus.VERIFIED,
                        availabilityStatus = AvailabilityStatus.AVAILABLE,
                        lastDonationDate = "2024-04-25",
                        totalDonations = 2,
                        age = 23,
                        gender = "Female",
                        emergencyContact = "01999002233",
                        notes = "Ready for immediate local call"
                    ),
                    Donor(
                        donorCode = "#MNR-BLOOD-1011",
                        fullName = "Al-Amin Sheikh",
                        phone = "01722114477",
                        bloodGroup = "A+",
                        union = "Rohita",
                        villageAddress = "Rohita Durgapur",
                        nidOrStudentId = "NID: 19964128503819",
                        verificationStatus = VerificationStatus.VERIFIED,
                        availabilityStatus = AvailabilityStatus.AVAILABLE,
                        lastDonationDate = "2024-05-02",
                        totalDonations = 7,
                        age = 28,
                        gender = "Male",
                        emergencyContact = "01722998877",
                        notes = "Rohita Union Volunteer"
                    ),
                    Donor(
                        donorCode = "#MNR-BLOOD-1012",
                        fullName = "Rashed Khan",
                        phone = "01833447799",
                        bloodGroup = "B+",
                        union = "Bhojna",
                        villageAddress = "Bhojna Paschim",
                        nidOrStudentId = "NID: 19994128506214",
                        verificationStatus = VerificationStatus.PENDING_KYC,
                        availabilityStatus = AvailabilityStatus.AVAILABLE,
                        lastDonationDate = "2023-12-10",
                        totalDonations = 3,
                        age = 25,
                        gender = "Male",
                        emergencyContact = "01833001122",
                        notes = "Pending Union KYC check"
                    )
                )
                donorDao.insertDonors(seedDonors)
            }

            val existingRequests = requestDao.getAllRequests().first()
            if (existingRequests.isEmpty()) {
                val seedRequests = listOf(
                    EmergencyRequest(
                        requestCode = "#EMR-2024-001",
                        patientName = "Hafizur Rahman (56 yrs)",
                        bloodGroup = "O+",
                        bagsNeeded = 2,
                        hospitalLocation = "Manirampur Upazila Health Complex",
                        union = "Manirampur Sadar",
                        contactPhone = "01712998877",
                        alternatePhone = "01712001122",
                        urgencyLevel = UrgencyLevel.CRITICAL,
                        status = RequestStatus.PENDING,
                        reasonOrDiagnosis = "Emergency Surgery after Road Accident on Jessore-Chuknagar Highway",
                        broadcastRecipientsCount = 0
                    ),
                    EmergencyRequest(
                        requestCode = "#EMR-2024-002",
                        patientName = "Sultana Begum (31 yrs)",
                        bloodGroup = "A+",
                        bagsNeeded = 1,
                        hospitalLocation = "Jessore 250 Bed General Hospital",
                        union = "Rohita",
                        contactPhone = "01822334411",
                        urgencyLevel = UrgencyLevel.CRITICAL,
                        status = RequestStatus.BROADCAST_SENT,
                        reasonOrDiagnosis = "Emergency C-Section Delivery Complication",
                        broadcastRecipientsCount = 14
                    ),
                    EmergencyRequest(
                        requestCode = "#EMR-2024-003",
                        patientName = "Mizanur Rahman (42 yrs)",
                        bloodGroup = "B+",
                        bagsNeeded = 1,
                        hospitalLocation = "Ad-Din Hospital Manirampur",
                        union = "Nehalpur",
                        contactPhone = "01944556622",
                        urgencyLevel = UrgencyLevel.URGENT,
                        status = RequestStatus.APPROVED,
                        reasonOrDiagnosis = "Severe Dengue Hemorrhagic Fever (Platelet Support)",
                        broadcastRecipientsCount = 0
                    ),
                    EmergencyRequest(
                        requestCode = "#EMR-2024-004",
                        patientName = "Anisur Zaman",
                        bloodGroup = "AB-",
                        bagsNeeded = 2,
                        hospitalLocation = "Jessore Queen's Hospital",
                        union = "Bhojna",
                        contactPhone = "01788112233",
                        urgencyLevel = UrgencyLevel.NORMAL,
                        status = RequestStatus.RESOLVED,
                        reasonOrDiagnosis = "Scheduled Orthopedic Knee Surgery",
                        resolvedTimestamp = System.currentTimeMillis() - 86400000L,
                        broadcastRecipientsCount = 3
                    ),
                    EmergencyRequest(
                        requestCode = "#EMR-2024-005",
                        patientName = "Test Spammer 999",
                        bloodGroup = "O-",
                        bagsNeeded = 5,
                        hospitalLocation = "Unknown Fake Clinic",
                        union = "Haridaspur",
                        contactPhone = "01900000000",
                        urgencyLevel = UrgencyLevel.NORMAL,
                        status = RequestStatus.FAKE_REJECTED,
                        reasonOrDiagnosis = "Spam test request with invalid patient credentials",
                        broadcastRecipientsCount = 0
                    )
                )
                requestDao.insertRequests(seedRequests)
            }

            val existingAmbulances = directoryDao.getAllAmbulances().first()
            if (existingAmbulances.isEmpty()) {
                val seedAmbulances = listOf(
                    AmbulanceContact(
                        serviceName = "Manirampur Upazila Health Complex Ambulance",
                        operatorName = "Kabir Hossain",
                        phone = "01715443322",
                        alternatePhone = "01715000111",
                        union = "Manirampur Sadar",
                        ambulanceType = "ICU Ambulance",
                        isAvailable24x7 = true,
                        baseLocation = "Hospital Complex, Manirampur Sadar"
                    ),
                    AmbulanceContact(
                        serviceName = "Red Crescent Emergency Ambulance Service",
                        operatorName = "Rafiqul Islam",
                        phone = "01819876543",
                        alternatePhone = "01819000222",
                        union = "Rohita",
                        ambulanceType = "AC Ambulance",
                        isAvailable24x7 = true,
                        baseLocation = "Rohita Bazar Mor"
                    ),
                    AmbulanceContact(
                        serviceName = "Jessore Highway Express Ambulance",
                        operatorName = "Shahinur Rahman",
                        phone = "01911224466",
                        alternatePhone = "01911000333",
                        union = "Bhojna",
                        ambulanceType = "Standard Non-AC",
                        isAvailable24x7 = true,
                        baseLocation = "Bhojna Road Crossing"
                    ),
                    AmbulanceContact(
                        serviceName = "Nehalpur Community Emergency Ambulance",
                        operatorName = "Jahangir Alam",
                        phone = "01722889900",
                        alternatePhone = "01722000444",
                        union = "Nehalpur",
                        ambulanceType = "AC Ambulance",
                        isAvailable24x7 = true,
                        baseLocation = "Nehalpur Bazar"
                    ),
                    AmbulanceContact(
                        serviceName = "Al-Madina Freezer & Emergency Transport",
                        operatorName = "Tariqul Islam",
                        phone = "01633557799",
                        alternatePhone = "01633000555",
                        union = "Jhanpa",
                        ambulanceType = "Freezer Van & Transport",
                        isAvailable24x7 = false,
                        baseLocation = "Jhanpa Baor Ghat"
                    )
                )
                directoryDao.insertAmbulances(seedAmbulances)
            }

            val existingCoordinators = directoryDao.getAllUnionCoordinators().first()
            if (existingCoordinators.isEmpty()) {
                val seedCoordinators = listOf(
                    UnionCoordinator(
                        unionName = "Rohita",
                        leadCoordinatorName = "Tanvir Ahmed",
                        phone = "01711998811",
                        activeVolunteersCount = 28,
                        assignedModeratorEmail = "tanvir.rohita@mbn.org",
                        emergencyHelpline = "01711998800"
                    ),
                    UnionCoordinator(
                        unionName = "Bhojna",
                        leadCoordinatorName = "Sakib Hossain",
                        phone = "01812998822",
                        activeVolunteersCount = 22,
                        assignedModeratorEmail = "sakib.bhojna@mbn.org",
                        emergencyHelpline = "01812998800"
                    ),
                    UnionCoordinator(
                        unionName = "Nehalpur",
                        leadCoordinatorName = "Rafiqul Islam",
                        phone = "01913998833",
                        activeVolunteersCount = 19,
                        assignedModeratorEmail = "rafiq.nehalpur@mbn.org",
                        emergencyHelpline = "01913998800"
                    ),
                    UnionCoordinator(
                        unionName = "Manirampur Sadar",
                        leadCoordinatorName = "Zarif Hasan (Lead Admin)",
                        phone = "01711223344",
                        activeVolunteersCount = 45,
                        assignedModeratorEmail = "zarifhasan216@gmail.com",
                        emergencyHelpline = "01711000000"
                    ),
                    UnionCoordinator(
                        unionName = "Jhanpa",
                        leadCoordinatorName = "Kamrul Hassan",
                        phone = "01614998844",
                        activeVolunteersCount = 15,
                        assignedModeratorEmail = "kamrul.jhanpa@mbn.org",
                        emergencyHelpline = "01614998800"
                    ),
                    UnionCoordinator(
                        unionName = "Kultia",
                        leadCoordinatorName = "Faruk Hossain",
                        phone = "01715998855",
                        activeVolunteersCount = 18,
                        assignedModeratorEmail = "faruk.kultia@mbn.org",
                        emergencyHelpline = "01715998800"
                    ),
                    UnionCoordinator(
                        unionName = "Shyamkur",
                        leadCoordinatorName = "Moshiur Rahman",
                        phone = "01816998866",
                        activeVolunteersCount = 20,
                        assignedModeratorEmail = "moshiur.shyamkur@mbn.org",
                        emergencyHelpline = "01816998800"
                    ),
                    UnionCoordinator(
                        unionName = "Kashimpur",
                        leadCoordinatorName = "Monirul Islam",
                        phone = "01917998877",
                        activeVolunteersCount = 16,
                        assignedModeratorEmail = "monirul.kashimpur@mbn.org",
                        emergencyHelpline = "01917998800"
                    ),
                    UnionCoordinator(
                        unionName = "Khanpur",
                        leadCoordinatorName = "Shohidul Islam",
                        phone = "01718998888",
                        activeVolunteersCount = 14,
                        assignedModeratorEmail = "shohidul.khanpur@mbn.org",
                        emergencyHelpline = "01718998800"
                    ),
                    UnionCoordinator(
                        unionName = "Haridaspur",
                        leadCoordinatorName = "Azizul Haque",
                        phone = "01819998899",
                        activeVolunteersCount = 12,
                        assignedModeratorEmail = "azizul.haridaspur@mbn.org",
                        emergencyHelpline = "01819998800"
                    )
                )
                directoryDao.insertUnionCoordinators(seedCoordinators)
            }
        }
    }
}
