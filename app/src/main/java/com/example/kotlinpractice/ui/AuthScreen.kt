package com.example.kotlinpractice.ui

import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.edit
import com.example.kotlinpractice.network.ExhibitionApi
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.example.kotlinpractice.network.CustomerApi

enum class ToggleScreen {
    Login,
    Register,
    Dashboard,
    AllCustomers,
    AddCustomer,
    Exhibition,
    AddExhibition


}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AuthScreen() {
    val scope = rememberCoroutineScope()
    val customersList = remember {
        mutableStateListOf<Customer>()
    }

    var customersError by remember {
        mutableStateOf<String?>(null)
    }

    var isLoadingCustomers by remember {
        mutableStateOf(false)
    }

    val exhibitionsList = remember {
        mutableStateListOf<Exhibition>()
    }

    var exhibitionsError by remember {
        mutableStateOf<String?>(null)
    }

    val context = LocalContext.current

    val sharedPreferences = remember {
        context.getSharedPreferences("app_prefs", MODE_PRIVATE)
    }

    val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)

    var currentScreen by remember {
        mutableStateOf(
            if (isLoggedIn) {
                ToggleScreen.Dashboard
            } else {
                ToggleScreen.Login
            }
        )
    }

    suspend fun refreshExhibitions() {
        try {
            val result = ExhibitionApi.getExhibitions()

            exhibitionsList.clear()
            exhibitionsList.addAll(result)

            exhibitionsError = null
        } catch (e: Exception) {
            exhibitionsError =
                e.message ?: "Failed to load exhibitions"

            e.printStackTrace()
        }
    }

    suspend fun refreshCustomers() {
        isLoadingCustomers = true

        try {
            val result = CustomerApi.getCustomers()

            customersList.clear()
            customersList.addAll(result)

            customersError = null
        } catch (e: Exception) {
            customersError = e.message ?: "Failed to load customers"
            e.printStackTrace()
        } finally {
            isLoadingCustomers = false
        }
    }

    LaunchedEffect(currentScreen) {
        when (currentScreen) {
            ToggleScreen.Exhibition,
            ToggleScreen.AddCustomer -> refreshExhibitions()

            ToggleScreen.AllCustomers -> refreshCustomers()

            else -> Unit
        }
    }



    var selectedCustomerExhibition by remember {
        mutableStateOf<String?>(null)
    }

    var selectedExhibitionId by remember {
        mutableStateOf<String?>(null)
    }

    BackHandler {

        when (currentScreen) {

            ToggleScreen.AddCustomer -> {
                currentScreen = ToggleScreen.AllCustomers
            }

            ToggleScreen.AllCustomers -> {
                currentScreen = ToggleScreen.Dashboard
            }

            ToggleScreen.Exhibition -> {
                currentScreen = ToggleScreen.Dashboard
            }

            ToggleScreen.AddExhibition -> {
                currentScreen = ToggleScreen.Exhibition
            }

            ToggleScreen.Register -> {
                currentScreen = ToggleScreen.Login
            }

            ToggleScreen.Dashboard -> {
                (context as? Activity)?.finish()
            }

            ToggleScreen.Login -> {
                (context as? Activity)?.finish()
            }
        }
    }

    when (currentScreen) {

        ToggleScreen.Login -> Login(
            onNavigateToRegister = {
                currentScreen = ToggleScreen.Register
            },
            onNavigateToDashboard = {
                sharedPreferences.edit {
                    putBoolean("is_logged_in", true)
                }

                currentScreen = ToggleScreen.Dashboard
            }
        )

        ToggleScreen.Register -> Register(
            onNavigateToLogin = {
                currentScreen = ToggleScreen.Login
            }
        )

        ToggleScreen.Dashboard,
        ToggleScreen.AllCustomers,
        ToggleScreen.Exhibition -> MainScreen(
            currentScreen = currentScreen,
            customersList = customersList,
            exhibitions = exhibitionsList,
            selectedCustomerExhibition = selectedCustomerExhibition,
            onNavigate = { screen ->
                currentScreen = screen
            },
            onLogout = {
                sharedPreferences.edit {
                    putBoolean("is_logged_in", false)
                }

                currentScreen = ToggleScreen.Login
            },
            onRemoveCustomer = {
                customersList.remove(it)
            },
            onBack = {
                currentScreen = ToggleScreen.Dashboard
            },
            onViewCustomers = { exhibitionName ->
                selectedCustomerExhibition = exhibitionName
                currentScreen = ToggleScreen.AllCustomers
            },

            )


        ToggleScreen.AddCustomer -> AddCustomer(
            customers = customersList,
            exhibitions = exhibitionsList,
            selectedExhibitionId = selectedExhibitionId,
            onExhibitionSelected = { id ->
                selectedExhibitionId = id
            },
            onBack = {
                currentScreen = ToggleScreen.AllCustomers
            },
            onSave = { newCustomer, exhibitionId, onResult ->
                customersError = null

                scope.launch {
                    try {
                        CustomerApi.addCustomer(newCustomer, exhibitionId)
                        onResult(true)
                        refreshCustomers()
//                        currentScreen = ToggleScreen.AllCustomers
                    } catch (e: Exception) {
                        customersError = e.message ?: "Failed to save customer"
                        android.util.Log.e(
                            "CustomerSave",
                            "Failed to save customer",
                            e
                        )
                        onResult(false)
                    }
                }
            }
        )



        ToggleScreen.AddExhibition -> AddExhibition(
            onBack = {
                currentScreen = ToggleScreen.Exhibition
            },
            onSave = { newExhibition ->
                exhibitionsList.add(0, newExhibition)
                currentScreen = ToggleScreen.Exhibition
                scope.launch {
                    try {
                        ExhibitionApi.addExhibition(newExhibition)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            },
            exhibition = exhibitionsList
        )


    }
}

