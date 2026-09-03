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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AdminProfile
import com.example.data.model.Donor
import com.example.data.model.UserRole
import com.example.data.model.VerificationStatus
import com.example.ui.components.DonorEditDialog
import com.example.ui.components.DonorItemCard
import com.example.ui.components.FooterBranding
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

@Composable
fun DonorManagementScreen(
    currentAdmin: AdminProfile,
    selectedUnion: String,
    donors: List<Donor>,
    searchQuery: String,
    bloodGroupFilter: String?,
    statusFilter: String?,
    onSearchQueryChange: (String) -> Unit,
    onBloodGroupFilterChange: (String?) -> Unit,
    onStatusFilterChange: (String?) -> Unit,
    onSaveDonor: (Donor) -> Unit,
    onVerifyDonor: (Donor, VerificationStatus) -> Unit,
    onToggleBanDonor: (Donor) -> Unit,
    onRecordDonation: (Donor) -> Unit,
    onDeleteDonor: (Donor) -> Unit,
    modifier: Modifier = Modifier
) {
    var editingDonor by remember { mutableStateOf<Donor?>(null) }
    var isCreatingNewDonor by remember { mutableStateOf(false) }

    val bloodGroupsList = listOf("All", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
    val statusFiltersList = listOf(
        "All" to "All Donors",
        "AVAILABLE" to "Available Now",
        "PENDING_KYC" to "Pending KYC",
        "DONATED_RECENTLY" to "Donated Recently",
        "BLOCKED" to "Banned / Blocked"
    )

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header & Counter
            item {
                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "DONOR DIRECTORY & KYC",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = TextPrimary,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            )
                        )
                        Text(
                            text = "Showing ${donors.size} registered donors in $selectedUnion",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
                        )
                    }

                    Button(
                        onClick = { isCreatingNewDonor = true },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.PersonAdd, "Add", modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Add Donor", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Search Bar (Phone, Donor ID #MNR-BLOOD-XXXX, Name, Union)
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text("Search by Phone, ID (#MNR-BLOOD-XXXX), Name, Union...", color = TextSecondary, fontSize = 12.sp)
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = CrimsonPrimary)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondary)
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CrimsonPrimary,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = DarkCardBg,
                        unfocusedContainerColor = DarkCardBg
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            // Blood Group Filter Chips (Horizontal Scroll)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    bloodGroupsList.forEach { bg ->
                        val isSelected = (bloodGroupFilter == null && bg == "All") || bloodGroupFilter == bg
                        FilterChip(
                            selected = isSelected,
                            onClick = { onBloodGroupFilterChange(if (bg == "All") null else bg) },
                            label = { Text(bg, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CrimsonPrimary,
                                selectedLabelColor = Color.White,
                                containerColor = DarkSurfaceElevated,
                                labelColor = TextSecondary
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            }

            // Status Filter Chips (Available, Pending KYC, Donated Recently, Blocked)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    statusFiltersList.forEach { (key, label) ->
                        val isSelected = (statusFilter == null && key == "All") || statusFilter == key
                        FilterChip(
                            selected = isSelected,
                            onClick = { onStatusFilterChange(if (key == "All") null else key) },
                            label = { Text(label, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = when (key) {
                                    "AVAILABLE" -> StatusSuccess
                                    "PENDING_KYC" -> StatusWarning
                                    "BLOCKED" -> StatusCritical
                                    else -> CrimsonPrimary
                                },
                                selectedLabelColor = if (key == "PENDING_KYC") Color.Black else Color.White,
                                containerColor = DarkSurfaceElevated,
                                labelColor = TextSecondary
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            }

            // Empty State
            if (donors.isEmpty()) {
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
                            Icon(Icons.Default.Group, "No Donors", tint = TextSecondary, modifier = Modifier.size(48.dp))
                            Spacer(Modifier.height(10.dp))
                            Text("No Donors Found", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Try adjusting your search query, union scope, or status filters.", color = TextSecondary, fontSize = 12.sp)
                        }
                    }
                }
            } else {
                // Donor Cards List
                items(donors, key = { it.id }) { donor ->
                    DonorItemCard(
                        donor = donor,
                        userRole = currentAdmin.role,
                        onEdit = { editingDonor = it },
                        onVerify = onVerifyDonor,
                        onToggleBan = onToggleBanDonor,
                        onRecordDonation = onRecordDonation,
                        onDelete = onDeleteDonor
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

    // Modal Edit / Add Sheet
    if (editingDonor != null || isCreatingNewDonor) {
        DonorEditDialog(
            donor = editingDonor,
            defaultUnion = selectedUnion,
            onDismiss = {
                editingDonor = null
                isCreatingNewDonor = false
            },
            onSave = { updated ->
                onSaveDonor(updated)
                editingDonor = null
                isCreatingNewDonor = false
            }
        )
    }
}
