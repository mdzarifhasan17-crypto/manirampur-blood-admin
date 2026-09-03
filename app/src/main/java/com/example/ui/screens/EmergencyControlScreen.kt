package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AdminProfile
import com.example.data.model.BroadcastLog
import com.example.data.model.Donor
import com.example.data.model.EmergencyRequest
import com.example.data.model.MANIRAMPUR_UNIONS
import com.example.data.model.RequestStatus
import com.example.data.model.UrgencyLevel
import com.example.data.model.UserRole
import com.example.ui.components.BroadcastDialog
import com.example.ui.components.EmergencyRequestCard
import com.example.ui.components.FooterBranding
import com.example.ui.theme.CrimsonDeep
import com.example.ui.theme.CrimsonGlow
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.DarkCardBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.StatusCritical
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyControlScreen(
    currentAdmin: AdminProfile,
    selectedUnion: String,
    requests: List<EmergencyRequest>,
    broadcastLogs: List<BroadcastLog>,
    availableDonors: List<Donor>,
    urgencyFilter: String?,
    statusFilter: String?,
    onUrgencyFilterChange: (String?) -> Unit,
    onStatusFilterChange: (String?) -> Unit,
    onSaveRequest: (EmergencyRequest) -> Unit,
    onApproveRequest: (EmergencyRequest) -> Unit,
    onResolveRequest: (EmergencyRequest) -> Unit,
    onRejectFakeRequest: (EmergencyRequest) -> Unit,
    onDeleteRequest: (EmergencyRequest) -> Unit,
    onTriggerBroadcast: (targetBg: String, targetUnion: String, title: String, msg: String, reqId: Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    var activeBroadcastRequest by remember { mutableStateOf<EmergencyRequest?>(null) }
    var isDirectBroadcastOpen by remember { mutableStateOf(false) }
    var isCreatingNewRequest by remember { mutableStateOf(false) }
    var showAuditLogs by remember { mutableStateOf(false) }

    val urgencyFilters = listOf("All", "CRITICAL", "URGENT", "NORMAL")
    val statusFilters = listOf("All", "PENDING", "APPROVED", "BROADCAST_SENT", "RESOLVED", "FAKE_REJECTED")

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header & Broadcast trigger
            item {
                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "EMERGENCY CONTROL CENTER",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = TextPrimary,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            )
                        )
                        Text(
                            text = "Live patient requests & 1-click Push Broadcast",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(
                            onClick = { isDirectBroadcastOpen = true },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Campaign, "Broadcast", modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Push Alert", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { isCreatingNewRequest = true },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceElevated),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Add, "New", modifier = Modifier.size(16.dp), tint = TextPrimary)
                            Spacer(Modifier.width(4.dp))
                            Text("Post Request", fontSize = 11.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Urgency Filter Chips
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    urgencyFilters.forEach { u ->
                        val isSelected = (urgencyFilter == null && u == "All") || urgencyFilter == u
                        FilterChip(
                            selected = isSelected,
                            onClick = { onUrgencyFilterChange(if (u == "All") null else u) },
                            label = { Text(if (u == "CRITICAL") "🚨 CRITICAL" else u, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = if (u == "CRITICAL") CrimsonPrimary else DarkSurfaceElevated,
                                selectedLabelColor = Color.White,
                                containerColor = DarkSurfaceElevated,
                                labelColor = TextSecondary
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            }

            // Status Filter Chips
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    statusFilters.forEach { s ->
                        val isSelected = (statusFilter == null && s == "All") || statusFilter == s
                        FilterChip(
                            selected = isSelected,
                            onClick = { onStatusFilterChange(if (s == "All") null else s) },
                            label = { Text(s.replace("_", " "), fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = when (s) {
                                    "RESOLVED" -> StatusSuccess
                                    "FAKE_REJECTED" -> StatusCritical
                                    else -> CrimsonPrimary
                                },
                                selectedLabelColor = Color.White,
                                containerColor = DarkSurfaceElevated,
                                labelColor = TextSecondary
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            }

            // Broadcast Logs Toggle Button
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(DarkSurfaceElevated)
                        .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(10.dp)),
                    color = DarkSurfaceElevated
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.History, "Logs", tint = CrimsonGlow, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Push Broadcast Audit History (${broadcastLogs.size})",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        Button(
                            onClick = { showAuditLogs = !showAuditLogs },
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonDeep),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(26.dp)
                        ) {
                            Text(if (showAuditLogs) "Hide Logs" else "View Logs", fontSize = 10.sp, color = CrimsonGlow)
                        }
                    }
                }
            }

            // Broadcast Logs View
            if (showAuditLogs) {
                if (broadcastLogs.isEmpty()) {
                    item {
                        Text(
                            text = "No broadcast alerts sent yet.",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                } else {
                    items(broadcastLogs, key = { it.id }) { log ->
                        val dateFormatted = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(log.timestamp))
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = DarkCardBg,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = log.title,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = CrimsonGlow,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        text = dateFormatted,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = TextSecondary,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                                Text(
                                    text = log.message,
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp),
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                                Text(
                                    text = "Sent to ${log.recipientCount} Donors (${log.targetBloodGroup} in ${log.targetUnion}) • by ${log.sentBy}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = StatusSuccess,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Emergency Requests List
            if (requests.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        color = DarkCardBg,
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.LocalHospital, "No Requests", tint = TextSecondary, modifier = Modifier.size(48.dp))
                            Spacer(Modifier.height(10.dp))
                            Text("No Blood Requests Active", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("All emergency requests for $selectedUnion are resolved or filtered.", color = TextSecondary, fontSize = 12.sp)
                        }
                    }
                }
            } else {
                items(requests, key = { it.id }) { req ->
                    EmergencyRequestCard(
                        request = req,
                        userRole = currentAdmin.role,
                        onBroadcast = { activeBroadcastRequest = it },
                        onResolve = onResolveRequest,
                        onApprove = onApproveRequest,
                        onRejectFake = onRejectFakeRequest,
                        onDelete = onDeleteRequest
                    )
                }
            }

            // Mandatory Footer Branding
            item {
                FooterBranding()
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    // Trigger Broadcast Dialog
    if (activeBroadcastRequest != null || isDirectBroadcastOpen) {
        val req = activeBroadcastRequest
        val targetBg = req?.bloodGroup ?: "O+"
        val targetUnion = req?.union ?: selectedUnion
        val matchingCount = availableDonors.count {
            it.bloodGroup.equals(targetBg, ignoreCase = true) &&
                    (targetUnion == "All Unions" || it.union.equals(targetUnion, ignoreCase = true)) &&
                    it.verificationStatus == com.example.data.model.VerificationStatus.VERIFIED &&
                    it.availabilityStatus == com.example.data.model.AvailabilityStatus.AVAILABLE
        }

        BroadcastDialog(
            request = req,
            matchingDonorsCount = matchingCount,
            onDismiss = {
                activeBroadcastRequest = null
                isDirectBroadcastOpen = false
            },
            onSendBroadcast = { bg, union, title, msg ->
                onTriggerBroadcast(bg, union, title, msg, req?.id)
                activeBroadcastRequest = null
                isDirectBroadcastOpen = false
            }
        )
    }

    // Create New Request Modal Sheet
    if (isCreatingNewRequest) {
        CreateRequestSheet(
            defaultUnion = selectedUnion,
            onDismiss = { isCreatingNewRequest = false },
            onSave = { newReq ->
                onSaveRequest(newReq)
                isCreatingNewRequest = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateRequestSheet(
    defaultUnion: String,
    onDismiss: () -> Unit,
    onSave: (EmergencyRequest) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var patientName by remember { mutableStateOf("") }
    var bloodGroup by remember { mutableStateOf("O+") }
    var bagsNeeded by remember { mutableIntStateOf(1) }
    var hospitalLocation by remember { mutableStateOf("Manirampur Upazila Health Complex") }
    var union by remember { mutableStateOf(if (defaultUnion != "All Unions") defaultUnion else "Manirampur Sadar") }
    var contactPhone by remember { mutableStateOf("") }
    var urgencyLevel by remember { mutableStateOf(UrgencyLevel.CRITICAL) }
    var reason by remember { mutableStateOf("Emergency Surgery & Blood Transfusion") }

    var showBgDropdown by remember { mutableStateOf(false) }
    var showUnionDropdown by remember { mutableStateOf(false) }
    val bloodGroups = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Post New Emergency Blood Request",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, "Close", tint = TextSecondary)
                }
            }

            Spacer(Modifier.height(14.dp))

            OutlinedTextField(
                value = patientName,
                onValueChange = { patientName = it },
                label = { Text("Patient Name & Age") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CrimsonPrimary,
                    unfocusedBorderColor = DarkSurfaceBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )

            Spacer(Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = bloodGroup,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Blood Group") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CrimsonPrimary,
                            unfocusedBorderColor = DarkSurfaceBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        trailingIcon = { Text("▼", color = CrimsonPrimary, modifier = Modifier.padding(end = 6.dp)) }
                    )
                    DropdownMenu(expanded = showBgDropdown, onDismissRequest = { showBgDropdown = false }, modifier = Modifier.background(DarkSurfaceElevated)) {
                        bloodGroups.forEach { bg ->
                            DropdownMenuItem(text = { Text(bg, color = TextPrimary) }, onClick = { bloodGroup = bg; showBgDropdown = false })
                        }
                    }
                }

                OutlinedTextField(
                    value = contactPhone,
                    onValueChange = { contactPhone = it },
                    label = { Text("Contact Phone") },
                    modifier = Modifier.weight(1.4f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CrimsonPrimary,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            }

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = hospitalLocation,
                onValueChange = { hospitalLocation = it },
                label = { Text("Hospital / Clinic Location") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CrimsonPrimary,
                    unfocusedBorderColor = DarkSurfaceBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = reason,
                onValueChange = { reason = it },
                label = { Text("Medical Reason / Case Diagnosis") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CrimsonPrimary,
                    unfocusedBorderColor = DarkSurfaceBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    val randomCode = "#EMR-2024-${(100..999).random()}"
                    val req = EmergencyRequest(
                        requestCode = randomCode,
                        patientName = patientName.ifBlank { "Emergency Patient" },
                        bloodGroup = bloodGroup,
                        bagsNeeded = bagsNeeded,
                        hospitalLocation = hospitalLocation,
                        union = union,
                        contactPhone = contactPhone.ifBlank { "01700000000" },
                        urgencyLevel = urgencyLevel,
                        status = RequestStatus.PENDING,
                        reasonOrDiagnosis = reason
                    )
                    onSave(req)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary)
            ) {
                Text("POST EMERGENCY REQUEST", fontWeight = FontWeight.Bold)
            }
        }
    }
}
