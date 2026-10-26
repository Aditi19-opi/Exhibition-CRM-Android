package com.example.kotlinpractice.ui

import android.util.Patterns
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PersonAddAlt
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Phone
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun AddCustomer(
    customers: List<Customer>,
    exhibitions: List<Exhibition>,
    selectedExhibitionId: String?,
    onExhibitionSelected: (String) -> Unit,
    onBack: () -> Unit,
    onSave: (Customer, String, (Boolean) -> Unit) -> Unit
) {

    var selectedExhibitionId by remember {
        mutableStateOf(selectedExhibitionId ?: exhibitions.firstOrNull()?.id?.toString().orEmpty())
    }

    LaunchedEffect(exhibitions, selectedExhibitionId) {
        if (selectedExhibitionId.isBlank() && exhibitions.isNotEmpty()) {
            selectedExhibitionId = exhibitions.first().id.toString()
            onExhibitionSelected(selectedExhibitionId)
        }
    }

    val exhibition = exhibitions
        .firstOrNull { it.id.toString() == selectedExhibitionId }
        ?.name.orEmpty()


    val exhibitionInteractionSource = remember {
        MutableInteractionSource()
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf("New") }

    val defaultDate = remember {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        "Today, ${sdf.format(Date())}"
    }

    var addedDate by remember { mutableStateOf(defaultDate) }
    var note by remember { mutableStateOf("") }

    var showExhibitionMenu by remember { mutableStateOf(false) }
    var isSubmitted by remember { mutableStateOf(false) }

    val interactionSource = remember { MutableInteractionSource() }
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    val nameError = isSubmitted && name.isBlank()
    val phoneError = isSubmitted && phone.isBlank()
    val phoneInvalidError =
        isSubmitted && phone.isNotBlank() && (!Patterns.PHONE.matcher(phone)
            .matches() || phone.first() == '0')
    val emailError = isSubmitted && email.isBlank()
    val emailInvalidError =
        isSubmitted && email.isNotBlank() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()


    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        },
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFF0F0F3),
                    )
            ) {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    title = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    top = 12.dp,
                                    bottom = 16.dp,
                                    end = 16.dp
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = "Add Customer",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    lineHeight = 22.sp
                                )

                                Text(
                                    text = "Save customer details and track their journey",
                                    fontSize = 12.sp,
                                    lineHeight = 14.sp,
                                    color = Color(0xFF6B7280)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

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
                                    imageVector = Icons.Default.PersonAdd,
                                    contentDescription = "Add Customer",
                                    tint = Color(0xFF6278F1),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    },

                    navigationIcon = {
                        IconButton(
                            onClick = {
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
            }
        }
    ) { innerPadding ->
        Card(
            modifier = Modifier
                .padding(innerPadding)
//                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = interactionSource
                ) {
                    focusManager.clearFocus()
                },
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = 0.dp,
                bottomEnd = 0.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Scrollable Form Area
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Full Name
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Full Name ",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF374151)
                            )
                            Text(
                                text = "*",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEF4444)
                            )
                        }
                        CustomerTextField(
                            value = name,
                            onValueChange = {
                                name = it.replaceFirstChar { char ->
                                    if (char.isLowerCase()) {
                                        char.titlecase()
                                    } else {
                                        char.toString()
                                    }
                                }
                            },
                            placeholder = "Enter customer name",
                            icon = Icons.Default.PersonOutline,
                            isError = nameError,
                            errorMessage = "Name is required"
                        )

                    }

                    // Phone & Email Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Phone input
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Phone Number",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF374151)
                                )
                                Text(
                                    text = " *",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFEF4444)
                                )
                            }
                            CustomerTextField(
                                value = phone,
                                onValueChange = {
                                    if (it.all { char -> char.isDigit() } && it.length <= 10) {
                                        phone = it
                                    }
                                },
                                placeholder = "+91 9876543210",
                                icon = Icons.Default.Phone,
                                isError = phoneError || phoneInvalidError,
                                errorMessage = when {
                                    phoneError -> "Phone number is required"
                                    phoneInvalidError -> "Enter a valid phone number"
                                    else -> ""
                                }
                            )

                        }
                        // Email input
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Email Address",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF374151),
                                    softWrap = false,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = " *",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFEF4444)
                                )
                            }
                            CustomerTextField(
                                value = email,
                                onValueChange = { email = it },
                                placeholder = "example@gmail.com",
                                icon = Icons.Default.MailOutline,
                                isError = emailError || emailInvalidError,
                                errorMessage = when {
                                    emailError -> "Email is required"
                                    emailInvalidError -> "Enter a valid email address"
                                    else -> ""
                                }
                            )

                        }
                    }

                    // Exhibition Section
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Exhibition",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF374151)
                            )
                            Text(
                                text = " *",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEF4444)
                            )
                        }
                        Box(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            BasicTextField(
                                value = exhibition,
                                onValueChange = { },
                                enabled = true,
                                readOnly = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .clickable { showExhibitionMenu = !showExhibitionMenu },
                                textStyle = TextStyle(
                                    fontSize = 14.sp,
                                    color = Color.Black
                                ),
                                singleLine = true,
                                interactionSource = exhibitionInteractionSource
                            ) { innerTextField ->
                                OutlinedTextFieldDefaults.DecorationBox(
                                    value = exhibition,
                                    innerTextField = innerTextField,
                                    enabled = true,
                                    singleLine = true,
                                    visualTransformation = VisualTransformation.None,
                                    interactionSource = interactionSource,
                                    placeholder = {
                                        Text(
                                            "Select or enter exhibition",
                                            fontSize = 13.sp,
                                            color = Color(0xFF9CA3AF)
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Business,
                                            contentDescription = "business",
                                            tint = Color(
                                                0xFF9CA3AF
                                            ),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    },
                                    trailingIcon = {
                                        IconButton(onClick = {
                                            showExhibitionMenu = !showExhibitionMenu
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.ArrowDropDown,
                                                contentDescription = "Dropdown Arrow",
                                                tint = Color(0xFF9CA3AF)
                                            )
                                        }
                                    },
                                    contentPadding = OutlinedTextFieldDefaults.contentPadding(
                                        start = 12.dp,
                                        end = 12.dp,
                                        top = 6.dp,
                                        bottom = 0.dp
                                    ),
                                    container = {
                                        OutlinedTextFieldDefaults.Container(
                                            enabled = true,
                                            isError = false,
                                            interactionSource = interactionSource,
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = Color(0xFFDCDFFF),
                                                unfocusedBorderColor = Color(0xFFE6E7FA),
                                                unfocusedContainerColor = Color(0xFFF0F1FD)
                                            ),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                    }
                                )
                            }

                            DropdownMenu(
                                expanded = showExhibitionMenu,
                                onDismissRequest = { showExhibitionMenu = false },
                                modifier = Modifier.heightIn(max = 200.dp)
                            ) {
                                if (exhibitions.isEmpty()) {
                                    DropdownMenuItem(
                                        text = {
                                            Text("No exhibitions available")
                                        },
                                        onClick = {
                                            showExhibitionMenu = false
                                        }
                                    )
                                } else {
                                    exhibitions.forEach { item ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(item.name)
                                            },
                                            onClick = {
                                                selectedExhibitionId = item.id.toString()
                                                onExhibitionSelected(selectedExhibitionId)
                                                showExhibitionMenu = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Text(
                            text = "Selected exhibition will be added to the customer's details.",
                            fontSize = 11.sp,
                            color = Color(0xFF9CA3AF)
                        )

                    }

                    // Status Tabs Section (Defaults to New with Green color)
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Status",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF374151)
                            )
                            Text(
                                text = " *",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEF4444)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = Color(0xFFECFDF5),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .border(
                                        width = 1.dp,
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color(0xFF10B981)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = selectedStatus,
                                    fontSize = 13.sp,
                                    color = Color(0xFF10B981)
                                )
                            }
                        }


                    }

                    // Added Date Section
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Added Date",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF374151)
                            )
                            Text(
                                text = " *",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEF4444)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .background(
                                    color = Color(0xFFF9FAFB),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = Color(0xFFE5E7EB),
                                    shape = RoundedCornerShape(12.dp)
                                )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = "Date",
                                    tint = Color(0xFF9CA3AF),
                                    modifier = Modifier.size(18.dp)
                                )

                                Spacer(modifier = Modifier.width(10.dp))

                                Text(
                                    text = addedDate,
                                    fontSize = 14.sp,
                                    color = Color(0xFF6B7280)
                                )
                            }
                        }

                    }

                    // Note (Optional) Section
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Remark ",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF374151)
                            )
                            Text(
                                text = "(Optional)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFF9CA3AF)
                            )
                        }
                        BasicTextField(
                            value = note,
                            onValueChange = { note = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            textStyle = TextStyle(
                                fontSize = 14.sp,
                                color = Color.Black
                            ),
                            singleLine = false,
                            interactionSource = interactionSource
                        ) { innerTextField ->
                            OutlinedTextFieldDefaults.DecorationBox(
                                value = note,
                                innerTextField = innerTextField,
                                enabled = true,
                                singleLine = false,
                                visualTransformation = VisualTransformation.None,
                                interactionSource = interactionSource,
                                placeholder = {
                                    Text(
                                        "Add any additional customer details...",
                                        fontSize = 13.sp,
                                        color = Color(0xFF9CA3AF)
                                    )
                                },
                                contentPadding = OutlinedTextFieldDefaults.contentPadding(
                                    start = 12.dp,
                                    end = 12.dp,
                                    top = 10.dp,
                                    bottom = 10.dp
                                ),
                                container = {
                                    OutlinedTextFieldDefaults.Container(
                                        enabled = true,
                                        isError = false,
                                        interactionSource = interactionSource,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = Color(0xFFDCDFFF),
                                            unfocusedBorderColor = Color(0xFFE5E7EB)
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                }
                            )
                        }
                    }
                }

                HorizontalDivider(
                    color = Color(0xFFE5E7EB),
                    thickness = 1.dp
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF374151),
                            containerColor = Color(0xFF6366F1).copy(alpha = 0.10f)
                        ),
                        border = BorderStroke(1.dp, Color(0xFF6366F1).copy(alpha = 0.35f))
                    ) {
                        Text(text = "Back", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = {
                            isSubmitted = true

                            if (
                                selectedExhibitionId.isNotBlank() &&
                                exhibitions.any { it.id.toString() == selectedExhibitionId } &&
                                name.isNotBlank() &&
                                phone.length == 10 &&
                                phone.first() != '0' &&
                                email.isNotBlank() &&
                                Patterns.EMAIL_ADDRESS.matcher(email).matches()
                            ) {

                                val (statusColor, statusBg) = when (selectedStatus) {
                                    "New" -> Pair(Color(0xFF10B981), Color(0xFFECFDF5))
                                    "Follow Up" -> Pair(Color(0xFFF59E0B), Color(0xFFFEF3C7))
                                    else -> Pair(Color(0xFF3B82F6), Color(0xFFEFF6FF))
                                }

                                val newCustomer = Customer(
                                    name = name,
                                    exhibition = exhibition,
                                    phone = phone,
                                    email = email,
                                    time = addedDate,
                                    status = selectedStatus,
                                    statusColor = statusColor,
                                    statusBg = statusBg,
                                    note = note
                                )

                                onSave(newCustomer, selectedExhibitionId) { sucess ->
                                    scope.launch {
                                        if (sucess) {
                                            name = ""
                                            phone = ""
                                            email = ""
                                            note = ""
                                            isSubmitted = false
                                            focusManager.clearFocus()
                                            addedDate = "Today, ${
                                                SimpleDateFormat(
                                                    "hh:mm a",
                                                    Locale.getDefault()
                                                ).format(Date())
                                            }"
                                            snackbarHostState.showSnackbar(
                                                "Customer added successfully"
                                            )
                                        } else {
                                            snackbarHostState.showSnackbar(
                                                "Failed to add customer. Please try again"
                                            )
                                        }
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6366F1)
                        )

                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PersonAddAlt,
                                contentDescription = "Save",
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Save Customer",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isError: Boolean = false,
    errorMessage: String = "",
    singleLine: Boolean = true,
    height: androidx.compose.ui.unit.Dp = 48.dp
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(height),
            textStyle = TextStyle(
                fontSize = 14.sp,
                color = Color.Black
            ),
            singleLine = singleLine,
            interactionSource = interactionSource
        ) { innerTextField ->

            OutlinedTextFieldDefaults.DecorationBox(
                value = value,
                innerTextField = innerTextField,
                enabled = true,
                singleLine = singleLine,
                visualTransformation = VisualTransformation.None,
                interactionSource = interactionSource,

                placeholder = {
                    Text(
                        text = placeholder,
                        fontSize = 13.sp,
                        color = Color(0xFF9CA3AF),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },

                leadingIcon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color(0xFF9CA3AF),
                        modifier = Modifier.size(18.dp)
                    )
                },

                contentPadding = OutlinedTextFieldDefaults.contentPadding(
                    start = 12.dp,
                    end = 12.dp,
                    top = 0.dp,
                    bottom = 0.dp
                ),

                container = {
                    OutlinedTextFieldDefaults.Container(
                        enabled = true,
                        isError = isError,
                        interactionSource = interactionSource,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor =
                                if (isError)
                                    Color(0xFFFA99BB)
                                else
                                    Color(0xFFDCDFFF),

                            unfocusedBorderColor =
                                if (isError)
                                    Color(0xFFFA99BB)
                                else
                                    Color(0xFFE5E7EB)
                        ),
                        focusedBorderThickness = 1.dp,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            )
        }

        if (isError) {
            Text(
                text = errorMessage,
                fontSize = 11.sp,
                color = Color(0xFFEF4444),
                fontWeight = FontWeight.Medium
            )
        }
    }
}
