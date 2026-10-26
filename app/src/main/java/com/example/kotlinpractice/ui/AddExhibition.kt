package com.example.kotlinpractice.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExhibition(
    exhibition: List<Exhibition>,
    onSave: (Exhibition) -> Unit,
    onBack: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    // Form state variables
    var exhibitionName by remember { mutableStateOf("") }
    var venue by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    var startDateMillis by remember { mutableStateOf<Long?>(null) }
    var endDateMillis by remember { mutableStateOf<Long?>(null) }

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    var startDateError by remember { mutableStateOf(false) }
    var endDateError by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("Upcoming") }
    var statusExpanded by remember { mutableStateOf(false) }
    val statusOptions = listOf(
        "Upcoming",
        "Ongoing",
        "Completed"
    )
    var description by remember { mutableStateOf("") }

    fun formatDate(millis: Long): String {
        return SimpleDateFormat(
            "dd MMM yyyy",
            Locale.getDefault()
        ).format(Date(millis))
    }

    var isSubmitted by remember { mutableStateOf(false) }

    val nameError = isSubmitted && exhibitionName.isBlank()
    val venueError = isSubmitted && venue.isBlank()

    val nameInteractionSource = remember { MutableInteractionSource() }
    val venueInteractionSource = remember { MutableInteractionSource() }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF8F9FA)
                ),
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
//                            modifier = Modifier.weight(1f),
//                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "Add Exhibition",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color(0xFF111827),
                                lineHeight = 22.sp
                            )
                            Text(
                                text = "Create a new exhibition and add its details",
                                fontSize = 12.sp,
                                color = Color(0xFF6B7280),
                                lineHeight = 14.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    color = Color(0xFFEEF2FF),
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Exhibition Icon",
                                tint = Color(0xFF6366F1),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            // TODO: Needed logic for back navigation
                            onBack()
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    color = Color(0xFFEEF0FF),
                                    shape = RoundedCornerShape(10.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color(0xFF6366F1),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            // TODO: Needed logic for cancel action
                            onBack()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF374151)
                        )
                    ) {
                        Text(
                            text = "Cancel",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }

                    Button(
                        onClick = {
                            isSubmitted = true

                            startDateError = startDate.isBlank()
                            endDateError = endDate.isBlank()

                            val isValid = exhibitionName.isNotBlank() &&
                                          venue.isNotBlank() &&
                                          startDate.isNotBlank() &&
                                          endDate.isNotBlank()

                            if (isValid) {
                                val newExhibition = Exhibition(
                                    id = System.currentTimeMillis().toString(),
                                    name = exhibitionName.trim(),
                                    venue = venue.trim(),
                                    startDate = startDate,
                                    endDate = endDate,
                                    status = status,
                                    description = description.trim().ifEmpty { null }
                                )

                                onSave(newExhibition)
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6366F1)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Save Exhibition",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .verticalScroll(scrollState)
                .padding(16.dp)
                .clickable(
                    indication = null,
                    interactionSource = interactionSource
                ) {
                    focusManager.clearFocus()
                },
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Exhibition Name
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row {
                    Text(
                        text = "Exhibition Name ",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF374151)
                    )
                    Text(
                        text = "*",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFEF4444)
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()

                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        BasicTextField(
                            value = exhibitionName,
                            onValueChange = {
                                exhibitionName = it.replaceFirstChar { char ->
                                    if (char.isLowerCase()) {
                                        char.titlecase()
                                    } else {
                                        char.toString()
                                    }
                                }
                            },
                            modifier = Modifier
                                .height(52.dp)
                                .weight(1f)
                                .background(color = Color.White),
                            singleLine = true,
                            interactionSource = nameInteractionSource
                        ) { innerTextField ->
                            OutlinedTextFieldDefaults.DecorationBox(
                                value = exhibitionName,
                                innerTextField = innerTextField,
                                enabled = true,
                                singleLine = true,
                                visualTransformation = VisualTransformation.None,
                                interactionSource = interactionSource,
                                placeholder = {
                                    Text(
                                        text = "Enter exhibition name",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = Color.Gray,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.LocalOffer,
                                        contentDescription = "name",
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
                                            focusedBorderColor = Color(0xFFE5E7EB),
                                            unfocusedBorderColor = if (nameError) Color(0xFFFF1744)
                                            else Color(0xFFE5E7EB),

                                            ),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                }
                            )
                        }
                    }
                }
                if (nameError) {
                    Text(
                        text = "Enter exhibition name",
                        color = Color(0xFFDE3757),
                        fontSize = 11.sp
                    )
                }
            }

            // 2. Venue
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row {
                    Text(
                        text = "Venue ",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF374151)
                    )
                    Text(
                        text = "*",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFEF4444)
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(12.dp))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {

                        BasicTextField(
                                value = venue,
                        onValueChange = {
                            venue = it.replaceFirstChar { char ->
                                if (char.isLowerCase()) {
                                    char.titlecase()
                                } else {
                                    char.toString()
                                }
                            }
                        },
                        modifier = Modifier
                            .height(52.dp)
                            .weight(1f)
                            .background(color = Color.White),
                        singleLine = true,
                        interactionSource = venueInteractionSource
                        ) { innerTextField ->
                        OutlinedTextFieldDefaults.DecorationBox(
                            value = venue,
                            innerTextField = innerTextField,
                            enabled = true,
                            singleLine = true,
                            visualTransformation = VisualTransformation.None,
                            interactionSource = interactionSource,
                            placeholder = {
                                Text(
                                    text = "Enter venue or location",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color.Gray,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "venue",
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
                                        focusedBorderColor = Color(0xFFE5E7EB),
                                        unfocusedBorderColor = if (venueError) Color(0xFFFF1744)
                                        else Color(0xFFE5E7EB),

                                        ),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                        )
                    }
                    }
                }
                if (venueError) {
                    Text(
                        text = "Enter venue or location",
                        color = Color(0xFFDE3757),
                        fontSize = 11.sp
                    )
                }
            }

            // 3. Start Date & End Date (Row)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Start Date
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row {
                        Text(
                            text = "Start Date ",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF374151)
                        )
                        Text(
                            text = "*",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFEF4444)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .border(
                                1.dp,
                                if (startDateError) Color(0xFFFF7F7F)
                                else Color(0xFFE5E7EB),
                                RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                focusManager.clearFocus()
                                showStartDatePicker = true
                            }
                            .padding(horizontal = 14.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    tint = if (startDate.isEmpty()) Color(0xFF9CA3AF) else Color(0xFF6366F1),
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = startDate.ifEmpty { "Select start date" },
                                    color = if (startDate.isEmpty()) Color(0xFF9CA3AF) else Color(0xFF111827),
                                    fontSize = 13.sp,
                                    fontWeight = if (startDate.isEmpty()) FontWeight.Normal else FontWeight.Medium
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = Color(0xFF9CA3AF),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    if (startDateError) {
                        Text(
                            text = "Start date is required",
                            fontSize = 11.sp,
                            color = Color(0xFFEF4444)
                        )
                    }
                }

                // End Date
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row {
                        Text(
                            text = "End Date ",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF374151)
                        )
                        Text(
                            text = "*",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFEF4444)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .border(
                                1.dp,
                                if (endDateError) Color(0xFFFF7F7F)
                                else Color(0xFFE5E7EB),
                                RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                focusManager.clearFocus()
                                showEndDatePicker = true
                            }
                            .padding(horizontal = 14.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    tint = if (endDate.isEmpty()) Color(0xFF9CA3AF) else Color(0xFF6366F1),
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = endDate.ifEmpty { "Select end date" },
                                    color = if (endDate.isEmpty()) Color(0xFF9CA3AF) else Color(0xFF111827),
                                    fontSize = 13.sp,
                                    fontWeight = if (endDate.isEmpty()) FontWeight.Normal else FontWeight.Medium
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = Color(0xFF9CA3AF),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    if (endDateError) {
                        Text(
                            text = if (startDateMillis != null && endDate.isEmpty()) {
                                "End date must be on or after the start date"
                            } else {
                                "End date is required"
                            },
                            fontSize = 11.sp,
                            color = Color(0xFFEF4444)
                        )
                    }
                }
            }

            // 4. Status
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row {
                    Text(
                        text = "Status ",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF374151)
                    )
                    Text(
                        text = "*",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFEF4444)
                    )
                }
                ExposedDropdownMenuBox(
                    expanded = statusExpanded,
                    onExpandedChange = {
                        statusExpanded = !statusExpanded
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Box(
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .height(52.dp)
                            .background(
                                Color.White,
                                RoundedCornerShape(12.dp)
                            )
                            .border(
                                1.dp,
                                Color(0xFFE5E7EB),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 14.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {

                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )

                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = when (status) {
                                                "Upcoming" -> Color(0xFFEFF6FF)
                                                "Ongoing" -> Color(0xFFECFDF5)
                                                "Completed" -> Color(0xFFF3F4F6)
                                                else -> Color.Gray
                                            },
                                            shape = RoundedCornerShape(50)
                                        )
                                        .padding(
                                            horizontal = 12.dp,
                                            vertical = 4.dp
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = status,
                                        color = when (status) {
                                            "Upcoming" -> Color(0xFF3B82F6)
                                            "Ongoing" -> Color(0xFF10B981)
                                            "Completed" -> Color(0xFF6B7280)
                                            else -> Color.Gray
                                        },
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Select status",
                                tint = Color(0xFF9CA3AF),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    ExposedDropdownMenu(
                        expanded = statusExpanded,
                        onDismissRequest = {
                            statusExpanded = false
                        }
                    ) {

                        statusOptions.forEach { option ->

                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = option,
                                        fontSize = 14.sp
                                    )
                                },
                                onClick = {
                                    status = option
                                    statusExpanded = false
                                },
                                leadingIcon = {

                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(
                                                color = when (option) {
                                                    "Upcoming" -> Color(0xFF3B82F6)
                                                    "Ongoing" -> Color(0xFF10B981)
                                                    "Completed" -> Color(0xFF6B7280)
                                                    else -> Color.Gray
                                                },
                                                shape = RoundedCornerShape(50)
                                            )
                                    )
                                }
                            )
                        }
                    }
                }
                Text(
                    text = "Choose the current status of the exhibition.",
                    fontSize = 11.sp,
                    color = Color(0xFF6B7280)
                )
            }

            // 5. Description (Optional)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Description (Optional)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF374151)
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = Color(0xFF9CA3AF),
                                modifier = Modifier.size(20.dp)
                            )
                            BasicTextField(
                                value = description,
                                onValueChange = {
                                    if (it.length <= 500) {
                                        description = it
                                        // TODO: Needed logic for description length/content validation
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(90.dp),
                                textStyle = TextStyle(fontSize = 14.sp, color = Color(0xFF111827)),
                                decorationBox = { innerTextField ->
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.TopStart
                                    ) {
                                        if (description.isEmpty()) {
                                            Text(
                                                text = "Add exhibition details, agenda, or notes...",
                                                color = Color(0xFF9CA3AF),
                                                fontSize = 14.sp
                                            )
                                        }
                                        innerTextField()
                                    }
                                }
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = Color(0xFF9CA3AF),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${description.length}/500",
                                fontSize = 11.sp,
                                color = Color(0xFF9CA3AF)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
    if (showStartDatePicker) {

        val startDatePickerState = rememberDatePickerState(
            initialSelectedDateMillis = startDateMillis
        )

        DatePickerDialog(
            onDismissRequest = {
                showStartDatePicker = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedDate =
                            startDatePickerState.selectedDateMillis

                        if (selectedDate != null) {

                            startDateMillis = selectedDate
                            startDate = formatDate(selectedDate)

                            startDateError = false

                            // If the existing end date is before
                            // the newly selected start date,
                            // clear the end date.
                            if (
                                endDateMillis != null &&
                                endDateMillis!! < selectedDate
                            ) {
                                endDateMillis = null
                                endDate = ""
                                endDateError = true
                            }
                        }

                        showStartDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showStartDatePicker = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = startDatePickerState
            )
        }
    }

    if (showEndDatePicker) {

        val endDatePickerState = rememberDatePickerState(
            initialSelectedDateMillis = endDateMillis,
            selectableDates = object : SelectableDates {

                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return startDateMillis == null ||
                            utcTimeMillis >= startDateMillis!!
                }
            }
        )

        DatePickerDialog(
            onDismissRequest = {
                showEndDatePicker = false
            },
            confirmButton = {
                TextButton(
                    onClick = {

                        val selectedDate =
                            endDatePickerState.selectedDateMillis

                        if (selectedDate != null) {

                            if (
                                startDateMillis == null ||
                                selectedDate >= startDateMillis!!
                            ) {
                                endDateMillis = selectedDate
                                endDate = formatDate(selectedDate)
                                endDateError = false

                                showEndDatePicker = false
                            }
                        }
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showEndDatePicker = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = endDatePickerState
            )
        }
    }
}
