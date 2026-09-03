package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AdminProfile
import com.example.data.model.AvailabilityStatus
import com.example.data.model.Donor
import com.example.data.model.MANIRAMPUR_UNIONS
import com.example.data.model.VerificationStatus
import com.example.ui.components.FooterBranding
import com.example.ui.theme.CrimsonDeep
import com.example.ui.theme.CrimsonGlow
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.DarkCardBg
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ExportReportsScreen(
    currentAdmin: AdminProfile,
    allDonors: List<Donor>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedUnion by remember { mutableStateOf(currentAdmin.assignedUnion ?: "All Unions") }
    var selectedBloodGroup by remember { mutableStateOf("All") }
    var exportFormat by remember { mutableStateOf("CSV Format") } // "CSV Format" or "Medical Camp Roster"

    var showUnionDropdown by remember { mutableStateOf(false) }
    var showBgDropdown by remember { mutableStateOf(false) }
    var showFormatDropdown by remember { mutableStateOf(false) }

    val bloodGroupsList = listOf("All", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

    // Filtered data for export
    val exportList = allDonors.filter { donor ->
        val matchesUnion = if (selectedUnion == "All Unions") true else donor.union.equals(selectedUnion, ignoreCase = true)
        val matchesBg = if (selectedBloodGroup == "All") true else donor.bloodGroup.equals(selectedBloodGroup, ignoreCase = true)
        matchesUnion && matchesBg
    }

    // Generated Content
    val generatedReportText = remember(exportList, exportFormat, selectedUnion, selectedBloodGroup) {
        val today = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())
        if (exportFormat == "CSV Format") {
            buildString {
                appendLine("Donor_ID,Full_Name,Blood_Group,Phone_Number,Union,Village_Address,KYC_Status,Availability,Total_Donations,Last_Donation_Date")
                exportList.forEach { d ->
                    appendLine("${d.donorCode},\"${d.fullName}\",${d.bloodGroup},${d.phone},\"${d.union}\",\"${d.villageAddress}\",${d.verificationStatus.name},${d.availabilityStatus.name},${d.totalDonations},${d.lastDonationDate}")
                }
            }
        } else {
            buildString {
                appendLine("================================================================================")
                appendLine("MANIRAMPUR BLOOD NETWORK - OFFICIAL MEDICAL CAMP EMERGENCY ROSTER")
                appendLine("Generated by: ${currentAdmin.name} (${currentAdmin.role.name}) | Date: $today")
                appendLine("Scope: Union - $selectedUnion | Blood Group - $selectedBloodGroup | Total: ${exportList.size} Donors")
                appendLine("================================================================================")
                appendLine(String.format("%-16s %-20s %-6s %-14s %-15s %-12s", "DONOR CODE", "NAME", "GROUP", "PHONE", "UNION", "STATUS"))
                appendLine("--------------------------------------------------------------------------------")
                exportList.forEach { d ->
                    val statusStr = if (d.availabilityStatus == AvailabilityStatus.AVAILABLE && d.verificationStatus == VerificationStatus.VERIFIED) "AVAILABLE" else d.availabilityStatus.name
                    appendLine(
                        String.format(
                            "%-16s %-20s %-6s %-14s %-15s %-12s",
                            d.donorCode,
                            d.fullName.take(19),
                            d.bloodGroup,
                            d.phone,
                            d.union.take(14),
                            statusStr
                        )
                    )
                }
                appendLine("================================================================================")
                appendLine("Note: Verified records only for emergency medical response and camp coordination.")
                appendLine("System Copyright © Manirampur Blood Network | Developed by Zarif")
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "DATA EXPORT & MEDICAL REPORTING",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = TextPrimary,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
            )
            Text(
                text = "Export donor rosters, CSV databases, and printable emergency medical sheets.",
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
            )
        }

        // Filter Controls Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = DarkCardBg,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "EXPORT CONFIGURATION",
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
                        // Blood Group Selector
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = selectedBloodGroup,
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

                            DropdownMenu(
                                expanded = showBgDropdown,
                                onDismissRequest = { showBgDropdown = false },
                                modifier = Modifier.background(DarkSurfaceElevated)
                            ) {
                                bloodGroupsList.forEach { bg ->
                                    DropdownMenuItem(
                                        text = { Text(bg, color = TextPrimary) },
                                        onClick = {
                                            selectedBloodGroup = bg
                                            showBgDropdown = false
                                        }
                                    )
                                }
                            }
                        }

                        // Union Selector
                        Box(modifier = Modifier.weight(1.3f)) {
                            OutlinedTextField(
                                value = selectedUnion,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Union Scope") },
                                modifier = Modifier.fillMaxWidth(),
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
                                            selectedUnion = un
                                            showUnionDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Format Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { exportFormat = "CSV Format" },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (exportFormat == "CSV Format") CrimsonPrimary else DarkSurfaceElevated
                            )
                        ) {
                            Icon(Icons.Default.TableChart, "CSV", modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("CSV Database", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { exportFormat = "Medical Camp Roster" },
                            modifier = Modifier.weight(1.2f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (exportFormat == "Medical Camp Roster") CrimsonPrimary else DarkSurfaceElevated
                            )
                        ) {
                            Icon(Icons.Default.LocalHospital, "Roster", modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Medical Roster", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quick Action Buttons (Copy, Share, Download)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("MBN Roster Export", generatedReportText)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Copied ${exportList.size} records to clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonDeep)
                        ) {
                            Icon(Icons.Default.ContentCopy, "Copy", modifier = Modifier.size(15.dp), tint = CrimsonGlow)
                            Spacer(Modifier.width(4.dp))
                            Text("Copy Data", fontSize = 11.sp, color = CrimsonGlow, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, "Manirampur Blood Network - $exportFormat")
                                    putExtra(Intent.EXTRA_TEXT, generatedReportText)
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share MBN Donor Roster"))
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary)
                        ) {
                            Icon(Icons.Default.Share, "Share", modifier = Modifier.size(15.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Share File", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Live Generated Preview Box
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
                            text = "LIVE EXPORT PREVIEW (${exportList.size} RECORDS)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextSecondary,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        )

                        Text(
                            text = exportFormat,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = StatusSuccess,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp),
                        color = DarkSurfaceElevated,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = generatedReportText,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    color = TextPrimary,
                                    lineHeight = 14.sp
                                )
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
