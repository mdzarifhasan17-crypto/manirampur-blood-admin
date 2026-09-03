package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EmergencyRequest
import com.example.data.model.RequestStatus
import com.example.data.model.UrgencyLevel
import com.example.data.model.UserRole
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
fun EmergencyRequestCard(
    request: EmergencyRequest,
    userRole: UserRole,
    onBroadcast: (EmergencyRequest) -> Unit,
    onResolve: (EmergencyRequest) -> Unit,
    onApprove: (EmergencyRequest) -> Unit,
    onRejectFake: (EmergencyRequest) -> Unit,
    onDelete: (EmergencyRequest) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                1.dp,
                when {
                    request.urgencyLevel == UrgencyLevel.CRITICAL && request.status != RequestStatus.RESOLVED -> CrimsonPrimary
                    request.status == RequestStatus.FAKE_REJECTED -> StatusCritical.copy(alpha = 0.4f)
                    request.status == RequestStatus.RESOLVED -> StatusSuccess.copy(alpha = 0.3f)
                    else -> DarkSurfaceBorder
                },
                RoundedCornerShape(16.dp)
            ),
        color = DarkCardBg
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Top Row: Urgency Tag, Status Badge, Blood Group & Menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Urgency Badge
                    val (urgencyBg, urgencyColor, urgencyText) = when (request.urgencyLevel) {
                        UrgencyLevel.CRITICAL -> Triple(CrimsonPrimary, Color.White, "CRITICAL EMERGENCY")
                        UrgencyLevel.URGENT -> Triple(StatusWarning, Color.Black, "HIGH PRIORITY")
                        UrgencyLevel.NORMAL -> Triple(DarkSurfaceElevated, TextSecondary, "STANDARD")
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(urgencyBg)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (request.urgencyLevel == UrgencyLevel.CRITICAL) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Critical",
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text(
                                text = urgencyText,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = urgencyColor,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 9.sp
                                )
                            )
                        }
                    }

                    // Request Status Pill
                    val (statusColor, statusBg, statusLabel) = when (request.status) {
                        RequestStatus.PENDING -> Triple(StatusWarning, StatusWarning.copy(alpha = 0.15f), "PENDING")
                        RequestStatus.APPROVED -> Triple(Color(0xFF3498DB), Color(0xFF3498DB).copy(alpha = 0.15f), "APPROVED")
                        RequestStatus.BROADCAST_SENT -> Triple(CrimsonGlow, CrimsonDeep.copy(alpha = 0.5f), "BROADCASTED (${request.broadcastRecipientsCount})")
                        RequestStatus.RESOLVED -> Triple(StatusSuccess, StatusSuccess.copy(alpha = 0.15f), "RESOLVED")
                        RequestStatus.CANCELLED -> Triple(TextSecondary, DarkSurfaceElevated, "CANCELLED")
                        RequestStatus.FAKE_REJECTED -> Triple(StatusCritical, StatusCritical.copy(alpha = 0.2f), "FAKE REJECTED")
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(statusBg)
                            .border(1.dp, statusColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = statusLabel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = statusColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            )
                        )
                    }
                }

                // Blood Group Needed Badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(CrimsonPrimary, CrimsonDeep)
                                )
                            )
                            .border(1.dp, CrimsonGlow, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${request.bloodGroup} • ${request.bagsNeeded} Bag(s)",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp
                            )
                        )
                    }

                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.MoreVert, "More", tint = TextSecondary)
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(DarkSurfaceElevated)
                        ) {
                            if (request.status != RequestStatus.RESOLVED) {
                                DropdownMenuItem(
                                    text = { Text("Broadcast Push Notification", color = CrimsonGlow, fontWeight = FontWeight.Bold) },
                                    leadingIcon = { Icon(Icons.Default.Campaign, "Broadcast", tint = CrimsonGlow) },
                                    onClick = {
                                        showMenu = false
                                        onBroadcast(request)
                                    }
                                )

                                DropdownMenuItem(
                                    text = { Text("Mark as Blood Provided / Resolved", color = StatusSuccess) },
                                    leadingIcon = { Icon(Icons.Default.CheckCircle, "Resolve", tint = StatusSuccess) },
                                    onClick = {
                                        showMenu = false
                                        onResolve(request)
                                    }
                                )
                            }

                            if (request.status == RequestStatus.PENDING) {
                                DropdownMenuItem(
                                    text = { Text("Approve Request", color = Color(0xFF3498DB)) },
                                    leadingIcon = { Icon(Icons.Default.CheckCircle, "Approve", tint = Color(0xFF3498DB)) },
                                    onClick = {
                                        showMenu = false
                                        onApprove(request)
                                    }
                                )
                            }

                            if (request.status != RequestStatus.FAKE_REJECTED) {
                                DropdownMenuItem(
                                    text = { Text("Flag & Reject as Fake Request", color = StatusCritical) },
                                    leadingIcon = { Icon(Icons.Default.Block, "Reject Fake", tint = StatusCritical) },
                                    onClick = {
                                        showMenu = false
                                        onRejectFake(request)
                                    }
                                )
                            }

                            if (userRole == UserRole.SUPER_ADMIN) {
                                DropdownMenuItem(
                                    text = { Text("Delete Request", color = StatusCritical) },
                                    leadingIcon = { Icon(Icons.Default.Delete, "Delete", tint = StatusCritical) },
                                    onClick = {
                                        showMenu = false
                                        onDelete(request)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Patient Name & Code
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = request.patientName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                )

                Text(
                    text = request.requestCode,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextSecondary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Reason / Diagnosis
            Text(
                text = request.reasonOrDiagnosis,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Hospital & Union Details
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocalHospital,
                    contentDescription = "Hospital",
                    tint = CrimsonPrimary,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = request.hospitalLocation,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 3.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Union Location",
                    tint = TextSecondary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Union: ${request.union}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Row: 1-Click Broadcast Button & Dial Phone
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Call Patient Attendant
                OutlinedButton(
                    onClick = {
                        val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${request.contactPhone}"))
                        context.startActivity(dialIntent)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)
                ) {
                    Icon(Icons.Default.Call, contentDescription = "Call", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = request.contactPhone, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                // Instant Broadcast Trigger Button
                if (request.status != RequestStatus.RESOLVED && request.status != RequestStatus.FAKE_REJECTED) {
                    Button(
                        onClick = { onBroadcast(request) },
                        modifier = Modifier.weight(1.3f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CrimsonPrimary,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(Icons.Default.Campaign, contentDescription = "Broadcast", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (request.status == RequestStatus.BROADCAST_SENT) "Re-Broadcast Alert" else "1-Click Broadcast",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else if (request.status == RequestStatus.RESOLVED) {
                    FilledTonalButton(
                        onClick = {},
                        enabled = false,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            disabledContainerColor = StatusSuccess.copy(alpha = 0.2f),
                            disabledContentColor = StatusSuccess
                        )
                    ) {
                        Icon(Icons.Default.CheckCircle, "Resolved", modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Resolved", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
