package com.example.kotlinpractice.ui

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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

@Composable
fun Register(onNavigateToLogin: () -> Unit) {
    var email by remember { mutableStateOf("alex@gmail.com") }
    var name by remember { mutableStateOf("Alex") }
    var password by remember { mutableStateOf("1111111111") }
    var confirmPassword by remember { mutableStateOf("111111111") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    var termAccepted by remember { mutableStateOf(false) }

    val buttonInteractionSource = remember { MutableInteractionSource() }
    val buttonHovered by buttonInteractionSource.collectIsHoveredAsState()

    val registerInteractionSource = remember { MutableInteractionSource() }
    val registerHovered by registerInteractionSource.collectIsHoveredAsState()

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
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = "Register Icon Logo",
                    modifier = Modifier.size(50.dp),
                    tint = Color(0xFF6278F1)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Create Account",
                color = Color.Black,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Sign up to get started with your account",
                color = Color.Gray,
                fontSize = 16.sp,
                fontWeight = FontWeight.Light
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
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(text = "Name") },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF6278F1),     // Outline color when clicked/typing
                    unfocusedBorderColor = Color.Gray,   // Outline color when sitting idle
                    focusedLabelColor = Color(0xFF6278F1),      // Label text color when focused
                    cursorColor = Color(0xFF6278F1)            // The blinking typing line color
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Register Icon Logo",
                        tint = Color(0xFF6278F1)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
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
                        imageVector = Icons.Default.Lock,
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
            Text(
                text = "At least 8 characters",
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier
                    .align(Alignment.Start)
                    .offset(x = 8.dp),

                )

            Spacer(modifier = Modifier.height(14.dp))
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                shape = RoundedCornerShape(8.dp),
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val icon =
                        if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    val description =
                        if (confirmPasswordVisible) "Hide password" else "Show password"
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(imageVector = icon, contentDescription = description)
                    }
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
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

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { termAccepted = !termAccepted },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Checkbox(
                    checked = termAccepted,
                    onCheckedChange = { termAccepted = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFF6278F1),
                        uncheckedColor = Color.Gray,
                        checkmarkColor = Color.White
                    )
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "I agree to the Terms & Conditions",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (name.isBlank() && email.isBlank() && password.isBlank() && confirmPassword.isBlank()) {
                        errorMessage = "Please fill in all fields."
                    } else if (!email.contains("@gmail.com")) {
                        errorMessage = "Please enter a valid email address"
                    } else if (password.length < 8) {
                        errorMessage = "Password must be at least 8 characters"
                    } else if (password != confirmPassword) {
                        errorMessage = "Passwords do not match"
                    } else if (!termAccepted) {
                        errorMessage = "Please accept the terms and conditions"
                    } else {
                        errorMessage = null
                        name = ""
                        email = ""
                        password = ""
                        confirmPassword = ""
                        termAccepted = false
                        android.util.Log.d("RegisterScreen", "Registration successful for: $email")
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
            ) { Text(text = "Create Account", fontSize = 18.sp) }

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically

            ) {
                Text(text = "Already have an account?", color = Color.Gray, fontSize = 16.sp)
//                    TextButton(onClick = {
////                        register link logic
//                    }) {
//                        Text(text = "Register", color = Color(0xFF6278F1), fontSize = 16.sp)
//                    }
                Text(
                    text = "Login",
                    color = if (registerHovered) Color(0xFF3730A3) else Color(0xFF4F46E5),
                    fontSize = 16.sp,
                    modifier = Modifier
                        .pointerHoverIcon(PointerIcon.Hand)
                        .clickable(
                            interactionSource = registerInteractionSource,
                            indication = null
                        ) {
//                            login logic link
                            onNavigateToLogin()
                        }

                )
            }
        }
    }
}