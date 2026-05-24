package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TransactionEntity
import com.example.ui.components.ComparativeBarChart
import com.example.ui.components.LineChartTrend
import com.example.ui.components.SpendingCard
import com.example.ui.theme.CosmicSlateDark
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.TextMuted
import com.example.ui.theme.ValidGreen
import com.example.ui.theme.WasteRed

@Composable
fun WatchdogApp(
    viewModel: WatchdogViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    
    val pendingContracts by viewModel.pendingTransactions.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val dropBannerMessage by viewModel.notificationMessage.collectAsState()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(CosmicSlateDark),
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .shadow(16.dp)
                    .background(ObsidianSurface),
                containerColor = ObsidianSurface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    label = { Text("Audit Deck", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Card Swipe Deck") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CosmicSlateDark,
                        selectedTextColor = GoldAccent,
                        indicatorColor = GoldAccent,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("nav_deck_tab")
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    label = { Text("Leaderboard", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.List, contentDescription = "Waste Metrics Leaderboard") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CosmicSlateDark,
                        selectedTextColor = GoldAccent,
                        indicatorColor = GoldAccent,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("nav_leaderboard_tab")
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    label = { Text("Analytics", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.Info, contentDescription = "Budget Analytics and Trends") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CosmicSlateDark,
                        selectedTextColor = GoldAccent,
                        indicatorColor = GoldAccent,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("nav_analytics_tab")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CosmicSlateDark)
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> AuditDeckScreen(
                    pending = pendingContracts,
                    isRefreshing = isRefreshing,
                    onSwipe = { tx, approved -> viewModel.handleSwipe(tx, !approved) },
                    onRefresh = { viewModel.syncFromUSAspending() },
                    onReset = { viewModel.resetStats() },
                    onNotifyMockTrigger = { viewModel.dropNewAbsurdContract() }
                )
                1 -> LeaderboardScreen(
                    viewModel = viewModel
                )
                2 -> AnalyticsScreen(
                    viewModel = viewModel
                )
            }

            // Flashing emergency in-app drops card banner alert overlay
            AnimatedVisibility(
                visible = dropBannerMessage != null,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                dropBannerMessage?.let { msg ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.5.dp, GoldAccent, RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Drop Alert Icon",
                                tint = GoldAccent,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = msg,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            IconButton(
                                onClick = { viewModel.clearNotificationBanner() },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close Banner",
                                    tint = Color.White.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AuditDeckScreen(
    pending: List<TransactionEntity>,
    isRefreshing: Boolean,
    onSwipe: (TransactionEntity, Boolean) -> Unit,
    onRefresh: () -> Unit,
    onReset: () -> Unit,
    onNotifyMockTrigger: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "DOGE WATCHDOG",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = GoldAccent,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "Federal Spending Tinder Audit (Live API Integration)",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.5f),
                    fontWeight = FontWeight.SemiBold
                )
            }

            IconButton(
                onClick = onRefresh,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.05f))
                    .border(0.5.dp, Color.White.copy(alpha = 0.1f), CircleShape)
            ) {
                if (isRefreshing) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = GoldAccent)
                } else {
                    Icon(Icons.Default.Refresh, contentDescription = "Sync Live USAspending Data", tint = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            if (pending.isNotEmpty()) {
                val topTx = pending.first()
                val nextTx = if (pending.size > 1) pending[1] else null

                if (nextTx != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxSize(0.96f)
                            .padding(top = 16.dp)
                            .shadow(2.dp, RoundedCornerShape(24.dp)),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = ObsidianSurface.copy(alpha = 0.8f))
                    ) {}
                }

                SpendingCard(
                    transaction = topTx,
                    onSwiped = { approved -> onSwipe(topTx, approved) }
                )
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = ObsidianSurface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "🐕",
                            fontSize = 62.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Batch Audited!",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "DOGE watchdog has evaluated all active loaded items. Swipe records are saved database-wide. Swipe more by refreshing contracts!",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Button(
                            onClick = onRefresh,
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh Icon", tint = CosmicSlateDark)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Fetch USAspending Live Contracts", color = CosmicSlateDark, fontWeight = FontWeight.Black, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onReset,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.05f))
                    .border(0.5.dp, Color.White.copy(alpha = 0.15f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Reset Swipe Status Metrics",
                    tint = TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(32.dp))

            IconButton(
                onClick = {
                    if (pending.isNotEmpty()) {
                        val top = pending.first()
                        onSwipe(top, false)
                    }
                },
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(WasteRed.copy(alpha = 0.15f))
                    .border(1.5.dp, WasteRed, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Swipe Waste Key",
                    tint = WasteRed,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(24.dp))

            IconButton(
                onClick = {
                    if (pending.isNotEmpty()) {
                        val top = pending.first()
                        onSwipe(top, true)
                    }
                },
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(ValidGreen.copy(alpha = 0.15f))
                    .border(1.5.dp, ValidGreen, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = "Swipe Valid Key",
                    tint = ValidGreen,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(32.dp))

            IconButton(
                onClick = onNotifyMockTrigger,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(GoldAccent.copy(alpha = 0.15f))
                    .border(1.4.dp, GoldAccent, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Simulate New Absurd Spending Drop Alert",
                    tint = GoldAccent,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LeaderboardScreen(
    viewModel: WatchdogViewModel
) {
    val rankingList by viewModel.agencyLeaderboard.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        contentPadding = WindowInsets.navigationBars.asPaddingValues()
    ) {
        item {
            Column(modifier = Modifier.padding(vertical = 12.dp)) {
                Text(
                    text = "AGENCY WASTE LEADERBOARD",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = GoldAccent,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "Ranked by percentage of 'WASTE' votes derived from public audits.",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.5f)
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        if (rankingList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No agencies recorded. Swipe on cards first to seed logs!", color = TextMuted)
                }
            }
        } else {
            items(rankingList) { row ->
                val ratingCategory = when {
                    row.wastePercentage >= 75f -> "⚠️ CRITICAL WASTE EXPOSURE"
                    row.wastePercentage >= 50f -> "🛑 MASSIVE LEAKAGE SUSPECTED"
                    row.wastePercentage >= 35f -> "📊 UNDER DIRECT DOGE INVESTIGATION"
                    else -> "✅ MODERATELY DISCIPLINED"
                }

                val rowColor = when {
                    row.wastePercentage >= 50f -> WasteRed
                    else -> GoldAccent
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .animateItemPlacement()
                        .testTag("leaderboard_agency_row_${row.agencyName}"),
                    colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 0.5.dp,
                        color = Color.White.copy(alpha = 0.08f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = row.agencyName,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "%.0f%% WASTE".format(row.wastePercentage),
                                color = rowColor,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color.White.copy(alpha = 0.04f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth((row.wastePercentage / 100f).coerceIn(0f, 1f))
                                    .fillMaxHeight()
                                    .background(rowColor)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = ratingCategory,
                                color = if (row.wastePercentage >= 50f) WasteRed.copy(alpha = 0.8f) else GoldAccent.copy(alpha = 0.8f),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Spent tracked: $%,.0fB".format(row.totalSpent / 1_000_000_000.0),
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnalyticsScreen(
    viewModel: WatchdogViewModel
) {
    val histories = viewModel.historicalTrends
    var selectedDeptIndex by remember { mutableIntStateOf(0) }
    val currentDept = histories[selectedDeptIndex]

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        contentPadding = WindowInsets.navigationBars.asPaddingValues()
    ) {
        item {
            Column(modifier = Modifier.padding(vertical = 12.dp)) {
                Text(
                    text = "FISCAL TRANSPARENCY",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = GoldAccent,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "Tracking total budget envelopes to promote public accountability.",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.5f)
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(histories.size) { idx ->
                    val dept = histories[idx]
                    val isSelected = selectedDeptIndex == idx
                    val deptColor = Color(android.graphics.Color.parseColor(dept.colorHex))

                    Box(
                        modifier = Modifier
                            .background(
                                if (isSelected) deptColor.copy(alpha = 0.12f) else ObsidianSurface,
                                RoundedCornerShape(12.dp)
                            )
                            .border(
                                1.dp,
                                if (isSelected) deptColor else Color.White.copy(alpha = 0.08f),
                                RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                selectedDeptIndex = idx
                            }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = dept.deptName.replace("Dept of ", ""),
                            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.5f),
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            LineChartTrend(
                selectedDept = currentDept
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.02f), RoundedCornerShape(16.dp))
                    .border(0.5.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "💡 SCALE TRUTH PANEL",
                        color = GoldAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "While focus goes to small absurd grants, the real structural drivers are major agencies. HHS ($1.9 Trillion) exceeds entire aerospace research programs at NASA by over 73x. Real tracking promotes structural clarity.",
                        color = Color.White.copy(alpha = 0.65f),
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            ComparativeBarChart(
                allDepts = histories
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
