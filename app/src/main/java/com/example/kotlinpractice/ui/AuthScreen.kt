package com.example.kotlinpractice.ui

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

enum class ToggleScreen {
    Login,
    Register,
    Dashboard,
    AllCustomers,
//    Reports

}

@Composable
fun AuthScreen() {

    val customersList = remember {
        mutableStateListOf(
            Customer(
                name = "Case",
                company = "ABC Technologies",
                phone = "+91 98765 43210",
                email = "case@abctech.com",

                time = "Today, 10:24 AM",
                status = "New",
                statusColor = Color(0xFF10B981),
                statusBg = Color(0xFFECFDF5),
                tags = listOf("Interested", "Website")
            ),

            Customer(
                name = "Fred",
                company = "ABC Technologies",
                phone = "+91 98123 45678",
                email = "fred@abctech.com",

                time = "Today, 09:45 AM",
                status = "New",
                statusColor = Color(0xFF10B981),
                statusBg = Color(0xFFECFDF5),
                tags = listOf("New Lead", "Exhibition")
            ),

            Customer(
                name = "Rahul Sharma",
                company = "ABC Technologies",
                phone = "+91 99887 66554",
                email = "rahul@abctech.com",

                time = "Today, 10:24 AM",
                status = "New",
                statusColor = Color(0xFF10B981),
                statusBg = Color(0xFFECFDF5),
                tags = listOf("Interested", "Follow Up")
            ),

            Customer(
                name = "Priya Mehta",
                company = "XYZ Solutions",
                phone = "+91 98765 12345",
                email = "priya@xyzsolutions.com",

                time = "Today, 09:15 AM",
                status = "Follow Up",
                statusColor = Color(0xFFF59E0B),
                statusBg = Color(0xFFFEF3C7),
                tags = listOf("Follow Up", "Product Demo")
            ),

            Customer(
                name = "Aman Gupta",
                company = "TechWorld",
                phone = "+91 91234 56789",
                email = "aman@techworld.com",

                time = "Yesterday, 04:32 PM",
                status = "Confirmed",
                statusColor = Color(0xFF3B82F6),
                statusBg = Color(0xFFEFF6FF),
                tags = listOf("Confirmed", "Premium")
            ),

            Customer(
                name = "Sneha Verma",
                company = "Global Enterprises",
                phone = "+91 97654 32109",
                email = "sneha@globalenterprises.com",

                time = "Yesterday, 11:20 AM",
                status = "New",
                statusColor = Color(0xFF10B981),
                statusBg = Color(0xFFECFDF5),
                tags = listOf("New Lead", "Business")
            ),

            Customer(
                name = "Vikram Singh",
                company = "Innovate Corp",
                phone = "+91 98989 76767",
                email = "vikram@innovatecorp.com",

                time = "2 days ago",
                status = "Follow Up",
                statusColor = Color(0xFFF59E0B),
                statusBg = Color(0xFFFEF3C7),
                tags = listOf("Follow Up", "Interested")
            )
        )
    }

    var currentScreen by remember {
        mutableStateOf(ToggleScreen.Login)
    }

    when (currentScreen) {

        ToggleScreen.Login -> Login(
            onNavigateToRegister = {
                currentScreen = ToggleScreen.Register
            },
            onNavigateToDashboard = {
                currentScreen = ToggleScreen.Dashboard
            }
        )

        ToggleScreen.Register -> Register(
            onNavigateToLogin = {
                currentScreen = ToggleScreen.Login
            }
        )

        ToggleScreen.Dashboard, ToggleScreen.AllCustomers -> {
            MainScreen(
                currentScreen = currentScreen,
                customersList = customersList,
                onNavigate = {
                    currentScreen = it
                },
                onLogout = {
                    currentScreen = ToggleScreen.Login
                },
                onRemoveCustomer = {
                    customersList.remove(it)
                }
            )
        }

//        ToggleScreen.Reports -> ToggleScreen.Reports(
//            onNavigateBack = {
//                currentScreen = ToggleScreen.Dashboard
//            }
//        )
    }
}

