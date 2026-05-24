package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TransactionEntity
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.ValidGreen
import com.example.ui.theme.WasteRed
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.min

@Composable
fun SpendingCard(
    transaction: TransactionEntity,
    onSwiped: (Boolean) -> Unit, // true = approved/VALID, false = waste/WASTE
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    
    // Physics-based offset values for swiping
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    
    // Swipe boundary thresholds
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val swipeThreshold = screenWidthPx * 0.40f

    // Current swipe status representation (for stamp overlays)
    val dragProgress = if (swipeThreshold > 0f) offsetX.value / swipeThreshold else 0f
    val rotationAngle = if (screenWidthPx > 0f) (offsetX.value / screenWidthPx) * 20f else 0f

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(transaction.transactionId) {
                detectDragGestures(
                    onDragEnd = {
                        coroutineScope.launch {
                            if (offsetX.value > swipeThreshold) {
                                // Slide out right: Approved VALID
                                offsetX.animateTo(screenWidthPx * 1.5f, spring())
                                onSwiped(true)
                            } else if (offsetX.value < -swipeThreshold) {
                                // Slide out left: Waste WASTE
                                offsetX.animateTo(-screenWidthPx * 1.5f, spring())
                                onSwiped(false)
                            } else {
                                // Snap back
                                launch { offsetX.animateTo(0f, spring()) }
                                launch { offsetY.animateTo(0f, spring()) }
                            }
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        coroutineScope.launch {
                            offsetX.snapTo(offsetX.value + dragAmount.x)
                            offsetY.snapTo(offsetY.value + dragAmount.y)
                        }
                    }
                )
            }
            .offset { IntOffset(offsetX.value.toInt(), offsetY.value.toInt()) }
            .rotate(rotationAngle)
            .testTag("spending_card_${transaction.transactionId}")
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        listOf(Color.White.copy(alpha = 0.15f), Color.Transparent)
                    ),
                    shape = RoundedCornerShape(24.dp)
                ),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = ObsidianSurface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Main Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    // Header Area with Category Badge + Amount Tag
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Category Badge
                        val badgeColor = when (transaction.category) {
                            "Defense" -> WasteRed.copy(alpha = 0.15f)
                            "Space" -> Color(0xFF38BDF8).copy(alpha = 0.15f)
                            "Science" -> GoldAccent.copy(alpha = 0.15f)
                            "Health" -> ValidGreen.copy(alpha = 0.15f)
                            "Education" -> Color(0xFF8B5CF6).copy(alpha = 0.15f)
                            else -> Color.White.copy(alpha = 0.08f)
                        }
                        
                        val categoryTextColor = when (transaction.category) {
                            "Defense" -> WasteRed
                            "Space" -> Color(0xFF38BDF8)
                            "Science" -> GoldLight
                            "Health" -> ValidGreen
                            "Education" -> Color(0xFFC084FC)
                            else -> Color.White
                        }

                        Box(
                            modifier = Modifier
                                .background(badgeColor, RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = transaction.category.uppercase(),
                                color = categoryTextColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Curated Highlight Tag
                        if (transaction.isCurated) {
                            Box(
                                modifier = Modifier
                                    .background(GoldAccent.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                                    .border(0.5.dp, GoldAccent, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "🔥 MEGA WASTE",
                                    color = GoldAccent,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Transaction Amount Display
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Amount Icon",
                            tint = GoldAccent,
                            modifier = Modifier.rotate(15f)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$%,.2f".format(transaction.amount),
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.SansSerif,
                            letterSpacing = (-0.5).sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Agency Name Display
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Agency Icon",
                            tint = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.height(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = transaction.agencyName,
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Text(
                        text = transaction.subAgencyName,
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.padding(start = 22.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Title / Summary header
                    Text(
                        text = transaction.title,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Description text (Scrollable/Wrap)
                    Text(
                        text = transaction.description,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        modifier = Modifier.weight(1f),
                        overflow = TextOverflow.Ellipsis
                    )

                    // Recipient Label
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White.copy(alpha = 0.04f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                text = "RECIPIENT",
                                color = Color.White.copy(alpha = 0.4f),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = transaction.recipientName,
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Doge Watchdog Audit / Humor Comment Section
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(GoldAccent.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                            .border(1.dp, GoldAccent.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Doge Audit Reason",
                                tint = GoldAccent,
                                modifier = Modifier
                                    .padding(top = 2.dp)
                                    .rotate(-10f)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "DOGE AUDIT COMMENT",
                                    color = GoldLight,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = transaction.absurdityReason,
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // DIRECTION STAMPS (Overlay)
                // Left Stamp (WASTE)
                if (dragProgress < 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 40.dp, end = 40.dp)
                            .rotate(15f)
                            .border(width = 3.dp, color = WasteRed, shape = RoundedCornerShape(8.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "WASTE",
                            color = WasteRed,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        )
                    }
                }

                // Right Stamp (VALID)
                if (dragProgress > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(top = 40.dp, start = 40.dp)
                            .rotate(-15f)
                            .border(width = 3.dp, color = ValidGreen, shape = RoundedCornerShape(8.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "VALID",
                            color = ValidGreen,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        )
                    }
                }
            }
        }
    }
}
