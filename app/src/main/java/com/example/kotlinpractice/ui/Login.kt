package com.example.kotlinpractice.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Login(onNavigateToRegister: () -> Unit, onNavigateToDashboard: () -> Unit) {
    var email by remember { mutableStateOf("test@gmail.com") }
    var password by remember { mutableStateOf("1111111111") }
    var passwordVisible by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    val buttonInteractionSource = remember { MutableInteractionSource() }
    val buttonHovered by buttonInteractionSource.collectIsHoveredAsState()

    val registerInteractionSource = remember { MutableInteractionSource() }
    val registerHovered by registerInteractionSource.collectIsHoveredAsState()

    var showForgetPassword by remember { mutableStateOf(false) }
    var resetEmail by remember { mutableStateOf("") }
    var resetEmailSent by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0x9FD5DAF5))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                focusManager.clearFocus()
            },

        contentAlignment = Alignment.Center,

        ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(26.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .background(
                        color = Color(0xFF6278F1).copy(alpha = 0.15f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Login Logo",
                    modifier = Modifier.size(50.dp),
                    tint = Color(0xFF6278F1)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            Text(text = "Welcome Back", fontSize = 38.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Sign in to continue",
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(28.dp))
//
            if (errorMessage != null) {
//                Spacer(modifier = Modifier.height(26.dp))
                Text(
                    text = errorMessage!!,
                    color = Color(0xFFEF4444),
                    fontSize = 16.sp,
                    modifier = Modifier.align(
                        Alignment.Start
                    )
                )
            }
//
//            Spacer(modifier = Modifier.height(26.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF6278F1),     // Outline color when clicked/typing
                    unfocusedBorderColor = Color.Gray,   // Outline color when sitting idle
                    focusedLabelColor = Color(0xFF6278F1),      // Label text color when focused
                    cursorColor = Color(0xFF6278F1)            // The blinking typing line color
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email icon",
                        tint = Color(0xFF6278F1)
                    )
                },

                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                shape = RoundedCornerShape(8.dp),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val icon =
                        if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    val description = if (passwordVisible) "Hide password" else "Show password"
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = icon, contentDescription = description)
                    }
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Password,
                        contentDescription = "Password icon",
                        tint = Color(0xFF6278F1)
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF6278F1),
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = Color(0xFF6278F1),
                    cursorColor = Color(0xFF6278F1)
                ),

                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = {
                    //forgot password logic
                    showForgetPassword = true
                    resetEmail = email
                }) {
                    Text(text = "Forgot Password?", color = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            // login button
            Button(
                onClick = {
                    // button logic
                    if (email.isBlank() || password.isBlank()) {
                        errorMessage = "Please fill in all fields."
                    } else if (!email.contains("@gmail.com")) {
                        errorMessage = "Please enter a valid email address."
                    } else if (password.length < 8) {
                        errorMessage = "Password must be at least 8 characters long."
                    } else if (email == "test@gmail.com" && password == "1111111111") {
                        errorMessage = null
                        email = ""
                        password = ""
                        onNavigateToDashboard()
                    } else {
                        errorMessage = "Incorrect email or password."
                    }

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .pointerHoverIcon(PointerIcon.Hand),
                shape = RoundedCornerShape(8.dp),
                interactionSource = buttonInteractionSource,

                colors = ButtonDefaults.buttonColors(
                    containerColor = if (buttonHovered) Color(0xFF6278F1) else Color(0xFF4962EC),
                    contentColor = Color.White,

                    ),

                contentPadding = PaddingValues(
                    vertical = 16.dp,
                    horizontal = 16.dp
                )

            ) {
                Text(text = "Sign In", fontSize = 20.sp, fontWeight = FontWeight.Medium)
            }


            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically

            ) {
                Text(text = "Don't have an account?", color = Color.Gray, fontSize = 16.sp)
//                    TextButton(onClick = {
////                        register link logic
//                    }) {
//                        Text(text = "Register", color = Color(0xFF6278F1), fontSize = 16.sp)
//                    }
                Text(
                    text = "Register",
                    color = if (registerHovered) Color(0xFF3730A3) else Color(0xFF4F46E5),
                    fontSize = 16.sp,
                    modifier = Modifier
                        .pointerHoverIcon(PointerIcon.Hand)
                        .clickable(
                            interactionSource = registerInteractionSource,
                            indication = null
                        ) {
//                            register logic link
                            onNavigateToRegister()
                        }

                )
            }
        }
        if (showForgetPassword) {
            AlertDialog(
                onDismissRequest = {
                    showForgetPassword = false
                    resetEmailSent = false
                },
                title = { Text(text = if (resetEmailSent) "Check your email" else "Reset Password") },
                text = {
                    Column {
                        if (!resetEmailSent) {
                            Text("Enter your email and we'll send you a link to reset your password.")
                            OutlinedTextField(
                                value = resetEmail,
                                onValueChange = { resetEmail = it },
                                label = { Text("Email") },
                                shape = RoundedCornerShape(8.dp),
                            )
                        } else {
                            Text("Check your email for a link to reset your password.")
                        }
                    }
                },
                confirmButton = {
                    if (!resetEmailSent) {
                        Button(
                            onClick = {
                                Log.d("Reset Password", "Resetting password for $resetEmail")
                                showForgetPassword = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6278F1))
                        ) {
                            Text("Reset Password", color = Color.White)
                        }
                    } else {
                        Button(
                            onClick = {
                                showForgetPassword = false
                                resetEmailSent = false // Reset everything for next time
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6278F1))
                        ) {
                            Text("Done")
                        }
                    }
                },
                dismissButton = {
                    if (!resetEmailSent) {
                        TextButton(onClick = { showForgetPassword = false }) {
                            Text("Cancel", color = Color.Gray)
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp)
            )
        }

    }


}