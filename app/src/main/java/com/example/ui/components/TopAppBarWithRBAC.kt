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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AdminProfile
import com.example.data.model.MANIRAMPUR_UNIONS
import com.example.data.model.UserRole
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

@Composable
fun TopAppBarWithRBAC(
    currentAdmin: AdminProfile,
    selectedUnion: String,
    onUnionSelected: (String) -> Unit,
    onSwitchRole: (AdminProfile) -> Unit,
    onTriggerBroadcast: () -> Unit,
    onOpenSecurityInfo: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showRoleDialog by remember { mutableStateOf(false) }
    var showUnionDropdown by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        color = DarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Main App Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Brand Title & Logo
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.radialGradient(
                                    listOf(CrimsonPrimary, CrimsonDeep)
                                )
                            )
                            .border(1.dp, CrimsonGlow.copy(alpha = 0.6f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bloodtype,
                            contentDescription = "Blood Network Icon",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "MBN CONTROL CENTER",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary,
                                    letterSpacing = 0.5.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(StatusCritical.copy(alpha = 0.2f))
                                    .border(1.dp, StatusCritical.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "ADMIN",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = StatusCritical,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    )
                                )
                            }
                        }
                        Text(
                            text = "Manirampur Upazila Blood Network",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                // Quick Broadcast & RBAC Profile actions
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Emergency Broadcast Shortcut
                    IconButton(
                        onClick = onTriggerBroadcast,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(CrimsonPrimary.copy(alpha = 0.2f))
                            .border(1.dp, CrimsonPrimary, RoundedCornerShape(10.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = "Quick Push Broadcast",
                            tint = CrimsonGlow,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Role Switcher Avatar Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkSurfaceElevated)
                            .border(1.dp, if (currentAdmin.role == UserRole.SUPER_ADMIN) CrimsonPrimary else StatusWarning, RoundedCornerShape(10.dp))
                            .clickable { showRoleDialog = true }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = if (currentAdmin.role == UserRole.SUPER_ADMIN) Icons.Default.AdminPanelSettings else Icons.Default.Shield,
                                contentDescription = "Role",
                                tint = if (currentAdmin.role == UserRole.SUPER_ADMIN) CrimsonGlow else StatusWarning,
                                modifier = Modifier.size(16.dp)
                            )
                            Column {
                                Text(
                                    text = currentAdmin.name.take(12),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = if (currentAdmin.role == UserRole.SUPER_ADMIN) "Super Admin" else "${currentAdmin.assignedUnion} Mod",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (currentAdmin.role == UserRole.SUPER_ADMIN) CrimsonGlow else StatusWarning,
                                        fontSize = 9.sp
                                    )
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = "Switch Role",
                                tint = TextSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Union Scope & RBAC Status Filter Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkSurfaceElevated)
                    .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Current Union / Scope
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        if (currentAdmin.role == UserRole.SUPER_ADMIN) {
                            showUnionDropdown = true
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Union Location",
                        tint = CrimsonPrimary,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Scope: ",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                    )
                    Text(
                        text = if (currentAdmin.role == UserRole.SUPER_ADMIN) selectedUnion else "${currentAdmin.assignedUnion} (Restricted)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (currentAdmin.role == UserRole.SUPER_ADMIN) TextPrimary else StatusWarning,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    if (currentAdmin.role == UserRole.SUPER_ADMIN) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "▼",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontSize = 8.sp)
                        )

                        DropdownMenu(
                            expanded = showUnionDropdown,
                            onDismissRequest = { showUnionDropdown = false },
                            modifier = Modifier.background(DarkSurfaceElevated)
                        ) {
                            MANIRAMPUR_UNIONS.forEach { union ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = union,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = if (union == selectedUnion) CrimsonPrimary else TextPrimary,
                                                fontWeight = if (union == selectedUnion) FontWeight.Bold else FontWeight.Normal
                                            )
                                        )
                                    },
                                    onClick = {
                                        onUnionSelected(union)
                                        showUnionDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Security & Token indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onOpenSecurityInfo() }
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(StatusSuccess)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "JWT VALID",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = StatusSuccess,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Security Blueprint",
                        tint = TextSecondary,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }
    }

    // Role Switcher Dialog for demonstration of RBAC
    if (showRoleDialog) {
        AlertDialog(
            onDismissRequest = { showRoleDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = "RBAC Switcher",
                        tint = CrimsonPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Role-Based Access (RBAC) Demo", color = TextPrimary)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Select an account to test permissions in real-time. Super Admin has full Upazila control; Union Moderators are restricted to their assigned union.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )

                    // Super Admin Option
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSwitchRole(
                                    AdminProfile(
                                        id = "ADM-01",
                                        name = "Zarif Hasan",
                                        email = "zarifhasan216@gmail.com",
                                        role = UserRole.SUPER_ADMIN,
                                        assignedUnion = null
                                    )
                                )
                                showRoleDialog = false
                            },
                        color = if (currentAdmin.role == UserRole.SUPER_ADMIN) CrimsonDeep.copy(alpha = 0.5f) else DarkSurfaceElevated,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (currentAdmin.role == UserRole.SUPER_ADMIN) CrimsonPrimary else DarkSurfaceBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.AdminPanelSettings, "Super Admin", tint = CrimsonGlow)
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text("Zarif Hasan (Super Admin)", color = TextPrimary, fontWeight = FontWeight.Bold)
                                Text("zarifhasan216@gmail.com • Full Upazila Access", color = TextSecondary, fontSize = 11.sp)
                            }
                        }
                    }

                    // Rohita Moderator Option
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSwitchRole(
                                    AdminProfile(
                                        id = "MOD-02",
                                        name = "Tanvir Ahmed",
                                        email = "tanvir.rohita@mbn.org",
                                        role = UserRole.UNION_MODERATOR,
                                        assignedUnion = "Rohita"
                                    )
                                )
                                showRoleDialog = false
                            },
                        color = if (currentAdmin.assignedUnion == "Rohita") CrimsonDeep.copy(alpha = 0.5f) else DarkSurfaceElevated,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (currentAdmin.assignedUnion == "Rohita") StatusWarning else DarkSurfaceBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Shield, "Rohita Moderator", tint = StatusWarning)
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text("Tanvir Ahmed (Rohita Moderator)", color = TextPrimary, fontWeight = FontWeight.Bold)
                                Text("Restricted to Rohita Union Donors & Requests", color = TextSecondary, fontSize = 11.sp)
                            }
                        }
                    }

                    // Bhojna Moderator Option
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSwitchRole(
                                    AdminProfile(
                                        id = "MOD-03",
                                        name = "Sakib Hossain",
                                        email = "sakib.bhojna@mbn.org",
                                        role = UserRole.UNION_MODERATOR,
                                        assignedUnion = "Bhojna"
                                    )
                                )
                                showRoleDialog = false
                            },
                        color = if (currentAdmin.assignedUnion == "Bhojna") CrimsonDeep.copy(alpha = 0.5f) else DarkSurfaceElevated,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (currentAdmin.assignedUnion == "Bhojna") StatusWarning else DarkSurfaceBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Shield, "Bhojna Moderator", tint = StatusWarning)
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text("Sakib Hossain (Bhojna Moderator)", color = TextPrimary, fontWeight = FontWeight.Bold)
                                Text("Restricted to Bhojna Union Donors & Requests", color = TextSecondary, fontSize = 11.sp)
                            }
                        }
                    }

                    // Nehalpur Moderator Option
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSwitchRole(
                                    AdminProfile(
                                        id = "MOD-04",
                                        name = "Rafiqul Islam",
                                        email = "rafiq.nehalpur@mbn.org",
                                        role = UserRole.UNION_MODERATOR,
                                        assignedUnion = "Nehalpur"
                                    )
                                )
                                showRoleDialog = false
                            },
                        color = if (currentAdmin.assignedUnion == "Nehalpur") CrimsonDeep.copy(alpha = 0.5f) else DarkSurfaceElevated,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (currentAdmin.assignedUnion == "Nehalpur") StatusWarning else DarkSurfaceBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Shield, "Nehalpur Moderator", tint = StatusWarning)
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text("Rafiqul Islam (Nehalpur Moderator)", color = TextPrimary, fontWeight = FontWeight.Bold)
                                Text("Restricted to Nehalpur Union Donors & Requests", color = TextSecondary, fontSize = 11.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showRoleDialog = false }) {
                    Text("Close", color = CrimsonPrimary)
                }
            },
            containerColor = DarkCardBg
        )
    }
}
