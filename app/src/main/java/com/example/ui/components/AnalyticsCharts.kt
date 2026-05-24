package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.BudgetYear
import com.example.ui.DepartmentBudgetHistory
import com.example.ui.theme.WasteRed
import com.example.ui.theme.ValidGreen
import kotlin.math.abs
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.ObsidianSurface

@Composable
fun LineChartTrend(
    selectedDept: DepartmentBudgetHistory,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val points = selectedDept.dataPoints
    
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    
    // Smooth drawing animations
    var animateTrigger by remember { mutableStateOf(0f) }
    val animateFactor by animateFloatAsState(
        targetValue = animateTrigger,
        animationSpec = tween(durationMillis = 800)
    )

    LaunchedEffect(selectedDept) {
        selectedIndex = null
        animateTrigger = 0f
        animateTrigger = 1f
    }

    val deptColor = Color(android.graphics.Color.parseColor(selectedDept.colorHex))
    
    // Map bounds
    val maxBudget = remember(points) { points.maxOf { it.dollarsInBillions } }
    val minBudget = remember(points) { points.minOf { it.dollarsInBillions } }
    val yMax = maxBudget * 1.15f
    val yMin = minBudget * 0.85f
    val budgetRange = yMax - yMin

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(ObsidianSurface, RoundedCornerShape(16.dp))
            .border(0.5.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .padding(16.dp)
            .testTag("trend_line_chart")
    ) {
        Text(
            text = "YOY TREND: ${selectedDept.deptName.uppercase()}",
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            color = GoldAccent,
            letterSpacing = 1.sp
        )
        Text(
            text = "Total Spending (Years 2021 - 2026)",
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(selectedDept) {
                        detectTapGestures { offset ->
                            // Calculate which coordinate index is closest to the tap
                            val chartWidth = size.width
                            val paddingStart = 60f
                            val paddingEnd = 40f
                            val usableWidth = chartWidth - paddingStart - paddingEnd
                            val divisor = points.size - 1
                            val stepX = if (divisor > 0) usableWidth / divisor else 1f

                            var closestIndex = 0
                            var minDistance = Float.MAX_VALUE
                            for (i in points.indices) {
                                val vx = paddingStart + i * stepX
                                val distance = abs(offset.x - vx)
                                if (distance < minDistance) {
                                    minDistance = distance
                                    closestIndex = i
                                }
                            }

                            if (minDistance < stepX * 0.6) {
                                selectedIndex = closestIndex
                            }
                        }
                    }
            ) {
                val chartWidth = size.width
                val chartHeight = size.height

                // Axis margins
                val paddingStart = 60f
                val paddingEnd = 40f
                val paddingTop = 20f
                val paddingBottom = 40f

                val usableWidth = chartWidth - paddingStart - paddingEnd
                val usableHeight = chartHeight - paddingTop - paddingBottom

                // Grid lines (3 horizontal helper slots)
                val gridColor = Color.White.copy(alpha = 0.05f)
                drawLine(gridColor, Offset(paddingStart, paddingTop), Offset(chartWidth - paddingEnd, paddingTop))
                drawLine(gridColor, Offset(paddingStart, paddingTop + usableHeight / 2), Offset(chartWidth - paddingEnd, paddingTop + usableHeight / 2))
                drawLine(gridColor, Offset(paddingStart, paddingTop + usableHeight), Offset(chartWidth - paddingEnd, paddingTop + usableHeight))

                // Collect points
                val divisor = points.size - 1
                val stepX = if (divisor > 0) usableWidth / divisor else 1f
                val coords = points.mapIndexed { idx, point ->
                    val vx = paddingStart + idx * stepX
                    val percentY = if (budgetRange > 0f) (point.dollarsInBillions - yMin) / budgetRange else 0.0
                    val vy = paddingTop + usableHeight - (percentY * usableHeight).toFloat()
                    Offset(vx, vy)
                }

                // Draw connecting smooth trend line
                if (coords.isNotEmpty()) {
                    val strokePath = Path().apply {
                        val firstLoc = coords.first()
                        moveTo(firstLoc.x, firstLoc.y + (usableHeight - (firstLoc.y - paddingTop)) * (1f - animateFactor))
                        
                        for (i in 1 until coords.size) {
                            val dest = coords[i]
                            val animatedY = dest.y + (usableHeight - (dest.y - paddingTop)) * (1f - animateFactor)
                            lineTo(dest.x, animatedY)
                        }
                    }

                    drawPath(
                        path = strokePath,
                        color = deptColor,
                        style = Stroke(width = 3.dp.toPx())
                    )

                    // Draw coordinates, stars, and click items
                    coords.forEachIndexed { i, coord ->
                        val pointBudget = points[i].dollarsInBillions
                        val animatedY = coord.y + (usableHeight - (coord.y - paddingTop)) * (1f - animateFactor)
                        
                        // Point marker glow
                        val isSelected = selectedIndex == i
                        val circleRadius = if (isSelected) 8.dp.toPx() else 4.dp.toPx()
                        val markerColor = if (isSelected) GoldAccent else deptColor

                        drawCircle(
                            color = markerColor.copy(alpha = 0.35f),
                            radius = circleRadius + 6f,
                            center = Offset(coord.x, animatedY)
                        )
                        drawCircle(
                            color = markerColor,
                            radius = circleRadius,
                            center = Offset(coord.x, animatedY)
                        )

                        // Draw year text below Axis
                        if (i % 1 == 0) {
                            val yearText = points[i].year.toString()
                            val layoutResult = textMeasurer.measure(
                                text = yearText,
                                style = TextStyle(
                                    color = Color.White.copy(alpha = 0.4f),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            drawText(
                                textLayoutResult = layoutResult,
                                topLeft = Offset(coord.x - layoutResult.size.width / 2, chartHeight - paddingBottom + 10f)
                            )
                        }
                    }

                    // Selected guideline and text tooltip
                    selectedIndex?.let { selIdx ->
                        if (selIdx < coords.size) {
                            val selCoord = coords[selIdx]
                            val animatedY = selCoord.y + (usableHeight - (selCoord.y - paddingTop)) * (1f - animateFactor)
                            
                            // Highlight Y vertical bar
                            drawLine(
                                color = GoldAccent.copy(alpha = 0.3f),
                                start = Offset(selCoord.x, paddingTop),
                                end = Offset(selCoord.x, chartHeight - paddingBottom),
                                strokeWidth = 1.dp.toPx()
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tooltip display
        val currentSelected = (selectedIndex ?: (points.size - 1)).coerceIn(0, points.size - 1)
        val pointVal = points.getOrNull(currentSelected) ?: BudgetYear(2026, 0.0)
        
        // Compute relative year percent shift (if index > 0)
        val percentText = if (currentSelected > 0 && points.size > currentSelected) {
            val past = points[currentSelected - 1].dollarsInBillions
            val curr = pointVal.dollarsInBillions
            val diffRatio = if (past != 0.0) ((curr - past) / past) * 100.0 else 0.0
            if (diffRatio >= 0.0) {
                "📈 +%.1f%% Year-over-Year".format(diffRatio)
            } else {
                "📉 %.1f%% Year-over-Year".format(diffRatio)
            }
        } else {
            "🏁 Baseline Year 2021"
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.03f), RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "DETAILED METRIC (${pointVal.year})",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.4f),
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "$%,.1f BILLION".format(pointVal.dollarsInBillions),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
                
                Box(
                    modifier = Modifier
                        .background(
                            if (percentText.contains("+")) WasteRed.copy(alpha = 0.12f)
                            else if (percentText.contains("-")) ValidGreen.copy(alpha = 0.12f)
                            else Color.White.copy(alpha = 0.05f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = percentText,
                        color = if (percentText.contains("+")) WasteRed
                                else if (percentText.contains("-")) ValidGreen
                                else Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ComparativeBarChart(
    allDepts: List<DepartmentBudgetHistory>,
    modifier: Modifier = Modifier
) {
    // Show 2026 comparative budgets horizontal bar metrics
    val reportData = remember(allDepts) {
        allDepts.map { dept ->
            val budget2026 = dept.dataPoints.lastOrNull { it.year == 2026 }?.dollarsInBillions ?: 0.0
            ComparativeDeptRow(
                name = dept.deptName.split(" ").take(3).joinToString(" "),
                amount = budget2026,
                color = Color(android.graphics.Color.parseColor(dept.colorHex))
            )
        }.sortedByDescending { it.amount }
    }

    val maxAmount = reportData.maxOf { it.amount }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("comparative_bar_chart"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
        border = androidx.compose.foundation.BorderStroke(
            0.5.dp, Color.White.copy(alpha = 0.1f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "DEPARTMENT FISCAL COMPARSION (2026)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = GoldAccent,
                letterSpacing = 1.sp
            )
            Text(
                text = "Comparing Department spending scale in $ Billions",
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            reportData.forEach { row ->
                val ratio = if (maxAmount > 0.0) (row.amount / maxAmount).toFloat() else 0f
                
                // Animated horizontal progress ratios
                val barProgress by animateFloatAsState(
                    targetValue = ratio,
                    animationSpec = tween(durationMillis = 1000)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = row.name,
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(180.dp),
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                        Text(
                            text = "$%,.1fB".format(row.amount),
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    // Custom pill bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(4.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(barProgress.coerceIn(0f, 1f))
                                .fillMaxHeight()
                                .background(row.color, RoundedCornerShape(4.dp))
                        )
                    }
                }
            }
        }
    }
}

data class ComparativeDeptRow(
    val name: String,
    val amount: Double,
    val color: Color
)
