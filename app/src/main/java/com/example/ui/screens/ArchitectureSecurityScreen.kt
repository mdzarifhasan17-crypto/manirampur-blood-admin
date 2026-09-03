package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Http
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.ui.components.FooterBranding
import com.example.ui.theme.AccentGold
import com.example.ui.theme.CrimsonDeep
import com.example.ui.theme.CrimsonGlow
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.DarkCardBg
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ArchitectureSecurityScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Security Rules", "API Endpoints", "RBAC Schema")

    val firebaseRulesCode = """
// ============================================================================
// MANIRAMPUR BLOOD NETWORK - PRODUCTION DATABASE SECURITY RULES
// Firebase Firestore Security Rules (RBAC with Custom JWT Claims)
// ============================================================================

rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {

    // Helper functions for Role-Based Access Control (RBAC)
    function isAuthenticated() {
      return request.auth != null;
    }

    function isSuperAdmin() {
      return isAuthenticated() && request.auth.token.role == 'SUPER_ADMIN';
    }

    function isUnionModerator(union) {
      return isAuthenticated() && 
        (request.auth.token.role == 'UNION_MODERATOR' && request.auth.token.assignedUnion == union);
    }

    function isAuthorizedForUnion(union) {
      return isSuperAdmin() || isUnionModerator(union);
    }

    // 1. Donors Collection
    match /donors/{donorId} {
      // Public / verified users can read donor listings
      allow read: if isAuthenticated();

      // Only Super Admin or the Union Moderator of that specific union can modify or KYC verify
      allow create: if isAuthenticated();
      allow update: if isAuthorizedForUnion(resource.data.union);
      allow delete: if isSuperAdmin(); // Super Admin ONLY
    }

    // 2. Emergency Blood Requests Collection
    match /emergency_requests/{requestId} {
      allow read: if true; // Public read for rapid emergency response
      allow create: if isAuthenticated();
      
      // Approve, resolve, or flag fake requests
      allow update: if isAuthorizedForUnion(resource.data.union);
      allow delete: if isSuperAdmin();
    }

    // 3. One-Click Push Broadcast Dispatch Logs
    match /broadcast_logs/{logId} {
      allow read: if isAuthenticated() && (isSuperAdmin() || request.auth.token.role == 'UNION_MODERATOR');
      allow create: if isAuthenticated() && (isSuperAdmin() || request.auth.token.role == 'UNION_MODERATOR');
      allow update, delete: if false; // Immutable audit logs
    }

    // 4. Directory & Ambulances
    match /ambulances/{ambulanceId} {
      allow read: if true;
      allow write: if isSuperAdmin();
    }
  }
}
""".trimIndent()

    val postgreSqlRlsCode = """
-- ============================================================================
-- POSTGRESQL ROW LEVEL SECURITY (RLS) POLICIES FOR SUPABASE / CLOUD SQL
-- ============================================================================

-- Enable RLS on donors table
ALTER TABLE donors ENABLE ROW LEVEL SECURITY;

-- 1. Super Admin Full Access Policy
CREATE POLICY "Super Admins have full access" ON donors
  FOR ALL
  TO authenticated
  USING (auth.jwt() ->> 'role' = 'SUPER_ADMIN')
  WITH CHECK (auth.jwt() ->> 'role' = 'SUPER_ADMIN');

-- 2. Union Moderator Scope Restriction Policy
CREATE POLICY "Union Moderators can only manage their union" ON donors
  FOR UPDATE
  TO authenticated
  USING (
    auth.jwt() ->> 'role' = 'UNION_MODERATOR' AND 
    union_name = (auth.jwt() ->> 'assigned_union')
  )
  WITH CHECK (
    auth.jwt() ->> 'role' = 'UNION_MODERATOR' AND 
    union_name = (auth.jwt() ->> 'assigned_union')
  );

-- 3. Public Authenticated Read Policy
CREATE POLICY "Authenticated users can view verified donors" ON donors
  FOR SELECT
  TO authenticated
  USING (verification_status = 'VERIFIED' AND availability_status != 'BLOCKED');
""".trimIndent()

    val apiEndpointsSpec = """
// ============================================================================
// MANIRAMPUR BLOOD NETWORK - RESTful ADMIN & BROADCAST API SPECIFICATION
// Base URL: https://api.manirampurblood.org/v1/admin
// Header: Authorization: Bearer <JWT_TOKEN>
// ============================================================================

1. TRIGGER ONE-CLICK PUSH NOTIFICATION BROADCAST
POST /notifications/broadcast
Headers:
  Authorization: Bearer <SUPER_ADMIN_OR_MODERATOR_TOKEN>
  Content-Type: application/json
Body:
{
  "target_blood_group": "O+",
  "target_union": "Rohita", // or "ALL"
  "title": "🚨 URGENT: O+ Blood Needed at Upazila Health Complex",
  "message": "Patient: Abdul Karim (Accident Trauma). 2 Bags needed. Call 01712-345678.",
  "priority": "HIGH",
  "associated_request_id": "EMR-2024-108"
}
Response 200 OK:
{
  "status": "success",
  "recipients_matched": 18,
  "fcm_message_ids": ["msg_09812", "msg_09813"],
  "dispatch_timestamp": "2024-05-18T14:32:00Z"
}

--------------------------------------------------------------------------------

2. KYC VERIFICATION / DONOR STATUS MANAGEMENT
PATCH /donors/{donor_code}/kyc-status
Headers:
  Authorization: Bearer <JWT_TOKEN>
Body:
{
  "verification_status": "VERIFIED", // "VERIFIED" | "PENDING_KYC" | "REJECTED"
  "admin_remarks": "NID #199841289128 verified by Rohita Union Moderator Tanvir Ahmed."
}
Response 200 OK:
{
  "status": "updated",
  "donor_code": "#MNR-BLOOD-1042",
  "verified_at": "2024-05-18T14:35:10Z"
}

--------------------------------------------------------------------------------

3. RECORD BLOOD DONATION
POST /donors/{donor_code}/donations
Body:
{
  "donation_date": "2024-05-18",
  "location": "Manirampur Sadar Hospital",
  "patient_request_code": "#EMR-2024-108"
}
Response 200 OK:
{
  "status": "success",
  "total_donations": 4,
  "next_eligible_date": "2024-08-18"
}

--------------------------------------------------------------------------------

4. BAN / BLOCK FRAUDULENT ACCOUNT
POST /donors/{donor_code}/ban
Body:
{
  "reason": "Repeated fake reports / unauthorized number misuse",
  "banned_by": "Zarif Hasan (Super Admin)"
}
""".trimIndent()

    val rbacSchemaSpec = """
================================================================================
ROLE-BASED ACCESS CONTROL (RBAC) MODEL & JWT CLAIMS
================================================================================

1. SUPER ADMIN (e.g. Zarif Hasan - zarifhasan216@gmail.com)
  - Scope: All 16 Unions of Manirampur Upazila
  - Capabilities:
    * Full database read/write/delete
    * Ban/unban any donor or moderator
    * Add/edit/remove emergency ambulances & services
    * Dispatch Upazila-wide push broadcast alerts
    * Assign moderators to union branches

2. UNION MODERATOR (e.g. Rohita, Bhojna, Nehalpur leads)
  - Scope: Restricted strictly to their assigned union
  - Capabilities:
    * KYC verify local donors in their union
    * Update local donor details & donation records
    * Approve / resolve local emergency requests
    * Dispatch localized push broadcast to their union donors
    * [DENIED]: Cannot delete database records or access other unions

JWT Token Payload Example:
{
  "sub": "user_admin_0918",
  "name": "Zarif Hasan",
  "email": "zarifhasan216@gmail.com",
  "role": "SUPER_ADMIN",
  "assigned_union": null,
  "permissions": ["all:read", "all:write", "all:delete", "broadcast:global"],
  "exp": 1747800000
}
""".trimIndent()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "SECURITY & API ARCHITECTURE BLUEPRINT",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = TextPrimary,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
            )
            Text(
                text = "Database rules, RBAC JWT specification, and Push Notification API endpoints.",
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
            )
        }

        // Tab Selector
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

        // Tab Content
        item {
            val contentToDisplay = when (selectedTab) {
                0 -> "$firebaseRulesCode\n\n$postgreSqlRlsCode"
                1 -> apiEndpointsSpec
                else -> rbacSchemaSpec
            }

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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = when (selectedTab) {
                                    0 -> Icons.Default.Security
                                    1 -> Icons.Default.Api
                                    else -> Icons.Default.Shield
                                },
                                contentDescription = "Security",
                                tint = CrimsonGlow,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = tabs[selectedTab].uppercase(),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        Button(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("MBN Security Spec", contentToDisplay)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Copied specification code to clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonDeep),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, "Copy", modifier = Modifier.size(14.dp), tint = CrimsonGlow)
                            Spacer(Modifier.width(4.dp))
                            Text("Copy Code", fontSize = 10.sp, color = CrimsonGlow, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(380.dp),
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
                                text = contentToDisplay,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    color = TextPrimary,
                                    lineHeight = 15.sp
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
