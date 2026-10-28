package com.example.kotlinpractice.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar
import java.util.Locale
import com.example.kotlinpractice.network.CustomerApi
import com.example.kotlinpractice.network.ExhibitionStats
import kotlin.math.abs
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dashboard(
    onLogout: () -> Unit,
    onNavigateToCustomers: () -> Unit,
    onNavigateToAddCustomer: () -> Unit = {},
    onNavigateToExhibition: () -> Unit = {},
    customersList: List<Customer>,
) {
    var customers by remember {
        mutableStateOf<List<Customer>>(emptyList())
    }

    var isLoadingCustomers by remember {
        mutableStateOf(true)
    }

    var todayVisitors by remember {
        mutableStateOf<Int?>(null)
    }

    var isLoadingVisitors by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(Unit) {
        // Load all customers
        try {
            customers = CustomerApi.getCustomers()
        } catch (e: Exception) {
            customers = emptyList()
        } finally {
            isLoadingCustomers = false
        }

        // Load today's visitors from the backend
        try {
            todayVisitors = CustomerApi.getTodayVisitors()
        } catch (e: Exception) {
            todayVisitors = null
        } finally {
            isLoadingVisitors = false
        }
    }

    val totalCustomer = customers.size

    val totalExhibitions = customers.map { it.exhibition }.distinct().size

    val animatedTotal = remember {
        Animatable(0f)
    }

    LaunchedEffect(totalCustomer) {
        animatedTotal.snapTo(0f)

        animatedTotal.animateTo(
            targetValue = totalCustomer.toFloat(),
            animationSpec = tween(
                durationMillis = 1500,
                easing = FastOutSlowInEasing
            )
        )
    }

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
            .background(Color(0xFFF8F9FA))
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
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Here's your exhibition overview for today",
                fontSize = 13.sp,
                color = Color(0xFF6B7280)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Hero Card: Total Customers
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF6366F1)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "TOTAL CUSTOMERS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.8f),
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = animatedTotal.value.toInt().toString(),
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Box(
                            modifier = Modifier
                                .background(
                                    Color.White.copy(alpha = 0.2f),
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "+12% this month",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.People,
                            contentDescription = "Customers",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Secondary Cards Row: Visitors & Exhibitions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                SecondaryMetricCard(
                    title = "Today's Visitors",
                    value = when {
                        isLoadingVisitors -> "..."
                        todayVisitors != null -> todayVisitors.toString()
                        else -> "—"
                    },
                    trend = "+18%",
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    iconColor = Color(0xFF10B981),
                    backgroundColor = Color(0xFFCFFCE7),
                    modifier = Modifier.weight(1f)
                )
                SecondaryMetricCard(
                    title = "Total Exhibitions",
                    value = totalExhibitions.toString(),
                    trend = "Active",
                    icon = Icons.Default.Storefront,
                    iconColor = Color(0xFF3B82F6),
                    backgroundColor = Color(0xFFD9E9FD),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            CustomerGrowthCard()
            Spacer(modifier = Modifier.height(16.dp))
            RecentCustomerCard(
                customersList = customers,
                onViewAllClick = { onNavigateToCustomers() }
            )
        }

        ExtendedFloatingActionButton(
            onClick = onNavigateToAddCustomer,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),
            containerColor = Color(0xFF6366F1),
            contentColor = Color.White,
            icon = {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Customer"
                )
            },
            text = {
                Text(
                    text = "Add Customer",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        )
    }
}


@Composable
fun SecondaryMetricCard(
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
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(
                            color = iconColor.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp)
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
                            .background(iconColor.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
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

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = value,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827)
                )

                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF4B5563)
                )
            }
        }
    }
}

// DONUT CHART GRAPH CARD
@Composable
fun CustomerGrowthCard() {
    var exhibitions by remember {
        mutableStateOf<List<ExhibitionStats>>(emptyList())
    }

    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val colors = listOf(
        Color(0xFF6366F1), // Indigo
        Color(0xFF10B981), // Emerald
        Color(0xFFF59E0B), // Amber
        Color(0xFFEC4899), // Pink
        Color(0xFF06B6D4), // Cyan
        Color(0xFF8B5CF6), // Violet
        Color(0xFFEF4444), // Red
        Color(0xFF3B82F6)  // Blue
    )

    LaunchedEffect(Unit) {
        try {
            exhibitions = CustomerApi.getExhibitionStats()
            errorMessage = null
        } catch (e: Exception) {
            errorMessage = "Unable to load exhibition statistics"
        } finally {
            isLoading = false
        }
    }

    val totalCustomers = exhibitions.sumOf {
        it.totalCustomers
    }

    val chartProgress = remember {
        Animatable(0f)
    }

    LaunchedEffect(exhibitions) {
        chartProgress.snapTo(0f)

        if (exhibitions.isNotEmpty() && totalCustomers > 0) {
            delay(150)

            chartProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 1400,
                    easing = FastOutSlowInEasing
                )
            )
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(
                            Color(0xFFEEF2FF),
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.People,
                        contentDescription = "Exhibition analytics",
                        tint = Color(0xFF6366F1),
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Exhibition Analytics",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827)
                    )

                    Text(
                        text = "Customers distributed by exhibition",
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Loading analytics...", color = Color(0xFF6B7280), fontSize = 13.sp)
                    }
                }

                errorMessage != null -> {
                    Text(
                        text = errorMessage!!,
                        color = Color(0xFFDC2626),
                        fontSize = 13.sp
                    )
                }

                exhibitions.isEmpty() || totalCustomers == 0 -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No exhibition customer data available",
                            color = Color(0xFF9CA3AF),
                            fontSize = 13.sp,
                        )
                    }
                }

                else -> {
                    // Donut chart
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(210.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(
                            modifier = Modifier.size(190.dp)
                        ) {
                            var startAngle = -90f
                            val strokeWidth = 36.dp.toPx()

                            exhibitions.forEachIndexed { index, exhibition ->
                                if (exhibition.totalCustomers > 0) {
                                    val sweepAngle =
                                        exhibition.totalCustomers.toFloat() /
                                                totalCustomers * 360f * chartProgress.value

                                    drawArc(
                                        color = colors[index % colors.size],
                                        startAngle = startAngle,
                                        sweepAngle = sweepAngle - 2f,
                                        useCenter = false,
                                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                    )

                                    startAngle += sweepAngle
                                }
                            }
                        }

                        // Center summary
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = totalCustomers.toString(),
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF111827)
                            )

                            Text(
                                text = "Total Customers",
                                fontSize = 11.sp,
                                color = Color(0xFF6B7280),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Exhibition legend
                    exhibitions.forEachIndexed { index, exhibition ->
                        val percentage =
                            exhibition.totalCustomers.toFloat() /
                                    totalCustomers * 100f

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(
                                            colors[index % colors.size],
                                            CircleShape
                                        )
                                )

                                Text(
                                    text = exhibition.exhibitionName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF374151),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${exhibition.totalCustomers}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF111827)
                                )
                                Text(
                                    text = "(${percentage.toInt()}%)",
                                    fontSize = 11.sp,
                                    color = Color(0xFF6B7280)
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
fun RecentCustomer(customer: Customer) {
    val avatarColors = listOf(
        Color(0xFF6278F1).copy(alpha = .10f),
        Color(0xFF10B981),
        Color(0xFFF59E0B),
        Color(0xFFEC4899),
        Color(0xFF8B5CF6),
        Color(0xFF06B6D4),
        Color(0xFF03A9F4),
        Color(0xFFE91E63),
        Color(0xFFE040FB),
        Color(0xFF64FFDA),
    )
    val colorIndex = abs(customer.name.hashCode()) % avatarColors.size
    val accentColor = avatarColors[colorIndex]
    Column(
        modifier = Modifier
            .padding(vertical = 16.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = accentColor.copy(alpha = 0.34f),
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
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = customer.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A26)
                    )
                    Box(
                        modifier = Modifier
                            .widthIn(max = 180.dp)
                            .border(
                                width = 1.dp,
                                color = Color(0xFF6366F1).copy(alpha = 0.30f),
                                shape = RoundedCornerShape(28.dp)
                            )
                            .background(
                                color = Color(0xFF6366F1).copy(alpha = 0.10f),
                                shape = RoundedCornerShape(28.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Exhibition: ${customer.exhibition}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6366F1),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
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
                TextButton(
                    onClick = onViewAllClick
                ) {
                    Text(
                        text = "View All",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6278F1)
                    )
                }

            }
            Spacer(modifier = Modifier.height(16.dp))
            if (customersList.count() == 0) {
                Text(
                    text = "No customers",
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                customersList.take(5).forEach { customer ->
                    RecentCustomer(customer = customer)
                }

            }
        }
    }
}

data class Customer(
    val id: String = "",
    val name: String,
    val exhibition: String,
    val phone: String,
    val email: String,
    val time: String,
    val status: String,
    val statusColor: Color,
    val statusBg: Color,
    val note: String? = null
)
