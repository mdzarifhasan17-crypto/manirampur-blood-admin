package com.example.ui.components

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.data.model.AvailabilityStatus
import com.example.data.model.Donor
import com.example.data.model.UserRole
import com.example.data.model.VerificationStatus
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
fun DonorItemCard(
    donor: Donor,
    userRole: UserRole,
    onEdit: (Donor) -> Unit,
    onVerify: (Donor, VerificationStatus) -> Unit,
    onToggleBan: (Donor) -> Unit,
    onRecordDonation: (Donor) -> Unit,
    onDelete: (Donor) -> Unit,
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
                if (donor.availabilityStatus == AvailabilityStatus.BLOCKED)
                    StatusCritical.copy(alpha = 0.5f)
                else if (donor.verificationStatus == VerificationStatus.PENDING_KYC)
                    StatusWarning.copy(alpha = 0.4f)
                else DarkSurfaceBorder,
                RoundedCornerShape(16.dp)
            ),
        color = DarkCardBg
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Top Row: Code, Verification Badge, Blood Group & Menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Donor ID & KYC Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = donor.donorCode,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextSecondary,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )

                    // KYC Verification Badge
                    when (donor.verificationStatus) {
                        VerificationStatus.VERIFIED -> {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(StatusSuccess.copy(alpha = 0.15f))
                                    .border(1.dp, StatusSuccess.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Verified",
                                    tint = StatusSuccess,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "VERIFIED",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = StatusSuccess,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    )
                                )
                            }
                        }
                        VerificationStatus.PENDING_KYC -> {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(StatusWarning.copy(alpha = 0.15f))
                                    .border(1.dp, StatusWarning.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.HourglassTop,
                                    contentDescription = "Pending KYC",
                                    tint = StatusWarning,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "PENDING KYC",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = StatusWarning,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    )
                                )
                            }
                        }
                        VerificationStatus.REJECTED -> {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(StatusCritical.copy(alpha = 0.15f))
                                    .border(1.dp, StatusCritical.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Block,
                                    contentDescription = "Rejected",
                                    tint = StatusCritical,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "REJECTED",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = StatusCritical,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    )
                                )
                            }
                        }
                    }

                    // Blocked Badge if banned
                    if (donor.availabilityStatus == AvailabilityStatus.BLOCKED) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(StatusCritical.copy(alpha = 0.25f))
                                .border(1.dp, StatusCritical, RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "BANNED",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = StatusCritical,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 9.sp
                                )
                            )
                        }
                    }
                }

                // Blood Group Badge & Menu
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(CrimsonPrimary, CrimsonDeep)
                                )
                            )
                            .border(1.dp, CrimsonGlow.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = donor.bloodGroup,
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp
                            )
                        )
                    }

                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More Options",
                                tint = TextSecondary
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(DarkSurfaceElevated)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Edit Donor Profile", color = TextPrimary) },
                                leadingIcon = { Icon(Icons.Default.Edit, "Edit", tint = TextPrimary) },
                                onClick = {
                                    showMenu = false
                                    onEdit(donor)
                                }
                            )

                            if (donor.verificationStatus != VerificationStatus.VERIFIED) {
                                DropdownMenuItem(
                                    text = { Text("Approve KYC Badge", color = StatusSuccess) },
                                    leadingIcon = { Icon(Icons.Default.Verified, "Approve", tint = StatusSuccess) },
                                    onClick = {
                                        showMenu = false
                                        onVerify(donor, VerificationStatus.VERIFIED)
                                    }
                                )
                            }

                            if (donor.verificationStatus != VerificationStatus.REJECTED) {
                                DropdownMenuItem(
                                    text = { Text("Reject KYC Application", color = StatusWarning) },
                                    leadingIcon = { Icon(Icons.Default.Block, "Reject", tint = StatusWarning) },
                                    onClick = {
                                        showMenu = false
                                        onVerify(donor, VerificationStatus.REJECTED)
                                    }
                                )
                            }

                            DropdownMenuItem(
                                text = { Text("Record Blood Donation (+1)", color = CrimsonGlow) },
                                leadingIcon = { Icon(Icons.Default.Favorite, "Donated", tint = CrimsonGlow) },
                                onClick = {
                                    showMenu = false
                                    onRecordDonation(donor)
                                }
                            )

                            DropdownMenuItem(
                                text = {
                                    Text(
                                        if (donor.availabilityStatus == AvailabilityStatus.BLOCKED) "Unban Account" else "Ban / Block Donor",
                                        color = if (donor.availabilityStatus == AvailabilityStatus.BLOCKED) StatusSuccess else StatusCritical
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Security,
                                        "Ban",
                                        tint = if (donor.availabilityStatus == AvailabilityStatus.BLOCKED) StatusSuccess else StatusCritical
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    onToggleBan(donor)
                                }
                            )

                            if (userRole == UserRole.SUPER_ADMIN) {
                                DropdownMenuItem(
                                    text = { Text("Delete Donor (Admin)", color = StatusCritical) },
                                    leadingIcon = { Icon(Icons.Default.Delete, "Delete", tint = StatusCritical) },
                                    onClick = {
                                        showMenu = false
                                        onDelete(donor)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Donor Name & Bio
            Text(
                text = donor.fullName,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(top = 2.dp)
            ) {
                Text(
                    text = "${donor.gender} • ${donor.age} yrs",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                )
                Text(text = "•", color = TextTertiary)
                Text(
                    text = donor.nidOrStudentId,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Location & Address
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Union Location",
                    tint = CrimsonPrimary,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${donor.union} • ${donor.villageAddress}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextPrimary,
                        fontSize = 12.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Donation History Summary Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkSurfaceElevated)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Total Donations",
                        tint = CrimsonGlow,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Donations: ${donor.totalDonations} times",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Event,
                        contentDescription = "Last Donated",
                        tint = TextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (donor.lastDonationDate.isNotBlank()) "Last: ${donor.lastDonationDate}" else "Never Donated",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom Actions: Direct Phone Call, KYC Approval quick button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Direct Call Button
                OutlinedButton(
                    onClick = {
                        val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${donor.phone}"))
                        context.startActivity(dialIntent)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonPrimary.copy(alpha = 0.5f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CrimsonGlow)
                ) {
                    Icon(Icons.Default.Call, contentDescription = "Call", modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = donor.phone, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                // Quick KYC Approve if Pending
                if (donor.verificationStatus == VerificationStatus.PENDING_KYC) {
                    FilledTonalButton(
                        onClick = { onVerify(donor, VerificationStatus.VERIFIED) },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = StatusSuccess.copy(alpha = 0.2f),
                            contentColor = StatusSuccess
                        )
                    ) {
                        Icon(Icons.Default.CheckCircle, "Approve KYC", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Verify KYC", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
