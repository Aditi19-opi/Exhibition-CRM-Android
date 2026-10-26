package com.example.kotlinpractice.ui
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PersonAddAlt
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinpractice.network.CustomerApi
import kotlinx.coroutines.launch
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllCustomers(
    customers: MutableList<Customer>,
    onNavigateToAddCustomer: () -> Unit,
    exhibitions: List<Exhibition>,
    selectedExhibition: String? = null
) {
    var customerToEdit by remember { mutableStateOf<Customer?>(null) }
    var customerToDelete by remember { mutableStateOf<Customer?>(null) }

    val scope = rememberCoroutineScope()
    var saveError by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    var selectedStatuses by remember { mutableStateOf(setOf("New", "Follow Up", "Confirmed")) }
    var selectedExhibitionFilter by remember(selectedExhibition) {
        mutableStateOf(selectedExhibition ?: "All Exhibitions")
    }
    var tempSelectedStatuses by remember { mutableStateOf(selectedStatuses) }
    var tempExhibition by remember { mutableStateOf(selectedExhibitionFilter) }

    var showFilter by remember { mutableStateOf(false) }
    var showExhibitionDropdown by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }

    val activeFilterCount = remember(selectedStatuses, selectedExhibitionFilter) {
        var count = 0
        if (selectedStatuses.size < 3) count += (3 - selectedStatuses.size)
        if (selectedExhibitionFilter != "All Exhibitions") count += 1
        count
    }

    val filterCustomer = customers.filter {
        val matchesSearch =
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.phone.contains(searchQuery, ignoreCase = true) ||
                    it.exhibition.contains(searchQuery, ignoreCase = true)

        val matchesStatus =
            selectedStatuses.isEmpty() ||
                    it.status in selectedStatuses

        val matchesExhibition =
            selectedExhibitionFilter == "All Exhibitions" ||
                    it.exhibition == selectedExhibitionFilter

        matchesSearch &&
                matchesStatus &&
                matchesExhibition
    }

    var currentPage by remember { mutableIntStateOf(1) }

    LaunchedEffect(selectedStatuses, selectedExhibitionFilter, searchQuery) {
        currentPage = 1
    }

    val pageSize = 5

    val totalPages = kotlin.math.max(
        1,
        (filterCustomer.size + pageSize - 1) / pageSize
    )

    val safePage = currentPage.coerceIn(1, totalPages)

    val paginated = filterCustomer
        .drop((safePage - 1) * pageSize)
        .take(pageSize)


    val interactionSource = remember { MutableInteractionSource() }
    val focusManager = LocalFocusManager.current

    val totalCount = customers.size
    val newCount = customers.count { it.status.equals("New", ignoreCase = true) }
    val followCount = customers.count { it.status.equals("Follow Up", ignoreCase = true) }
    val confirmedCount = customers.count { it.status.equals("Confirmed", ignoreCase = true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = interactionSource
            ) {
                focusManager.clearFocus()
            }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,

                ) {
                Column {
                    Text(text = "All Customers", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "Manage and track all your exhibition customers",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Gray
                    )
                }
                Button(
                    onClick = {
                        // add customer logic
                        onNavigateToAddCustomer()
                    }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonAddAlt,
                        contentDescription = "Add customers",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Add Customers", fontSize = 13.sp)
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                MetricCards(
                    title = "Total",
                    value = totalCount,
                    icon = Icons.Default.PersonAddAlt,
                    color = Color(0xFF6366F1),
                    modifier = Modifier.weight(1f)
                )

                MetricCards(
                    title = "New",
                    value = newCount,
                    icon = Icons.Default.StarOutline,
                    color = Color(0xFF10B981),
                    modifier = Modifier.weight(1f)
                )

                MetricCards(
                    title = "Follow Up",
                    value = followCount,
                    icon = Icons.Default.AccessTime,
                    color = Color(0xFFF59E0B),
                    modifier = Modifier.weight(1f)
                )

                MetricCards(
                    title = "Confirmed",
                    value = confirmedCount,
                    icon = Icons.Default.CheckCircleOutline,
                    color = Color(0xFF3B82F6),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(2.dp))
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
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
                                text = "Search by name, exhibition or phone...",
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
                Box {
                    OutlinedButton(
                        onClick = {
                            tempSelectedStatuses = selectedStatuses
                            tempExhibition = selectedExhibitionFilter
                            showFilter = true
                        },
                        modifier = Modifier.height(40.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color(0xFFEEF2FF),
                            contentColor = Color(0xFF4F46E5)
                        ),
                        border = BorderStroke(1.5.dp, Color(0xFFC7D2FE)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterAlt,
                            contentDescription = "filter",
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFF6366F1)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Filter",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF4F46E5)
                        )
                        if (activeFilterCount > 0) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .background(Color(0xFF6366F1), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = activeFilterCount.toString(),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = if (showFilter) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "Dropdown",
                            modifier = Modifier.size(18.dp),
                            tint = Color(0xFF6366F1)
                        )
                    }

                    DropdownMenu(
                        expanded = showFilter,
                        onDismissRequest = { showFilter = false },
                        modifier = Modifier
                            .width(300.dp)
                            .background(Color.White, RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Header Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Filter Customers",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E1B4B)
                                )
                                TextButton(onClick = {
                                    tempSelectedStatuses =
                                        setOf("New", "Follow Up", "Confirmed")
                                    tempExhibition = "All Exhibitions"
                                    selectedStatuses = tempSelectedStatuses
                                    selectedExhibitionFilter = tempExhibition
                                    showFilter = false
                                }) {
                                    Text(
                                        text = "✕ Clear Filter",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF6366F1),
                                    )
                                }
                            }

                            // Status Section
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Status",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E1B4B)
                                    )
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowUp,
                                        contentDescription = null,
                                        tint = Color(0xFF6B7280),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                val statusItems = listOf(
                                    Triple("New", Color(0xFF10B981), Color(0xFFECFDF5)),
                                    Triple("Follow Up", Color(0xFFF59E0B), Color(0xFFFEF3C7)),
                                    Triple("Confirmed", Color(0xFF3B82F6), Color(0xFFEFF6FF))
                                )

                                statusItems.forEach { (status, textColor, bgColor) ->
                                    val isChecked = tempSelectedStatuses.contains(status)
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                tempSelectedStatuses = if (isChecked) {
                                                    tempSelectedStatuses - status
                                                } else {
                                                    tempSelectedStatuses + status
                                                }
                                            }
                                            .padding(vertical = 2.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(
                                                    if (isChecked) Color(0xFF6366F1) else Color(
                                                        0xFFE5E7EB
                                                    )
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (isChecked) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Box(
                                            modifier = Modifier
                                                .background(bgColor, RoundedCornerShape(8.dp))
                                                .padding(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = status,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = textColor
                                            )
                                        }
                                    }
                                }
                            }

                            // Exhibition Section
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Exhibition",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E1B4B)
                                    )
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowUp,
                                        contentDescription = null,
                                        tint = Color(0xFF6B7280),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Box(modifier = Modifier.fillMaxWidth()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(42.dp)
                                            .background(
                                                Color(0xFFF8FAFC),
                                                RoundedCornerShape(12.dp)
                                            )
                                            .border(
                                                1.dp,
                                                Color(0xFFE2E8F0),
                                                RoundedCornerShape(12.dp)
                                            )
                                            .clickable {
                                                showExhibitionDropdown = !showExhibitionDropdown
                                            }
                                            .padding(horizontal = 12.dp),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = tempExhibition,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = Color(0xFF1E1B4B),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Icon(
                                                imageVector = Icons.Default.UnfoldMore,
                                                contentDescription = "Select",
                                                tint = Color(0xFF64748B),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }

                                    DropdownMenu(
                                        expanded = showExhibitionDropdown,
                                        onDismissRequest = { showExhibitionDropdown = false },
                                        modifier = Modifier.heightIn(max = 200.dp)
                                    ) {
                                        val exhibitionsList =
                                            listOf("All Exhibitions") + customers.map { it.exhibition }
                                                .distinct()
                                        exhibitionsList.forEach { item ->
                                            DropdownMenuItem(
                                                text = { Text(text = item, fontSize = 13.sp) },
                                                onClick = {
                                                    tempExhibition = item
                                                    showExhibitionDropdown = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            // Apply Filters Button
                            Button(
                                onClick = {
                                    selectedStatuses = tempSelectedStatuses
                                    selectedExhibitionFilter = tempExhibition
                                    showFilter = false
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF6366F1)
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FilterAlt,
                                    contentDescription = "Apply",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Apply Filters",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
            if (paginated.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No customer found",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )
                }

            } else {

                CustomerCardItems(
                    customers = paginated,
                    modifier = Modifier.weight(1f),
                    onEdit = { customer ->
                        customerToEdit = customer
                    },
                    onRemoveCustomer = { customer ->
                        customerToDelete = customer
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                Pagination(
                    currentPage = safePage,
                    totalPages = totalPages,
                    onPrevious = {
                        if (currentPage > 1) {
                            currentPage--
                        }
                    },
                    onNext = {
                        if (currentPage < totalPages) {
                            currentPage++
                        }
                    }
                )
            }

            if (customerToEdit != null) {
                EditCustomers(
                    customers = customerToEdit!!,
                    exhibitions = exhibitions,
                    isSaving = isSaving,
                    onDismiss = {
                        customerToEdit = null
                        saveError = null
                    },
                    onSave = { updatedCustomer, exhibitionId ->
                        if (!isSaving) {
                            scope.launch {
                                isSaving = true
                                saveError = null

                                val index = customers.indexOfFirst {
                                    it.id == updatedCustomer.id
                                }

                                if (index != -1) {
                                    customers[index] = updatedCustomer
                                }

                                customerToEdit = null

                                try {
                                    CustomerApi.updateCustomer(
                                        updatedCustomer,
                                        exhibitionId
                                    )
                                } catch (e: Exception) {
                                    Log.e(
                                        "CustomerUpdate",
                                        "Failed to update customer on server",
                                        e
                                    )
                                } finally {
                                    isSaving = false
                                }
                            }
                        }
                    }
                )

                if (saveError != null) {
                    AlertDialog(
                        onDismissRequest = { saveError = null },
                        title = { Text("Update Failed") },
                        text = { Text(saveError ?: "Unknown error") },
                        confirmButton = {
                            TextButton(
                                onClick = { saveError = null }
                            ) {
                                Text("OK")
                            }
                        }
                    )
                }
            }



            if (customerToDelete != null) {
                DeleteDialog(
                    customer = customerToDelete!!,
                    onDismiss = {
                        if (!isSaving) {
                            customerToDelete = null
                        }
                    },
                    onConfirm = {
                        if (!isSaving) {
                            val customer = customerToDelete!!

                            scope.launch {
                                isSaving = true
                                saveError = null

                                try {
                                    // 1. Delete from PostgreSQL through the API
                                    CustomerApi.deleteCustomer(customer.id)

                                    // 2. Remove from the local list after success
                                    customers.removeAll { it.id == customer.id }

                                    // 3. Close the dialog
                                    customerToDelete = null
                                } catch (e: Exception) {
                                    Log.e(
                                        "CustomerDelete",
                                        "Failed to delete customer",
                                        e
                                    )

                                    saveError = e.message ?: "Failed to delete customer"
                                } finally {
                                    isSaving = false
                                }
                            }
                        }
                    }
                )
            }

        }
    }
}

//metric cards
@Composable
fun MetricCards(
    title: String,
    value: Int,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(54.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.035f)
        ),
        border = BorderStroke(
            width = 1.dp,
            color = color.copy(alpha = 0.10f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 9.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            // Icon
            Box(
                modifier = Modifier
                    .size(27.dp)
                    .background(
                        color = color.copy(alpha = 0.10f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(15.dp)
                )
            }

            // Text
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF6B7280),
                    maxLines = 1
                )

                Text(
                    text = value.toString(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = color,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun CustomerCardItems(
    customers: List<Customer>,
    onEdit: (Customer) -> Unit,
    onRemoveCustomer: (Customer) -> Unit,
    modifier: Modifier = Modifier,
) {
    val avatarColors = listOf(
        Color(0xFF6278F1),
        Color(0xFF10B981),
        Color(0xFFF59E0B),
        Color(0xFFEC4899),
        Color(0xFF03A9F4),
        Color(0xFFE91E63),
        Color(0xFF673AB7),
        Color(0xFFE040FB),
        Color(0xFF64FFDA),
    )
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 0.dp,
            bottom = 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(customers) { customer ->
            val accentColor =
                avatarColors[kotlin.math.abs(customer.name.hashCode()) % avatarColors.size]

            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(
                                        accentColor.copy(alpha = 0.34f),
                                        RoundedCornerShape(10.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = customer.name.take(1).uppercase(), fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Column {
                                Text(
                                    text = customer.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
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
                        Column(horizontalAlignment = Alignment.End) {
                            Box(
                                modifier = Modifier
                                    .background(customer.statusBg, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = customer.status,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = customer.statusColor
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = customer.time, fontSize = 11.sp, color = Color(0xFF9CA3AF))
                        }
                    }
//                    Spacer(modifier = Modifier.height(3.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(1.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    Icons.Default.Phone,
                                    contentDescription = "Phone",
                                    tint = Color(0xFF9CA3AF),
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = customer.phone,
                                    fontSize = 11.sp,
                                    color = Color(0xFF4B5563)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    Icons.Default.Email,
                                    contentDescription = "Email",
                                    tint = Color(0xFF9CA3AF),
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = customer.email,
                                    fontSize = 11.sp,
                                    color = Color(0xFF4B5563)
                                )
                            }
                            if (!customer.note.isNullOrBlank()) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Notes,
                                        contentDescription = "remark",
                                        tint = Color(0xFF9CA3AF),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = customer.note,
                                        fontSize = 11.sp,
                                        color = Color(0xFF4B5563)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFFEEF2FF))
                                            .clickable { onEdit(customer) }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Edit,
                                                contentDescription = "Edit",
                                                tint = Color(0xFF6278F1),
                                                modifier = Modifier.size(11.dp)
                                            )
                                            Text(
                                                text = "Edit",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF6278F1)
                                            )
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFFFEF2F2))
                                            .clickable { onRemoveCustomer(customer) }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Delete",
                                                tint = Color(0xFFEF4444),
                                                modifier = Modifier.size(11.dp)
                                            )
                                            Text(
                                                text = "Delete",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFEF4444)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EditCustomers(
    exhibitions: List<Exhibition>,
    customers: Customer,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: (Customer, String) -> Unit
) {
    var name by remember { mutableStateOf(customers.name) }
    var selectedExhibitionId by remember(customers.id) {
        mutableStateOf(
            exhibitions.firstOrNull {
                it.name.trim().equals(
                    customers.exhibition.trim(),
                    ignoreCase = true
                )
            }?.id?.toString() ?: ""
        )
    }

    var showExhibitionMenu by remember { mutableStateOf(false) }
    var phone by remember { mutableStateOf(customers.phone) }
    var status by remember { mutableStateOf(customers.status) }
    var statusEdit by remember { mutableStateOf(false) }
    val availableStatus = remember { listOf("New", "Follow Up", "Confirmed") }


    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text(
                "Edit Customer Details",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },

        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Customer Name") },
                    singleLine = true
                )


                Box(modifier = Modifier.fillMaxWidth()) {

                    val selectedExhibition = exhibitions.firstOrNull {
                        it.id.toString() == selectedExhibitionId
                    }

                    OutlinedTextField(
                        value = selectedExhibition?.name
                            ?: customers.exhibition.ifBlank {
                                "Select exhibition"
                            },
                        onValueChange = {},
                        label = { Text("Exhibition") },
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Select exhibition"
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showExhibitionMenu = true
                            }
                    )

                    DropdownMenu(
                        expanded = showExhibitionMenu,
                        onDismissRequest = {
                            showExhibitionMenu = false
                        }
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
                            exhibitions.forEach { exhibition ->
                                DropdownMenuItem(
                                    text = { Text(exhibition.name) },
                                    onClick = {
                                        selectedExhibitionId = exhibition.id.toString()
                                        showExhibitionMenu = false
                                    }
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    singleLine = true
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
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank()) {
                        return@Button
                    }

                    val selectedExhibition = exhibitions.firstOrNull {
                        it.id.toString() == selectedExhibitionId
                    } ?: exhibitions.firstOrNull()

                    val exhibitionName = selectedExhibition?.name ?: customers.exhibition.ifBlank { "India Tech Expo" }
                    val exhibitionIdToUse = selectedExhibition?.id?.toString() ?: "1"

                    val (statusColor, statusBg) = getStatusColors(status)

                    val updated = customers.copy(
                        name = name.trim(),
                        exhibition = exhibitionName,
                        phone = phone.trim(),
                        status = status,
                        statusColor = statusColor,
                        statusBg = statusBg
                    )

                    onSave(updated, exhibitionIdToUse)
                },
                modifier = Modifier.height(44.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                enabled = !isSaving
            ) {
                Text(if (isSaving) "Saving..." else "Save Changes", fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.height(44.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cancel", color = Color(0xFF374151), fontWeight = FontWeight.Medium)
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = Color.White
    )
}

fun getStatusColors(status: String): Pair<Color, Color> {
    return when (status) {
        "New" -> Pair(
            Color(0xFF10B981),
            Color(0xFFECFDF5)
        )

        "Follow Up" -> Pair(
            Color(0xFFF59E0B),
            Color(0xFFFEF3C7)
        )

        "Confirmed" -> Pair(
            Color(0xFF3B82F6),
            Color(0xFFEFF6FF)
        )

        else -> Pair(
            Color.Gray,
            Color.LightGray
        )
    }
}

@Composable
fun DeleteDialog(
    customer: Customer,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Customer?", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
        text = {
            Text(
                text = buildAnnotatedString {
                    append("Are you sure you want to delete")

                    withStyle(
                        style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    ) {
                        append(" ${customer.name}")
                    }
                    append("?")
                },
                color = Color(0xFF4B5563),
                fontSize = 14.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)) // Red delete button
            ) {
                Text("Delete", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color(0xFF6B7280))
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun Pagination(
    currentPage: Int,
    totalPages: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        OutlinedButton(
            onClick = onPrevious,
            enabled = currentPage > 1,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Previous", fontSize = 12.sp)
        }

        Text(
            text = "Page $currentPage of $totalPages",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF6B7280)
        )

        OutlinedButton(
            onClick = onNext,
            enabled = currentPage < totalPages,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Next", fontSize = 12.sp)
        }
    }
}