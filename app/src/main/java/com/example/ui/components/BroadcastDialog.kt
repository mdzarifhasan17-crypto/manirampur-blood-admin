package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EmergencyRequest
import com.example.data.model.MANIRAMPUR_UNIONS
import com.example.ui.theme.AccentGold
import com.example.ui.theme.BloodRed
import com.example.ui.theme.CrimsonDark
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
import com.example.ui.theme.TextTertiary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BroadcastDialog(
    request: EmergencyRequest?,
    matchingDonorsCount: Int,
    onDismiss: () -> Unit,
    onSendBroadcast: (targetBloodGroup: String, targetUnion: String, title: String, message: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var bloodGroup by remember { mutableStateOf(request?.bloodGroup ?: "O+") }
    var targetUnion by remember { mutableStateOf(request?.union ?: "All Unions") }
    var title by remember {
        mutableStateOf(
            if (request != null) "🚨 URGENT: ${request.bloodGroup} Blood Needed (${request.hospitalLocation})"
            else "🚨 EMERGENCY: Blood Needed in Manirampur"
        )
    }
    var message by remember {
        mutableStateOf(
            if (request != null)
                "Patient: ${request.patientName}. Location: ${request.hospitalLocation}, Union: ${request.union}. Bags: ${request.bagsNeeded}. Call: ${request.contactPhone}. If available, please respond immediately!"
            else
                "Critical patient urgently requires blood donation. Please contact the coordinator if you are available."
        )
    }

    var showBloodGroupDropdown by remember { mutableStateOf(false) }
    var showUnionDropdown by remember { mutableStateOf(false) }

    val bloodGroupsList = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

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
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(CrimsonDeep)
                            .border(1.dp, CrimsonPrimary, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = "Push Broadcast",
                            tint = CrimsonGlow,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Broadcast Push Alert System",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "Instant High-Priority Push to Registered Donors",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Real-Time Matched Donors Metric Banner
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = CrimsonDeep.copy(alpha = 0.4f),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonPrimary.copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = "Target",
                            tint = CrimsonGlow,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Estimated Recipients:",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                            )
                            Text(
                                text = "$matchingDonorsCount Verified Active Donors",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Black
                                )
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(CrimsonPrimary)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "$bloodGroup • $targetUnion",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Blood Group & Union Targeting
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Blood Group Picker
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = bloodGroup,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Target Group") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showBloodGroupDropdown = true },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CrimsonPrimary,
                            unfocusedBorderColor = DarkSurfaceBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        trailingIcon = { Text("▼", color = CrimsonPrimary, modifier = Modifier.padding(end = 6.dp)) }
                    )

                    DropdownMenu(
                        expanded = showBloodGroupDropdown,
                        onDismissRequest = { showBloodGroupDropdown = false },
                        modifier = Modifier.background(DarkSurfaceElevated)
                    ) {
                        bloodGroupsList.forEach { bg ->
                            DropdownMenuItem(
                                text = { Text(bg, color = TextPrimary, fontWeight = FontWeight.Bold) },
                                onClick = {
                                    bloodGroup = bg
                                    showBloodGroupDropdown = false
                                }
                            )
                        }
                    }
                }

                // Union Scope Picker
                Box(modifier = Modifier.weight(1.4f)) {
                    OutlinedTextField(
                        value = targetUnion,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Target Union") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showUnionDropdown = true },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CrimsonPrimary,
                            unfocusedBorderColor = DarkSurfaceBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        trailingIcon = { Text("▼", color = CrimsonPrimary, modifier = Modifier.padding(end = 6.dp)) }
                    )

                    DropdownMenu(
                        expanded = showUnionDropdown,
                        onDismissRequest = { showUnionDropdown = false },
                        modifier = Modifier.background(DarkSurfaceElevated)
                    ) {
                        MANIRAMPUR_UNIONS.forEach { un ->
                            DropdownMenuItem(
                                text = { Text(un, color = TextPrimary) },
                                onClick = {
                                    targetUnion = un
                                    showUnionDropdown = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Title
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Notification Push Title") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CrimsonPrimary,
                    unfocusedBorderColor = DarkSurfaceBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Body
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                label = { Text("Push Alert Message Body") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CrimsonPrimary,
                    unfocusedBorderColor = DarkSurfaceBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                minLines = 3,
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Push Notification Dispatch Button
            Button(
                onClick = {
                    onSendBroadcast(bloodGroup, targetUnion, title, message)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CrimsonPrimary,
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send", modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "DISPATCH PUSH BROADCAST NOW",
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}
