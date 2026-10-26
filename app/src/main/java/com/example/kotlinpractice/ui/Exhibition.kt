package com.example.kotlinpractice.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.time.LocalDate
import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import com.example.kotlinpractice.network.ExhibitionApi
import kotlinx.coroutines.launch

data class Exhibition(
    val id: String,
    val name: String,
    val venue: String,
    val startDate: String,
    val endDate: String,
    val status: String,
    val description: String? = null
)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Exhibition(
    customer: List<Customer>,
    exhibition: MutableList<Exhibition>,
    onViewCustomers: (String) -> Unit,
    onNavigateToAddExhibition: () -> Unit
) {
    var editExhibition by remember { mutableStateOf<Exhibition?>(null) }
    val scope = rememberCoroutineScope()
    var isSaving by remember { mutableStateOf(false) }
    var editError by remember { mutableStateOf<String?>(null) }
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf("All") }

    val filteredExhibitions = exhibition.filter {
        val matchSearch = it.name.contains(searchQuery, ignoreCase = true)

        val matchStatus = selectedStatus == "All" ||
                it.status == selectedStatus

        matchSearch && matchStatus
    }

    var selectedExhibition by remember { mutableStateOf<Exhibition?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF5264F6).copy(alpha = .02f))
            .clickable(
                indication = null,
                interactionSource = interactionSource
            ) {
                focusManager.clearFocus()
            }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Exhibition", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "View and manage your all your exhibition",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Gray
                    )
                }
                Button(
                    onClick = {
                        // add customer logic
                        onNavigateToAddExhibition()
                    }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add exhibition",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Add Exhibition", fontSize = 13.sp)
                }

            }
//            Spacer(modifier = Modifier.height(2.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                BasicTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .height(40.dp)
                        .weight(1f),
                    singleLine = true,
                    interactionSource = interactionSource
                ) { innerTextField ->

                    OutlinedTextFieldDefaults.DecorationBox(
                        value = searchQuery,
                        innerTextField = innerTextField,
                        enabled = true,
                        singleLine = true,
                        visualTransformation = VisualTransformation.None,
                        interactionSource = interactionSource,
                        placeholder = {
                            Text(
                                text = "Search by exhibition name...",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color.Gray,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "search",
                                tint = Color.Gray,
                                modifier = Modifier.size(17.dp)
                            )
                        },
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                        container = {
                            OutlinedTextFieldDefaults.Container(
                                enabled = true,
                                isError = false,
                                interactionSource = interactionSource,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.LightGray,
                                    unfocusedBorderColor = Color(0xFFE5E7EB),
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                val statuses = listOf("All", "Upcoming", "Ongoing", "Completed")

                statuses.forEach { status ->
                    val isStatus = selectedStatus == status
                    Box(
                        modifier = Modifier
                            .background(
                                shape = RoundedCornerShape(20.dp),
                                color = if (isStatus) Color(0xFF9E5FF5).copy(alpha = 0.2f) else Color(
                                    0xFF9E5FF5
                                ).copy(alpha = 0.049f)
                            )
                            .clickable {
                                selectedStatus = status
                            }
                            .padding(
                                horizontal = 12.dp,
                                vertical = 2.dp
                            ),
                    ) {
                        Text(
                            text = status,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(
                                0xFF525252
                            )

                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))

            // cards
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredExhibitions) { item ->
                    ExhibitionCards(
                        exhibition = item,
                        onClick = {
                            selectedExhibition = item
                        }
                    )
                }
            }
            selectedExhibition?.let { exhibition ->

                DetailsDialog(
                    customer = customer,
                    exhibition = exhibition,
                    onDismiss = {
                        selectedExhibition = null
                    },
                    onViewCustomers = {
                        onViewCustomers(exhibition.name)
                    },
                    onEdit = {
                        editExhibition = exhibition
                        selectedExhibition = null
                    }
                )
            }

            editExhibition?.let { selected ->

                EditExhibitionDetails(
                    exhibition = selected,
                    isSaving = isSaving,
                    saveError = editError,
                    onDismiss = {
                        if (!isSaving) {
                            editExhibition = null
                            editError = null
                        }
                    },
                    onSave = { updatedExhibition ->

                        editError = null
                        isSaving = true

                        scope.launch {
                            try {
                                // 1. Send PUT request
                                ExhibitionApi.updateExhibition(updatedExhibition)

                                // 2. Fetch updated data from the server
                                val latestExhibitions =
                                    ExhibitionApi.getExhibitions()

                                // 3. Refresh the displayed list
                                exhibition.clear()
                                exhibition.addAll(latestExhibitions)

                                // 4. Close the dialog after success
                                editExhibition = null

                            } catch (e: Exception) {
                                editError = e.message
                                    ?: "Failed to update exhibition"
                            } finally {
                                isSaving = false
                            }
                        }
                    }
                )
            }

        }
    }
}

@Composable
fun ExhibitionCards(
    exhibition: Exhibition,
    onClick: () -> Unit
) {
    val statusStyle = exhibitionStatusStyle(exhibition.status)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false,
                spotColor = Color(0xFF6366F1).copy(alpha = 0.15f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFEEF2FF)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Business,
                            contentDescription = "Exhibition",
                            tint = Color(0xFF6366F1),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = exhibition.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF111827),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "venue",
                                tint = Color(0xFF9CA3AF),
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = exhibition.venue,
                                fontWeight = FontWeight.Normal,
                                fontSize = 13.sp,
                                color = Color(0xFF6B7280),
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = statusStyle.background,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = statusStyle.icon,
                                contentDescription = exhibition.status,
                                tint = statusStyle.color,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = exhibition.status,
                                fontSize = 12.sp,
                                color = statusStyle.color,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    IconButton(
                        onClick = onClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color(0xFFF3F4F6), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = Color(0xFF4B5563),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Date",
                        tint = Color(0xFF9CA3AF),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "${exhibition.startDate}  —  ${exhibition.endDate}",
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = Color(0xFF4B5563)
                    )
                }

                if (!exhibition.description.isNullOrBlank()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Notes,
                            contentDescription = "Note",
                            tint = Color(0xFF9CA3AF),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Has description",
                            fontSize = 11.sp,
                            color = Color(0xFF9CA3AF)
                        )
                    }
                }
            }
        }
    }
}

data class ExhibitionStatusStyle(
    val color: Color,
    val background: Color,
    val icon: ImageVector
)

fun exhibitionStatusStyle(status: String): ExhibitionStatusStyle {
    return when (status) {
        "Upcoming" -> ExhibitionStatusStyle(
            color = Color(0xFF3B82F6),
            background = Color(0xFFEFF6FF),
            icon = Icons.Default.AccessTime
        )

        "Ongoing" -> ExhibitionStatusStyle(
            color = Color(0xFF10B981),
            background = Color(0xFFECFDF5),
            icon = Icons.Default.HourglassTop
        )

        "Completed" -> ExhibitionStatusStyle(
            color = Color(0xFF6B7280),
            background = Color(0xFFF3F4F6),
            icon = Icons.Default.Check
        )

        else -> ExhibitionStatusStyle(
            color = Color.Gray,
            background = Color.LightGray,
            icon = Icons.Default.AccessTime
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsDialog(
    customer: List<Customer>,
    exhibition: Exhibition,
    onDismiss: () -> Unit,
    onViewCustomers: () -> Unit,
    onEdit: () -> Unit
) {
    val statusStyle = exhibitionStatusStyle(exhibition.status)
    val exhibitionCustomers = customer.filter {
        it.exhibition == exhibition.name
    }

    val totalCustomers = exhibitionCustomers.size

    val followUpCustomers = exhibitionCustomers.count {
        it.status == "Follow Up"
    }

    val confirmedCustomers = exhibitionCustomers.count {
        it.status == "Confirmed"
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0xFF5E35B1).copy(alpha = 0.10f)
                            )
                            .padding(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = "Exhibition",
                            tint = Color(0xFF9E5FF5).copy(alpha = 0.40f),
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text(text = exhibition.name, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Box(
                            modifier = Modifier
                                .background(
                                    shape = RoundedCornerShape(44.dp),
                                    color = statusStyle.background
                                )
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = statusStyle.icon,
                                    contentDescription = exhibition.status,
                                    tint = statusStyle.color,
                                    modifier = Modifier.size(14.dp)
                                )

                                Text(
                                    text = exhibition.status,
                                    fontSize = 12.sp,
                                    color = statusStyle.color,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(3.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Exhibition",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )

                    Text(text = exhibition.venue, fontSize = 12.sp)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        verticalAlignment = Alignment.CenterVertically,

                        ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Date from",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = exhibition.startDate,
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Hypen",
                        tint = Color.Gray,
                        modifier = Modifier.size(10.dp)
                    )


                    Text(
                        text = exhibition.endDate,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                HorizontalDivider(
                    thickness = 1.dp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.height(IntrinsicSize.Min),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    shape = CircleShape,
                                    color = Color(0xFF6366F1).copy(alpha = 0.16f)
                                )
                                .padding(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.People,
                                contentDescription = "customers",
                                tint = Color(0xFF6366F1),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column(
                            verticalArrangement = Arrangement.spacedBy(1.dp)
                        ) {
                            Text(
                                text = totalCustomers.toString(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(text = "Leads", fontSize = 12.sp)
                        }
                    }

                    VerticalDivider(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(1.dp), // Takes up full row height
                        color = Color.LightGray
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    shape = CircleShape,
                                    color = Color(0xFFFFC400).copy(alpha = 0.16f)
                                )
                                .padding(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = "follow up",
                                tint = Color(0xFFFFC400),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column(
                            verticalArrangement = Arrangement.spacedBy(1.dp)
                        ) {
                            Text(
                                text = followUpCustomers.toString(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(text = "Follow Up", fontSize = 12.sp)
                        }
                    }

                    VerticalDivider(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(1.dp), // Takes up full row height
                        color = Color.LightGray
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    shape = CircleShape,
                                    color = Color(0xFF3B82F6).copy(alpha = 0.16f)
                                )
                                .padding(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "confirmed",
                                tint = Color(0xFF3B82F6),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column(
                            verticalArrangement = Arrangement.spacedBy(1.dp)
                        ) {
                            Text(
                                text = confirmedCustomers.toString(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(text = "Confirmed", fontSize = 12.sp)
                        }
                    }

                }

                Spacer(modifier = Modifier.height(6.dp))
                HorizontalDivider(
                    thickness = 1.dp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(6.dp))

                if (!exhibition.description.isNullOrBlank()) {
                    Text(text = "Description")
                    Text(
                        text = exhibition.description,
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFF9E5FF5).copy(alpha = 0.10f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            // add view customers logic
                            onViewCustomers()
                        }
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.People,
                        contentDescription = "View customers",
                        tint = Color(0xFF9E5FF5),
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "View Customers",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF7E3FD3),
                        modifier = Modifier.weight(1f)
                    )

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color(0xFF9E5FF5),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Edit Exhibition
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFF6366F1),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            // add edit exhibition logic
                            onEdit()
                        }
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit exhibition",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "Edit Exhibition",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditExhibitionDetails(
    exhibition: Exhibition,
    isSaving: Boolean = false,
    saveError: String? = null,
    onDismiss: () -> Unit,
    onSave: (Exhibition) -> Unit,
) {
    var name by remember { mutableStateOf(exhibition.name) }
    var venue by remember { mutableStateOf(exhibition.venue) }
    var startDate by remember { mutableStateOf(exhibition.startDate) }
    var endDate by remember { mutableStateOf(exhibition.endDate) }
    var status by remember { mutableStateOf(exhibition.status) }
    var statusEdit by remember { mutableStateOf(false) }
    val availableStatus = remember { listOf("Ongoing", "Upcoming", "Completed") }
    val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
    var dateError by remember { mutableStateOf("") }
    val context = LocalContext.current


    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    text = "Edit Exhibition",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Text(
                    text = "Update exhibition details",
                    fontSize = 13.sp,
                    color = Color.Gray
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = {
                        Text("Exhibition Name")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    OutlinedTextField(
                        value = status,
                        onValueChange = { },
                        label = { Text("Status") },
                        singleLine = true,
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Menu"
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable {
                                statusEdit = !statusEdit
                            }
                    )

                    DropdownMenu(
                        expanded = statusEdit,
                        onDismissRequest = {
                            statusEdit = false
                        }
                    ) {
                        availableStatus.forEach { option ->

                            DropdownMenuItem(
                                text = {
                                    Text(option)
                                },
                                onClick = {
                                    status = option
                                    statusEdit = false
                                }
                            )
                        }
                    }
                }

                // Start Date
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = startDate,
                        onValueChange = {},
                        readOnly = true,
                        enabled = true,
                        label = { Text("Start Date") },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Select start date"
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Click layer
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable {
                                val calendar = Calendar.getInstance()

                                DatePickerDialog(
                                    context,
                                    { _, year, month, day ->
                                        val selectedDate = LocalDate.of(
                                            year,
                                            month + 1,
                                            day
                                        )

                                        startDate = selectedDate.format(dateFormatter)

                                        if (endDate.isNotBlank()) {
                                            val currentEndDate =
                                                LocalDate.parse(
                                                    endDate,
                                                    dateFormatter
                                                )

                                            if (currentEndDate.isBefore(selectedDate)) {
                                                endDate = ""
                                            }
                                        }

                                        dateError = ""
                                    },
                                    calendar.get(Calendar.YEAR),
                                    calendar.get(Calendar.MONTH),
                                    calendar.get(Calendar.DAY_OF_MONTH)
                                ).show()
                            }
                    )
                }
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = endDate,
                        onValueChange = {},
                        readOnly = true,
                        enabled = true,
                        label = { Text("End Date") },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Select end date"
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable {

                                // Start date must exist first
                                if (startDate.isBlank()) {
                                    dateError = "Please select start date first"
                                    return@clickable
                                }

                                val start = LocalDate.parse(
                                    startDate,
                                    dateFormatter
                                )

                                val calendar = Calendar.getInstance()

                                DatePickerDialog(
                                    context,
                                    { _, year, month, day ->

                                        val selectedDate = LocalDate.of(
                                            year,
                                            month + 1,
                                            day
                                        )

                                        endDate = selectedDate.format(dateFormatter)
                                        dateError = ""
                                    },
                                    calendar.get(Calendar.YEAR),
                                    calendar.get(Calendar.MONTH),
                                    calendar.get(Calendar.DAY_OF_MONTH)
                                ).apply {

                                    // Disable all dates before Start Date
                                    datePicker.minDate =
                                        start
                                            .atStartOfDay(
                                                java.time.ZoneId.systemDefault()
                                            )
                                            .toInstant()
                                            .toEpochMilli()

                                }.show()
                            }
                    )
                }

                if (dateError.isNotBlank()) {
                    Text(
                        text = dateError,
                        color = Color.Red,
                        fontSize = 12.sp
                    )
                }

                OutlinedTextField(
                    value = venue,
                    onValueChange = { venue = it },
                    label = {
                        Text("Venue")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                saveError?.let { error ->
                    Text(
                        text = error,
                        color = Color.Red,
                        fontSize = 12.sp
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        enabled = !isSaving,
                        onClick = {
                            if (startDate.isBlank()) {
                                dateError = "Please select a start date"
                                return@Button
                            }

                            // Check end date
                            if (endDate.isBlank()) {
                                dateError = "Please select an end date"
                                return@Button
                            }

                            val start = LocalDate.parse(
                                startDate,
                                dateFormatter
                            )

                            val end = LocalDate.parse(
                                endDate,
                                dateFormatter
                            )

                            if (end.isBefore(start)) {
                                dateError = "End date cannot be before start date"
                                return@Button
                            }

                            dateError = ""

                            val updated = exhibition.copy(
                                name = name,
                                venue = venue,
                                startDate = startDate,
                                endDate = endDate,
                                status = status
                            )

                            onSave(updated)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6366F1)
                        )
                    ) {
                        Text(
                            if (isSaving) "Saving..." else "Save Changes"
                        )
                    }
                }
            }
        }
    }
}