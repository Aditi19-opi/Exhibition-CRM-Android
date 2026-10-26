package com.example.kotlinpractice.ui

import android.os.Build
import android.system.Os.remove
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Notifications
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    currentScreen: ToggleScreen,
    customersList: List<Customer>,
    exhibitions: MutableList<Exhibition>,
    selectedCustomerExhibition: String?,
    onViewCustomers: (String) -> Unit,
    onNavigate: (ToggleScreen) -> Unit,
    onLogout: () -> Unit,
    onRemoveCustomer: (Customer) -> Unit,
    onBack: () -> Unit,

) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.fillMaxWidth(0.75f)
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Exhibition CRM",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = 1.dp,
                    color = Color.LightGray.copy(alpha = 0.5f)
                )

                // MENU ITEMS
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Dashboard,
                            contentDescription = "Dashboard",
                            tint = Color(0xFF6366F1)
                        )
                    },
                    label = {
                        Text(
                            "Dashboard",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    selected = currentScreen == ToggleScreen.Dashboard,
                    onClick = {
                        onNavigate(ToggleScreen.Dashboard)
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.People,
                            contentDescription = "Customers",
                            tint = Color(0xFF6366F1)
                        )
                    },
                    label = { Text("Customers", fontSize = 15.sp) },
                    selected = currentScreen == ToggleScreen.AllCustomers,
                    onClick = {
                        onNavigate(ToggleScreen.AllCustomers)
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Business,
                            contentDescription = "Exhibition",
                            tint = Color(0xFF6366F1)
                        )
                    },
                    label = { Text("Exhibition", fontSize = 15.sp) },
                    selected = currentScreen == ToggleScreen.Exhibition,
                    onClick = {
                        onNavigate(ToggleScreen.Exhibition)
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout",
                            tint = Color(0xFFEF4444)
                        )
                    },
                    label = { Text("Logout", fontSize = 15.sp, color = Color(0xFFEF4444)) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onLogout()
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Spacer(modifier = Modifier.weight(1f))
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Exhibition CRM",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    },

                    navigationIcon = {
                        when (currentScreen) {
                            ToggleScreen.Dashboard ->
                                IconButton(
                                    onClick = {
                                        scope.launch {
                                            drawerState.open()
                                        }
                                    }
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(
                                                color = Color(0xFFEEF0FF),
                                                shape = RoundedCornerShape(10.dp)
                                            ), contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Menu,
                                            contentDescription = "Menu",
                                            tint = Color(0xFF6366F1),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                            ToggleScreen.AllCustomers ->
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
                                            ), contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Menu",
                                            tint = Color(0xFF6366F1),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            ToggleScreen.Exhibition ->
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
                                            ), contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Menu",
                                            tint = Color(0xFF6366F1),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                            else -> {}
                        }
                    },

                    actions = {

                        IconButton(onClick = {}) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(color = Color(0xFFF3F4F6), shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Notifications,
                                    contentDescription = "Notifications",
                                    tint = Color(0xFF374151),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        IconButton(onClick = {}) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(color = Color(0xFFEEF0FF), shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Profile",
                                    tint = Color(0xFF6366F1),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier.padding(innerPadding)
            ) {

                when (currentScreen) {

                    ToggleScreen.Dashboard -> {
                        Dashboard(
                            onLogout = onLogout,
                            onNavigateToCustomers = {
                                onNavigate(ToggleScreen.AllCustomers)
                            },
                            onNavigateToAddCustomer = {
                                onNavigate(ToggleScreen.AddCustomer)
                            },
                            onNavigateToExhibition = {
                                onNavigate(ToggleScreen.Exhibition)
                            },
                            customersList = customersList
                        )
                    }

                    ToggleScreen.AllCustomers -> {
                        AllCustomers(
                            customers = customersList as MutableList<Customer>,
                            exhibitions = exhibitions,
                            onNavigateToAddCustomer = {
                                onNavigate(ToggleScreen.AddCustomer)
                            },
                            selectedExhibition = selectedCustomerExhibition
                        )

                    }


                    ToggleScreen.Exhibition -> Exhibition(
                        exhibition = exhibitions,
                        customer = customersList,
                        onViewCustomers = onViewCustomers,
                        onNavigateToAddExhibition ={
                            onNavigate(ToggleScreen.AddExhibition)
                        }

                    )
                    else -> {}
                }
            }
        }
    }
}