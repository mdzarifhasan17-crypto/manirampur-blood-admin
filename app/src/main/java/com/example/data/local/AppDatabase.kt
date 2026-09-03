package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.model.AmbulanceContact
import com.example.data.model.BroadcastLog
import com.example.data.model.Donor
import com.example.data.model.EmergencyRequest
import com.example.data.model.UnionCoordinator

@Database(
    entities = [
        Donor::class,
        EmergencyRequest::class,
        AmbulanceContact::class,
        UnionCoordinator::class,
        BroadcastLog::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun donorDao(): DonorDao
    abstract fun emergencyRequestDao(): EmergencyRequestDao
    abstract fun directoryDao(): DirectoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "manirampur_blood_network_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
