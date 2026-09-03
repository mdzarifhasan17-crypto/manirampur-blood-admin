package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Emergency
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.data.model.Donor
import com.example.data.model.EmergencyRequest
import com.example.data.model.RequestStatus
import com.example.data.model.UrgencyLevel
import com.example.ui.components.BroadcastDialog
import com.example.ui.components.DonorEditDialog
import com.example.ui.components.TopAppBarWithRBAC
import com.example.ui.screens.ArchitectureSecurityScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DirectoryServicesScreen
import com.example.ui.screens.DonorManagementScreen
import com.example.ui.screens.EmergencyControlScreen
import com.example.ui.screens.ExportReportsScreen
import com.example.ui.theme.BloodRed
import com.example.ui.theme.CrimsonDark
import com.example.ui.theme.CrimsonDeep
import com.example.ui.theme.CrimsonGlow
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.DarkCardBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.StatusCritical
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AdminViewModel

enum class AdminNavigationDestination(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    DASHBOARD("dashboard", "Hub", Icons.Filled.Dashboard, Icons.Outlined.Dashboard),
    DONORS("donors", "Donors", Icons.Filled.Group, Icons.Outlined.Group),
    EMERGENCY("emergency", "Alerts", Icons.Filled.Emergency, Icons.Outlined.Emergency),
    SERVICES("services", "Services", Icons.Filled.LocalHospital, Icons.Outlined.LocalHospital),
    SECURITY("security", "Security", Icons.Filled.Security, Icons.Outlined.Security),
    EXPORT("export", "Export", Icons.Filled.Description, Icons.Outlined.Description)
}

class MainActivity : ComponentActivity() {
    private val viewModel: AdminViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAdminApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAdminApp(viewModel: AdminViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: AdminNavigationDestination.DASHBOARD.route

    val currentAdmin by viewModel.currentAdmin.collectAsState()
    val selectedUnion by viewModel.selectedUnion.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()

    val allDonors by viewModel.allDonors.collectAsState()
    val filteredDonors by viewModel.filteredDonors.collectAsState()
    val allRequests by viewModel.allEmergencyRequests.collectAsState()
    val filteredRequests by viewModel.filteredEmergencyRequests.collectAsState()
    val ambulances by viewModel.allAmbulances.collectAsState()
    val unionCoordinators by viewModel.allUnionCoordinators.collectAsState()
    val broadcastLogs by viewModel.allBroadcastLogs.collectAsState()

    val donorSearchQuery by viewModel.donorSearchQuery.collectAsState()
    val donorBloodGroupFilter by viewModel.donorBloodGroupFilter.collectAsState()
    val donorStatusFilter by viewModel.donorStatusFilter.collectAsState()
    val requestUrgencyFilter by viewModel.requestUrgencyFilter.collectAsState()
    val requestStatusFilter by viewModel.requestStatusFilter.collectAsState()

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    var isGlobalBroadcastOpen by remember { mutableStateOf(false) }
    var isAddingNewDonorOpen by remember { mutableStateOf(false) }

    val pendingEmergencyCount = allRequests.count {
        it.urgencyLevel == UrgencyLevel.CRITICAL && it.status != RequestStatus.RESOLVED
    }

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearUserMessage()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DarkSurface,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBarWithRBAC(
                currentAdmin = currentAdmin,
                selectedUnion = selectedUnion,
                onUnionSelected = { viewModel.setSelectedUnion(it) },
                onSwitchRole = { viewModel.switchAdminProfile(it) },
                onTriggerBroadcast = { isGlobalBroadcastOpen = true },
                onOpenSecurityInfo = {
                    navController.navigate(AdminNavigationDestination.SECURITY.route) {
                        launchSingleTop = true
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                color = DarkCardBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
            ) {
                NavigationBar(
                    containerColor = Color.Transparent,
                    tonalElevation = 0.dp,
                    modifier = Modifier.height(64.dp)
                ) {
                    AdminNavigationDestination.entries.forEach { destination ->
                        val isSelected = currentRoute == destination.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (currentRoute != destination.route) {
                                    navController.navigate(destination.route) {
                                        popUpTo(AdminNavigationDestination.DASHBOARD.route) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                if (destination == AdminNavigationDestination.EMERGENCY && pendingEmergencyCount > 0) {
                                    BadgedBox(
                                        badge = {
                                            Badge(
                                                containerColor = StatusCritical,
                                                contentColor = Color.White
                                            ) {
                                                Text(
                                                    text = "$pendingEmergencyCount",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                                            contentDescription = destination.title
                                        )
                                    }
                                } else {
                                    Icon(
                                        imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                                        contentDescription = destination.title
                                    )
                                }
                            },
                            label = {
                                Text(
                                    text = destination.title,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = CrimsonGlow,
                                selectedTextColor = CrimsonGlow,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextSecondary,
                                indicatorColor = CrimsonDeep.copy(alpha = 0.6f)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AdminNavigationDestination.DASHBOARD.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(AdminNavigationDestination.DASHBOARD.route) {
                DashboardScreen(
                    currentAdmin = currentAdmin,
                    selectedUnion = selectedUnion,
                    donors = filteredDonors,
                    requests = filteredRequests,
                    onNavigateToDonors = {
                        navController.navigate(AdminNavigationDestination.DONORS.route)
                    },
                    onNavigateToRequests = {
                        navController.navigate(AdminNavigationDestination.EMERGENCY.route)
                    },
                    onNavigateToExport = {
                        navController.navigate(AdminNavigationDestination.EXPORT.route)
                    },
                    onQuickBroadcast = { isGlobalBroadcastOpen = true },
                    onAddNewDonor = { isAddingNewDonorOpen = true },
                    onAddNewRequest = {
                        navController.navigate(AdminNavigationDestination.EMERGENCY.route)
                    }
                )
            }

            composable(AdminNavigationDestination.DONORS.route) {
                DonorManagementScreen(
                    currentAdmin = currentAdmin,
                    selectedUnion = selectedUnion,
                    donors = filteredDonors,
                    searchQuery = donorSearchQuery,
                    bloodGroupFilter = donorBloodGroupFilter,
                    statusFilter = donorStatusFilter,
                    onSearchQueryChange = { viewModel.setDonorSearchQuery(it) },
                    onBloodGroupFilterChange = { viewModel.setDonorBloodGroupFilter(it) },
                    onStatusFilterChange = { viewModel.setDonorStatusFilter(it) },
                    onSaveDonor = { viewModel.saveDonor(it) },
                    onVerifyDonor = { donor, status -> viewModel.updateDonorVerification(donor, status) },
                    onToggleBanDonor = { viewModel.toggleDonorBan(it) },
                    onRecordDonation = { viewModel.recordDonation(it) },
                    onDeleteDonor = { viewModel.deleteDonor(it) }
                )
            }

            composable(AdminNavigationDestination.EMERGENCY.route) {
                EmergencyControlScreen(
                    currentAdmin = currentAdmin,
                    selectedUnion = selectedUnion,
                    requests = filteredRequests,
                    broadcastLogs = broadcastLogs,
                    availableDonors = allDonors,
                    urgencyFilter = requestUrgencyFilter,
                    statusFilter = requestStatusFilter,
                    onUrgencyFilterChange = { viewModel.setRequestUrgencyFilter(it) },
                    onStatusFilterChange = { viewModel.setRequestStatusFilter(it) },
                    onSaveRequest = { viewModel.saveEmergencyRequest(it) },
                    onApproveRequest = { viewModel.approveEmergencyRequest(it) },
                    onResolveRequest = { viewModel.resolveEmergencyRequest(it) },
                    onRejectFakeRequest = { viewModel.rejectFakeRequest(it) },
                    onDeleteRequest = { viewModel.deleteEmergencyRequest(it) },
                    onTriggerBroadcast = { bg, union, title, msg, reqId ->
                        viewModel.triggerPushBroadcast(bg, union, title, msg, reqId)
                    }
                )
            }

            composable(AdminNavigationDestination.SERVICES.route) {
                DirectoryServicesScreen(
                    currentAdmin = currentAdmin,
                    selectedUnion = selectedUnion,
                    ambulances = ambulances,
                    unionCoordinators = unionCoordinators,
                    onSaveAmbulance = { viewModel.saveAmbulance(it) },
                    onDeleteAmbulance = { viewModel.deleteAmbulance(it) },
                    onSaveCoordinator = { viewModel.saveUnionCoordinator(it) }
                )
            }

            composable(AdminNavigationDestination.SECURITY.route) {
                ArchitectureSecurityScreen()
            }

            composable(AdminNavigationDestination.EXPORT.route) {
                ExportReportsScreen(
                    currentAdmin = currentAdmin,
                    allDonors = allDonors
                )
            }
        }
    }

    // Global Quick Push Broadcast Dialog
    if (isGlobalBroadcastOpen) {
        val matchingCount = allDonors.count {
            (selectedUnion == "All Unions" || it.union.equals(selectedUnion, ignoreCase = true)) &&
                    it.verificationStatus == com.example.data.model.VerificationStatus.VERIFIED &&
                    it.availabilityStatus == com.example.data.model.AvailabilityStatus.AVAILABLE
        }

        BroadcastDialog(
            request = null,
            matchingDonorsCount = matchingCount,
            onDismiss = { isGlobalBroadcastOpen = false },
            onSendBroadcast = { bg, union, title, msg ->
                viewModel.triggerPushBroadcast(bg, union, title, msg, null)
                isGlobalBroadcastOpen = false
            }
        )
    }

    // Global Add Donor Dialog
    if (isAddingNewDonorOpen) {
        DonorEditDialog(
            donor = null,
            defaultUnion = selectedUnion,
            onDismiss = { isAddingNewDonorOpen = false },
            onSave = {
                viewModel.saveDonor(it)
                isAddingNewDonorOpen = false
            }
        )
    }
}
