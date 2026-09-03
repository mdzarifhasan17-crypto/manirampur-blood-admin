package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AdminProfile
import com.example.data.model.AvailabilityStatus
import com.example.data.model.Donor
import com.example.data.model.EmergencyRequest
import com.example.data.model.RequestStatus
import com.example.data.model.UrgencyLevel
import com.example.data.model.UserRole
import com.example.data.model.VerificationStatus
import com.example.ui.components.FooterBranding
import com.example.ui.components.MetricCard
import com.example.ui.theme.AccentGold
import com.example.ui.theme.BloodRed
import com.example.ui.theme.CrimsonDark
import com.example.ui.theme.CrimsonDeep
import com.example.ui.theme.CrimsonGlow
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.DarkCardBg
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.StatusCritical
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun DashboardScreen(
    currentAdmin: AdminProfile,
    selectedUnion: String,
    donors: List<Donor>,
    requests: List<EmergencyRequest>,
    onNavigateToDonors: () -> Unit,
    onNavigateToRequests: () -> Unit,
    onNavigateToExport: () -> Unit,
    onQuickBroadcast: () -> Unit,
    onAddNewDonor: () -> Unit,
    onAddNewRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Calculated Analytics
    val totalDonors = donors.size
    val verifiedDonors = donors.count { it.verificationStatus == VerificationStatus.VERIFIED }
    val pendingKYCDonors = donors.count { it.verificationStatus == VerificationStatus.PENDING_KYC }
    val activeAvailableDonors = donors.count { it.availabilityStatus == AvailabilityStatus.AVAILABLE && it.verificationStatus == VerificationStatus.VERIFIED }
    val ineligibleOrRecentDonors = donors.count { it.availabilityStatus != AvailabilityStatus.AVAILABLE || it.verificationStatus != VerificationStatus.VERIFIED }
    val totalDonationsGiven = donors.sumOf { it.totalDonations }

    val pendingRequests = requests.count { it.status == RequestStatus.PENDING || it.status == RequestStatus.APPROVED || it.status == RequestStatus.BROADCAST_SENT }
    val resolvedRequests = requests.count { it.status == RequestStatus.RESOLVED }
    val criticalEmergencyRequests = requests.filter { it.urgencyLevel == UrgencyLevel.CRITICAL && it.status != RequestStatus.RESOLVED }

    val bloodGroups = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))

            // Welcome & Live System Status Banner
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = DarkCardBg,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Brush.horizontalGradient(
                        listOf(CrimsonDark.copy(alpha = 0.8f), DarkSurfaceBorder)
                    )
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(CrimsonDeep.copy(alpha = 0.4f), Color.Transparent)
                            )
                        )
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(StatusSuccess)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "SYSTEM ONLINE • REAL-TIME SYNC",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = StatusSuccess,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp
                                    )
                                )
                            }

                            Text(
                                text = "Union: $selectedUnion",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Admin Analytics Hub",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                color = TextPrimary,
                                fontWeight = FontWeight.Black
                            )
                        )

                        Text(
                            text = if (currentAdmin.role == UserRole.SUPER_ADMIN)
                                "Full Manirampur Upazila command center for blood donors and live emergencies."
                            else
                                "Moderator panel for ${currentAdmin.assignedUnion} Union. Restricted local controls active.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }
        }

        // Live Critical Emergency Alerts Ticker (if any)
        if (criticalEmergencyRequests.isNotEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = CrimsonDeep.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonPrimary)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Emergency,
                                    contentDescription = "Alert",
                                    tint = CrimsonGlow,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "LIVE CRITICAL BLOOD ALERTS (${criticalEmergencyRequests.size})",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = CrimsonGlow,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 0.5.sp
                                    )
                                )
                            }

                            Text(
                                text = "View All ▶",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.clickable { onNavigateToRequests() }
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        criticalEmergencyRequests.take(2).forEach { alert ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DarkSurfaceElevated.copy(alpha = 0.8f))
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "${alert.patientName} (${alert.bloodGroup} • ${alert.bagsNeeded} Bags)",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = TextPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        text = "${alert.hospitalLocation} • ${alert.union}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = TextSecondary,
                                            fontSize = 11.sp
                                        )
                                    )
                                }

                                Button(
                                    onClick = onQuickBroadcast,
                                    shape = RoundedCornerShape(6.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Text("Broadcast", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Real-Time Analytics KPI Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "REAL-TIME KPI ANALYTICS",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = TextSecondary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = "Total Donors",
                        value = "$totalDonors",
                        subtitle = "$verifiedDonors Verified • $pendingKYCDonors KYC Pending",
                        icon = Icons.Default.Group,
                        accentColor = CrimsonPrimary,
                        modifier = Modifier.weight(1f)
                    )

                    MetricCard(
                        title = "Available Now",
                        value = "$activeAvailableDonors",
                        subtitle = "$ineligibleOrRecentDonors Ineligible / Donated",
                        icon = Icons.Default.CheckCircle,
                        accentColor = StatusSuccess,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = "Pending Emergencies",
                        value = "$pendingRequests",
                        subtitle = "$resolvedRequests Blood Requests Resolved",
                        icon = Icons.Default.NotificationImportant,
                        accentColor = if (pendingRequests > 0) StatusCritical else TextSecondary,
                        modifier = Modifier.weight(1f)
                    )

                    MetricCard(
                        title = "Units Donated",
                        value = "$totalDonationsGiven",
                        subtitle = "Lives Saved Across Manirampur",
                        icon = Icons.Default.Favorite,
                        accentColor = AccentGold,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Quick Command Shortcuts
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = DarkCardBg,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "QUICK CONTROL ACTIONS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextSecondary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Quick 1-Click Push Broadcast
                        Button(
                            onClick = onQuickBroadcast,
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary)
                        ) {
                            Icon(Icons.Default.Campaign, "Broadcast", modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Broadcast", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        // Add Donor
                        OutlinedButton(
                            onClick = onAddNewDonor,
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)
                        ) {
                            Icon(Icons.Default.PersonAdd, "Add Donor", modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("New Donor", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        // Export Roster
                        OutlinedButton(
                            onClick = onNavigateToExport,
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)
                        ) {
                            Icon(Icons.Default.Description, "Export", modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Export", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Blood Group Inventory & Availability Heatmap
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = DarkCardBg,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "BLOOD GROUP INVENTORY & AVAILABILITY",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextSecondary,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        )

                        Text(
                            text = "Manage Donors ▶",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = CrimsonPrimary,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.clickable { onNavigateToDonors() }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 4x2 Grid of Blood Groups
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        bloodGroups.chunked(4).forEach { rowGroups ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowGroups.forEach { bg ->
                                    val count = donors.count { it.bloodGroup.equals(bg, ignoreCase = true) }
                                    val available = donors.count {
                                        it.bloodGroup.equals(bg, ignoreCase = true) &&
                                                it.availabilityStatus == AvailabilityStatus.AVAILABLE &&
                                                it.verificationStatus == VerificationStatus.VERIFIED
                                    }

                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .border(
                                                1.dp,
                                                if (available > 0) CrimsonDark.copy(alpha = 0.6f) else DarkSurfaceBorder,
                                                RoundedCornerShape(10.dp)
                                            ),
                                        color = DarkSurfaceElevated
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(8.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = bg,
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    color = if (available > 0) CrimsonGlow else TextSecondary,
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 15.sp
                                                )
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "$available Ready",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = if (available > 0) StatusSuccess else TextTertiary,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 10.sp
                                                )
                                            )
                                            Text(
                                                text = "$count Total",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = TextTertiary,
                                                    fontSize = 9.sp
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Union-Level Donor Distribution Analytics
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = DarkCardBg,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "MANIRAMPUR UNION DENSITY DISTRIBUTION",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextSecondary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    val topUnions = donors.groupBy { it.union }
                        .map { (union, list) -> union to list.size }
                        .sortedByDescending { it.second }
                        .take(5)

                    val maxUnionCount = (topUnions.maxOfOrNull { it.second } ?: 1).coerceAtLeast(1)

                    topUnions.forEach { (unionName, count) ->
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.LocationOn,
                                        "Union",
                                        tint = CrimsonPrimary,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        text = unionName,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = TextPrimary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }

                                Text(
                                    text = "$count Donors",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = TextSecondary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            LinearProgressIndicator(
                                progress = { count.toFloat() / maxUnionCount.toFloat() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = CrimsonPrimary,
                                trackColor = DarkSurfaceElevated
                            )
                        }
                    }
                }
            }
        }

        // Mandatory Footer Branding
        item {
            FooterBranding()
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
