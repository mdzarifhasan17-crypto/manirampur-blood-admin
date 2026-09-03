package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AirportShuttle
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AdminProfile
import com.example.data.model.AmbulanceContact
import com.example.data.model.MANIRAMPUR_UNIONS
import com.example.data.model.UnionCoordinator
import com.example.data.model.UserRole
import com.example.ui.components.FooterBranding
import com.example.ui.theme.AccentGold
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DirectoryServicesScreen(
    currentAdmin: AdminProfile,
    selectedUnion: String,
    ambulances: List<AmbulanceContact>,
    unionCoordinators: List<UnionCoordinator>,
    onSaveAmbulance: (AmbulanceContact) -> Unit,
    onDeleteAmbulance: (AmbulanceContact) -> Unit,
    onSaveCoordinator: (UnionCoordinator) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    var isAddingAmbulance by remember { mutableStateOf(false) }
    var editingAmbulance by remember { mutableStateOf<AmbulanceContact?>(null) }

    val tabs = listOf("Ambulance Services", "Union Coordinators", "Emergency Helplines")

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            item {
                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "DIRECTORY & LOCAL SERVICES",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = TextPrimary,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            )
                        )
                        Text(
                            text = "Ambulances, Union Coordinators & Rapid Response Contacts",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
                        )
                    }

                    if (selectedTab == 0 && currentAdmin.role == UserRole.SUPER_ADMIN) {
                        Button(
                            onClick = { isAddingAmbulance = true },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Add, "Add", modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Add Ambulance", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Tabs Selector
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = DarkCardBg,
                    contentColor = CrimsonPrimary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = CrimsonPrimary
                        )
                    },
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(12.dp))
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 11.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == index) CrimsonGlow else TextSecondary
                                )
                            }
                        )
                    }
                }
            }

            // Tab 0: Ambulance Services
            if (selectedTab == 0) {
                items(ambulances, key = { it.id }) { ambulance ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = DarkCardBg,
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(CrimsonDeep)
                                            .border(1.dp, CrimsonPrimary, RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.AirportShuttle,
                                            contentDescription = "Ambulance",
                                            tint = CrimsonGlow,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = ambulance.serviceName,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                color = TextPrimary,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                        Text(
                                            text = "Operator: ${ambulance.operatorName} • ${ambulance.ambulanceType}",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = TextSecondary,
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                }

                                if (ambulance.isAvailable24x7) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(StatusSuccess.copy(alpha = 0.15f))
                                            .border(1.dp, StatusSuccess.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "24/7 ACTIVE",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = StatusSuccess,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.sp
                                            )
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, "Location", tint = CrimsonPrimary, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = "${ambulance.union} • ${ambulance.baseLocation}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = {
                                        val dial = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${ambulance.phone}"))
                                        context.startActivity(dial)
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary)
                                ) {
                                    Icon(Icons.Default.Call, "Call", modifier = Modifier.size(14.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Call ${ambulance.phone}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                if (currentAdmin.role == UserRole.SUPER_ADMIN) {
                                    IconButton(
                                        onClick = { editingAmbulance = ambulance },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(Icons.Default.Edit, "Edit", tint = TextSecondary, modifier = Modifier.size(16.dp))
                                    }

                                    IconButton(
                                        onClick = { onDeleteAmbulance(ambulance) },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, "Delete", tint = StatusCritical, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Tab 1: Union Coordinators
            if (selectedTab == 1) {
                items(unionCoordinators, key = { it.id }) { coord ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = DarkCardBg,
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(DarkSurfaceElevated)
                                            .border(1.dp, StatusWarning, RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Shield,
                                            contentDescription = "Coordinator",
                                            tint = StatusWarning,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "${coord.unionName} Union Lead",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                color = TextPrimary,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                        Text(
                                            text = coord.leadCoordinatorName,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = CrimsonGlow,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 12.sp
                                            )
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(StatusWarning.copy(alpha = 0.15f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                    Text(
                                        text = "${coord.activeVolunteersCount} Volunteers",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = StatusWarning,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Email: ${coord.assignedModeratorEmail}",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        val dial = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${coord.phone}"))
                                        context.startActivity(dial)
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)
                                ) {
                                    Icon(Icons.Default.Call, "Call", modifier = Modifier.size(14.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Lead: ${coord.phone}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = {
                                        val dial = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${coord.emergencyHelpline}"))
                                        context.startActivity(dial)
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonDeep)
                                ) {
                                    Icon(Icons.Default.Phone, "Helpline", modifier = Modifier.size(14.dp), tint = CrimsonGlow)
                                    Spacer(Modifier.width(4.dp))
                                    Text("Helpline", fontSize = 11.sp, color = CrimsonGlow, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Tab 2: Emergency Helplines
            if (selectedTab == 2) {
                val helplines = listOf(
                    Triple("Manirampur Upazila Health Complex", "01730324819", "24/7 Government Hospital & Blood Bank"),
                    Triple("Manirampur Fire Service & Civil Defence", "01712200000", "Emergency Rescue & Disaster Support"),
                    Triple("Manirampur Police Station (Thana)", "01713374244", "Law Enforcement & Highway Escort"),
                    Triple("Jessore 250 Bed Hospital Blood Bank", "01711889900", "Central Regional Blood Transfusion Center"),
                    Triple("National Emergency Service", "999", "Instant Police, Fire & Ambulance Hotline")
                )

                items(helplines) { (name, number, desc) ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = DarkCardBg,
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = name,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = desc,
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
                                )
                                Text(
                                    text = number,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = CrimsonGlow,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 13.sp
                                    ),
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }

                            Button(
                                onClick = {
                                    val dial = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
                                    context.startActivity(dial)
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary)
                            ) {
                                Icon(Icons.Default.Call, "Call", modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Call", fontWeight = FontWeight.Bold, fontSize = 12.sp)
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

    // Add / Edit Ambulance Dialog
    if (isAddingAmbulance || editingAmbulance != null) {
        AmbulanceEditSheet(
            ambulance = editingAmbulance,
            defaultUnion = selectedUnion,
            onDismiss = {
                isAddingAmbulance = false
                editingAmbulance = null
            },
            onSave = {
                onSaveAmbulance(it)
                isAddingAmbulance = false
                editingAmbulance = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AmbulanceEditSheet(
    ambulance: AmbulanceContact?,
    defaultUnion: String,
    onDismiss: () -> Unit,
    onSave: (AmbulanceContact) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var serviceName by remember { mutableStateOf(ambulance?.serviceName ?: "") }
    var operatorName by remember { mutableStateOf(ambulance?.operatorName ?: "") }
    var phone by remember { mutableStateOf(ambulance?.phone ?: "") }
    var union by remember { mutableStateOf(ambulance?.union ?: (if (defaultUnion != "All Unions") defaultUnion else "Rohita")) }
    var ambulanceType by remember { mutableStateOf(ambulance?.ambulanceType ?: "ICU Ambulance") }
    var baseLocation by remember { mutableStateOf(ambulance?.baseLocation ?: "") }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, containerColor = DarkSurface) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 32.dp)) {
            Text(if (ambulance == null) "Add Ambulance Contact" else "Edit Ambulance Contact", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = serviceName, onValueChange = { serviceName = it }, label = { Text("Service Name") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = operatorName, onValueChange = { operatorName = it }, label = { Text("Driver / Operator Name") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone Number") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = baseLocation, onValueChange = { baseLocation = it }, label = { Text("Base Location / Stand") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(14.dp))
            Button(
                onClick = {
                    val updated = (ambulance ?: AmbulanceContact(
                        serviceName = serviceName,
                        operatorName = operatorName,
                        phone = phone,
                        union = union,
                        ambulanceType = ambulanceType,
                        baseLocation = baseLocation
                    )).copy(
                        serviceName = serviceName.ifBlank { "Manirampur Ambulance" },
                        operatorName = operatorName.ifBlank { "Operator" },
                        phone = phone.ifBlank { "01700000000" },
                        union = union,
                        ambulanceType = ambulanceType,
                        baseLocation = baseLocation
                    )
                    onSave(updated)
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary)
            ) {
                Text("SAVE AMBULANCE", fontWeight = FontWeight.Bold)
            }
        }
    }
}
