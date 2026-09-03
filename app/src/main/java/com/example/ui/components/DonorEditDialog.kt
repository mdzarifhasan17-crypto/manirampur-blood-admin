package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Remove
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AvailabilityStatus
import com.example.data.model.Donor
import com.example.data.model.MANIRAMPUR_UNIONS
import com.example.data.model.VerificationStatus
import com.example.ui.theme.BloodRed
import com.example.ui.theme.CrimsonDark
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonorEditDialog(
    donor: Donor?,
    defaultUnion: String?,
    onDismiss: () -> Unit,
    onSave: (Donor) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val isNew = donor == null
    var fullName by remember { mutableStateOf(donor?.fullName ?: "") }
    var phone by remember { mutableStateOf(donor?.phone ?: "") }
    var bloodGroup by remember { mutableStateOf(donor?.bloodGroup ?: "O+") }
    var union by remember {
        mutableStateOf(
            donor?.union ?: (if (defaultUnion != null && defaultUnion != "All Unions") defaultUnion else "Rohita")
        )
    }
    var villageAddress by remember { mutableStateOf(donor?.villageAddress ?: "") }
    var nidOrStudentId by remember { mutableStateOf(donor?.nidOrStudentId ?: "") }
    var lastDonationDate by remember { mutableStateOf(donor?.lastDonationDate ?: "2024-05-15") }
    var totalDonations by remember { mutableIntStateOf(donor?.totalDonations ?: 0) }
    var age by remember { mutableIntStateOf(donor?.age ?: 24) }
    var gender by remember { mutableStateOf(donor?.gender ?: "Male") }
    var verificationStatus by remember {
        mutableStateOf(donor?.verificationStatus ?: VerificationStatus.VERIFIED)
    }
    var availabilityStatus by remember {
        mutableStateOf(donor?.availabilityStatus ?: AvailabilityStatus.AVAILABLE)
    }
    var notes by remember { mutableStateOf(donor?.notes ?: "") }

    var showUnionDropdown by remember { mutableStateOf(false) }
    var showBloodGroupDropdown by remember { mutableStateOf(false) }

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
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isNew) Icons.Default.Add else Icons.Default.Edit,
                        contentDescription = "Edit Donor",
                        tint = CrimsonPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isNew) "Register New Blood Donor" else "Edit Donor Profile (${donor?.donorCode})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Full Name
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Donor Full Name") },
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

            // Phone & Blood Group Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    modifier = Modifier.weight(1.5f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CrimsonPrimary,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )

                // Blood Group Dropdown Field
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = bloodGroup,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Blood Group") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showBloodGroupDropdown = true },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CrimsonPrimary,
                            unfocusedBorderColor = DarkSurfaceBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        trailingIcon = {
                            Text("▼", color = CrimsonPrimary, modifier = Modifier.padding(end = 8.dp))
                        }
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
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Union Selector
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = union,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Manirampur Union Area") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showUnionDropdown = true },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CrimsonPrimary,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    trailingIcon = {
                        Text("▼", color = CrimsonPrimary, modifier = Modifier.padding(end = 8.dp))
                    }
                )

                DropdownMenu(
                    expanded = showUnionDropdown,
                    onDismissRequest = { showUnionDropdown = false },
                    modifier = Modifier.background(DarkSurfaceElevated)
                ) {
                    MANIRAMPUR_UNIONS.filter { it != "All Unions" }.forEach { un ->
                        DropdownMenuItem(
                            text = { Text(un, color = TextPrimary) },
                            onClick = {
                                union = un
                                showUnionDropdown = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Village Address
            OutlinedTextField(
                value = villageAddress,
                onValueChange = { villageAddress = it },
                label = { Text("Village / Ward / Street Address") },
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

            // NID / Student ID
            OutlinedTextField(
                value = nidOrStudentId,
                onValueChange = { nidOrStudentId = it },
                label = { Text("National ID (NID) / Student ID") },
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

            // Donation Stats: Last Donation Date & Increment counter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = lastDonationDate,
                    onValueChange = { lastDonationDate = it },
                    label = { Text("Last Donation (YYYY-MM-DD)") },
                    modifier = Modifier.weight(1.3f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CrimsonPrimary,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )

                // Total Donations Counter with + / -
                Surface(
                    modifier = Modifier.weight(1f),
                    color = DarkSurfaceElevated,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Total: $totalDonations", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { if (totalDonations > 0) totalDonations-- },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.Remove, "-", tint = TextSecondary)
                            }
                            IconButton(
                                onClick = { totalDonations++ },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.Add, "+", tint = CrimsonGlow)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // KYC Status selector
            Text("KYC Verification Status:", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                VerificationStatus.entries.forEach { status ->
                    FilterChip(
                        selected = verificationStatus == status,
                        onClick = { verificationStatus = status },
                        label = { Text(status.name, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = when (status) {
                                VerificationStatus.VERIFIED -> StatusSuccess.copy(alpha = 0.2f)
                                VerificationStatus.PENDING_KYC -> StatusWarning.copy(alpha = 0.2f)
                                VerificationStatus.REJECTED -> StatusCritical.copy(alpha = 0.2f)
                            },
                            selectedLabelColor = when (status) {
                                VerificationStatus.VERIFIED -> StatusSuccess
                                VerificationStatus.PENDING_KYC -> StatusWarning
                                VerificationStatus.REJECTED -> StatusCritical
                            }
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Availability Status selector (including Banned)
            Text("Availability / Account Status:", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AvailabilityStatus.entries.forEach { status ->
                    FilterChip(
                        selected = availabilityStatus == status,
                        onClick = { availabilityStatus = status },
                        label = { Text(status.name.replace("_", " "), fontSize = 10.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = when (status) {
                                AvailabilityStatus.AVAILABLE -> StatusSuccess.copy(alpha = 0.2f)
                                AvailabilityStatus.DONATED_RECENTLY -> StatusWarning.copy(alpha = 0.2f)
                                AvailabilityStatus.INELIGIBLE -> TextSecondary.copy(alpha = 0.2f)
                                AvailabilityStatus.BLOCKED -> StatusCritical.copy(alpha = 0.3f)
                            },
                            selectedLabelColor = when (status) {
                                AvailabilityStatus.AVAILABLE -> StatusSuccess
                                AvailabilityStatus.DONATED_RECENTLY -> StatusWarning
                                AvailabilityStatus.INELIGIBLE -> TextSecondary
                                AvailabilityStatus.BLOCKED -> StatusCritical
                            }
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Notes / Admin Remarks
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Admin Remarks & KYC Notes") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CrimsonPrimary,
                    unfocusedBorderColor = DarkSurfaceBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Save / Submit Button
            Button(
                onClick = {
                    val code = donor?.donorCode ?: "#MNR-BLOOD-${(1000..9999).random()}"
                    val updatedDonor = (donor ?: Donor(
                        donorCode = code,
                        fullName = fullName,
                        phone = phone,
                        bloodGroup = bloodGroup,
                        union = union,
                        villageAddress = villageAddress,
                        nidOrStudentId = nidOrStudentId
                    )).copy(
                        fullName = fullName.ifBlank { "Anonymous Donor" },
                        phone = phone.ifBlank { "01700000000" },
                        bloodGroup = bloodGroup,
                        union = union,
                        villageAddress = villageAddress,
                        nidOrStudentId = nidOrStudentId,
                        lastDonationDate = lastDonationDate,
                        totalDonations = totalDonations,
                        age = age,
                        gender = gender,
                        verificationStatus = verificationStatus,
                        availabilityStatus = availabilityStatus,
                        notes = notes
                    )
                    onSave(updatedDonor)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CrimsonPrimary,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = if (isNew) "SAVE & REGISTER DONOR" else "UPDATE DONOR RECORD",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}
