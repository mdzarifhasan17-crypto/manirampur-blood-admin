package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.AvailabilityStatus
import com.example.data.model.RequestStatus
import com.example.data.model.UrgencyLevel
import com.example.data.model.VerificationStatus

class Converters {
    @TypeConverter
    fun fromVerificationStatus(value: VerificationStatus): String = value.name

    @TypeConverter
    fun toVerificationStatus(value: String): VerificationStatus = try {
        VerificationStatus.valueOf(value)
    } catch (e: Exception) {
        VerificationStatus.PENDING_KYC
    }

    @TypeConverter
    fun fromAvailabilityStatus(value: AvailabilityStatus): String = value.name

    @TypeConverter
    fun toAvailabilityStatus(value: String): AvailabilityStatus = try {
        AvailabilityStatus.valueOf(value)
    } catch (e: Exception) {
        AvailabilityStatus.AVAILABLE
    }

    @TypeConverter
    fun fromUrgencyLevel(value: UrgencyLevel): String = value.name

    @TypeConverter
    fun toUrgencyLevel(value: String): UrgencyLevel = try {
        UrgencyLevel.valueOf(value)
    } catch (e: Exception) {
        UrgencyLevel.CRITICAL
    }

    @TypeConverter
    fun fromRequestStatus(value: RequestStatus): String = value.name

    @TypeConverter
    fun toRequestStatus(value: String): RequestStatus = try {
        RequestStatus.valueOf(value)
    } catch (e: Exception) {
        RequestStatus.PENDING
    }
}
