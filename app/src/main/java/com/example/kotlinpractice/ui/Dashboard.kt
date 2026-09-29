package com.example.kotlinpractice.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dashboard(
    onLogout: () -> Unit,
    onNavigateToCustomers: () -> Unit,
    customersList: List<Customer>,
) {


//    val scope = rememberCoroutineScope()
//    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

//    var selectedDrawerItem by remember { mutableStateOf("Dashboard") }


    val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when (currentHour) {
        in 0..11 -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        else -> "Good Evening"
    }

    val scrollState = rememberScrollState()
    Box(
        modifier = Modifier
            .fillMaxSize()
           //.padding(horizontal = 16.dp)
            .background(Color(0xFFF9FAFB))
    ) {

        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(20.dp)
        ) {
            // Header Greeting
            Text(
                text = "$greeting, User 👋",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Here's your exhibition overview for today",
                fontSize = 14.sp,
                color = Color(0xFF6B7280)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Metrics Cards Grid - Row 1
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Cards(
                    title = "Total Customers",
                    value = "1,020",
                    trend = "+12%",
                    icon = Icons.Default.People,
                    iconColor = Color(0xFF6366F1),
                    backgroundColor = Color(0xFFEEF0FF),
                    modifier = Modifier.weight(1f)
                )
                Cards(
                    title = "Today's Visitors",
                    value = "102",
                    trend = "+18",
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    iconColor = Color(0xFF10B981),
                    backgroundColor = Color(0xFFECFDF5),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            CustomerGrowthCard()
            Spacer(modifier = Modifier.height(14.dp))
            RecentCustomerCard(
                customersList = customersList,
                onViewAllClick = { onNavigateToCustomers() }
            )
        }
    }
}


@Composable
fun Cards(
    title: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    backgroundColor: Color = Color.White,
    trend: String? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = iconColor.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = iconColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                if (trend != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .border(
                                width = 1.dp,
                                color = Color(0xA1FAB851),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .background(iconColor.copy(alpha = 0.12f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)


                    ) {
                        Text(
                            text = trend,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = iconColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF4B5563)
            )
        }
    }
}

//GRAPH
@Composable
fun CustomerGrowthCard() {
    var selectedTab by remember { mutableStateOf("6 Months") }

    val months = when (selectedTab) {
        "3 Months" -> listOf("Apr", "May", "Jun")
        "1 Month" -> listOf("May", "Jun", "Jul")
        else -> listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun")
    }

    val points = when (selectedTab) {
        "3 Months" -> listOf(Offset(0f, 95f), Offset(175f, 60f), Offset(350f, 20f))
        "1 Year" -> listOf(
            Offset(0f, 110f),
            Offset(70f, 90f),
            Offset(140f, 60f),
            Offset(210f, 85f),
            Offset(280f, 40f),
            Offset(350f, 10f)
        )

        else -> listOf(
            Offset(0f, 100f),
            Offset(70f, 130f),
            Offset(140f, 95f),
            Offset(210f, 70f),
            Offset(280f, 45f),
            Offset(350f, 15f)
        )
    }

    val growthRate = when (selectedTab) {
        "3 Months" -> "+12% growth rate"
        "1 Year" -> "+15% growth rate"
        else -> "+24.5% growth rate"
    }

    val peakValue = when (selectedTab) {
        "3 Months" -> "May (1,150 Leads)"
        "1 Year" -> "Jun (1,200 Leads)"
        else -> "Jun (1,450 Leads)"
    }


    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // HEADER ROW: Title + Modern Pill Tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                color = Color(0xFF6278F1).copy(alpha = 0.15f),
                                shape = RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = "Growth",
                            tint = Color(0xFF6278F1),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Customer Growth",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A26)
                        )
                        Text(
                            text = growthRate,
                            fontSize = 11.sp,
                            color = Color(0xFF10B981),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Filter Tabs (6 Months, 3 Months, 1 Year)
                Row(
                    modifier = Modifier
                        .background(Color(0xFFF4F6FB), shape = RoundedCornerShape(20.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    listOf("6 Months", "3 Months", "1 Year").forEach { tab ->
                        val isSelected = selectedTab == tab
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) Color(0xFF6278F1) else Color.Transparent)
                                .clickable { selectedTab = tab }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tab,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else Color.Gray
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // FLOATING PEAK BADGE (Highlights the highest month's performance)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            color = Color(0xFFEEF0FF),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "🔥 Peak $peakValue",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6278F1)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // THE UNIQUE CANVAS GRAPH
            val primaryColor = Color(0xFF6278F1)
            val points = points

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                val width = size.width
                val height = size.height
                val stepX = width / (points.size - 1)

                // 1. Unique Vertical Column Gridlines (Clean analytics style)
                val gridColor = Color(0xFFF1F3F9)
                for (i in 0 until points.size) {
                    val x = i * stepX
                    drawLine(
                        color = gridColor,
                        start = Offset(x, 0f),
                        end = Offset(x, height),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // 2. Build Smooth Wave Curve Path
                val path = Path().apply {
                    moveTo(0f, height - (points[0].y / 150f) * height)
                    for (i in 0 until points.size - 1) {
                        val current = points[i]
                        val next = points[i + 1]
                        val x1 = i * stepX
                        val y1 = height - (current.y / 150f) * height
                        val x2 = (i + 1) * stepX
                        val y2 = height - (next.y / 150f) * height

                        cubicTo(
                            x1 + stepX / 2, y1,
                            x1 + stepX / 2, y2,
                            x2, y2
                        )
                    }
                }

                // 3. Rich Gradient Fill Underneath
                val fillPath = Path().apply {
                    addPath(path)
                    lineTo(width, height)
                    lineTo(0f, height)
                    close()
                }

                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = 0.35f),
                            primaryColor.copy(alpha = 0.02f)
                        ),
                        startY = 0f,
                        endY = height
                    )
                )

                // 4. Main Line Curve Stroke
                drawPath(
                    path = path,
                    color = primaryColor,
                    style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
                )

                // 5. Data Nodes with Special Glowing Effect on the Peak Node
                points.forEachIndexed { i, point ->
                    val x = i * stepX
                    val y = height - (point.y / 150f) * height
                    val isPeak = i == points.size - 1 // Last point is the peak

                    if (isPeak) {
                        // Outer glowing pulse ring for the peak node
                        drawCircle(
                            color = primaryColor.copy(alpha = 0.25f),
                            radius = 10.dp.toPx(),
                            center = Offset(x, y)
                        )
                    }

                    // Standard white border dot
                    drawCircle(
                        color = Color.White,
                        radius = 5.dp.toPx(),
                        center = Offset(x, y)
                    )
                    // Inner colored core dot
                    drawCircle(
                        color = primaryColor,
                        radius = 3.dp.toPx(),
                        center = Offset(x, y)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // X-AXIS MONTH LABELS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun").forEach { month ->
                    Text(
                        text = month,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

data class Customer(
    val name: String,
    val company: String,
    val phone: String,
    val email: String,
    val time: String,
    val status: String,
    val statusColor: Color,
    val statusBg: Color,
    val tags: List<String>,
)

@Composable
fun RecentCustomer(customer: Customer) {
    val avatarColors = listOf(
        Color(0xFF6278F1), // Blue
        Color(0xFF10B981), // Emerald Green
        Color(0xFFF59E0B), // Amber
        Color(0xFFEC4899), // Pink
        Color(0xFF8B5CF6), // Purple
        Color(0xFF06B6D4)  // Cyan
    )
    val colorIndex = kotlin.math.abs(customer.name.hashCode()) % avatarColors.size
    val accentColor = avatarColors[colorIndex]
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = accentColor.copy(alpha = 0.51f),
                        shape = RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = customer.name.take(1),
                    fontSize = 20.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Column {
                Text(
                    text = customer.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A26)
                )
                Text(text = customer.company, fontSize = 12.sp, color = Color(0xFF4B5563))
            }
        }
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(text = customer.time, fontSize = 12.sp, color = Color(0xFF4B5563))
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .background(
                        color = customer.statusBg,
                        shape = RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = customer.status,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = customer.statusColor
                )
            }
        }
    }
}

@Composable
fun RecentCustomerCard(customersList: List<Customer>, onViewAllClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Customers",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A26)
                )
                Text(
                    text = "View All",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6278F1),
                    modifier = Modifier.clickable {
                        onViewAllClick()
                    }
                )

            }
            Spacer(modifier = Modifier.height(16.dp))
            customersList.take(5).forEach { customer ->
                RecentCustomer(customer = customer)
            }
        }
    }
}


