package com.example.kotlinpractice.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // Mock in-memory user database (email -> pair(fullName, password))
    private val registeredUsers = mutableMapOf<String, Pair<String, String>>(
        "user@example.com" to Pair("Alex Morgan", "Password123")
    )

    // --- Login Form Actions ---

    fun onLoginEmailChanged(email: String) {
        _uiState.update { currentState ->
            currentState.copy(
                loginForm = currentState.loginForm.copy(
                    email = email,
                    emailError = null,
                    generalError = null
                )
            )
        }
    }

    fun onLoginPasswordChanged(password: String) {
        _uiState.update { currentState ->
            currentState.copy(
                loginForm = currentState.loginForm.copy(
                    password = password,
                    passwordError = null,
                    generalError = null
                )
            )
        }
    }

    fun toggleLoginPasswordVisibility() {
        _uiState.update { currentState ->
            currentState.copy(
                loginForm = currentState.loginForm.copy(
                    isPasswordVisible = !currentState.loginForm.isPasswordVisible
                )
            )
        }
    }

    fun toggleRememberMe() {
        _uiState.update { currentState ->
            currentState.copy(
                loginForm = currentState.loginForm.copy(
                    rememberMe = !currentState.loginForm.rememberMe
                )
            )
        }
    }

    fun login() {
        val form = _uiState.value.loginForm
        val email = form.email.trim()
        val password = form.password

        var hasError = false
        var emailErr: String? = null
        var passwordErr: String? = null

        if (email.isEmpty()) {
            emailErr = "Email address is required"
            hasError = true
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailErr = "Please enter a valid email address"
            hasError = true
        }

        if (password.isEmpty()) {
            passwordErr = "Password is required"
            hasError = true
        }

        if (hasError) {
            _uiState.update { currentState ->
                currentState.copy(
                    loginForm = currentState.loginForm.copy(
                        emailError = emailErr,
                        passwordError = passwordErr
                    )
                )
            }
            return
        }

        // Simulate network API request
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(loginForm = currentState.loginForm.copy(isLoading = true))
            }

            delay(1200) // Simulated network latency

            val userRecord = registeredUsers[email.lowercase()]
            if (userRecord != null && userRecord.second == password) {
                val loggedUser = User(fullName = userRecord.first, email = email)
                _uiState.update { currentState ->
                    currentState.copy(
                        currentScreen = AuthScreenMode.HOME,
                        currentUser = loggedUser,
                        loginForm = LoginFormState(), // Reset login form
                        userMessage = "Welcome back, ${loggedUser.fullName}!"
                    )
                }
            } else {
                _uiState.update { currentState ->
                    currentState.copy(
                        loginForm = currentState.loginForm.copy(
                            isLoading = false,
                            generalError = "Invalid email or password. Try user@example.com / Password123"
                        )
                    )
                }
            }
        }
    }

    // --- Register Form Actions ---

    fun onRegisterFullNameChanged(fullName: String) {
        _uiState.update { currentState ->
            currentState.copy(
                registerForm = currentState.registerForm.copy(
                    fullName = fullName,
                    fullNameError = null,
                    generalError = null
                )
            )
        }
    }

    fun onRegisterEmailChanged(email: String) {
        _uiState.update { currentState ->
            currentState.copy(
                registerForm = currentState.registerForm.copy(
                    email = email,
                    emailError = null,
                    generalError = null
                )
            )
        }
    }

    fun onRegisterPasswordChanged(password: String) {
        _uiState.update { currentState ->
            currentState.copy(
                registerForm = currentState.registerForm.copy(
                    password = password,
                    passwordError = null,
                    generalError = null
                )
            )
        }
    }

    fun onRegisterConfirmPasswordChanged(confirmPassword: String) {
        _uiState.update { currentState ->
            currentState.copy(
                registerForm = currentState.registerForm.copy(
                    confirmPassword = confirmPassword,
                    confirmPasswordError = null,
                    generalError = null
                )
            )
        }
    }

    fun toggleRegisterPasswordVisibility() {
        _uiState.update { currentState ->
            currentState.copy(
                registerForm = currentState.registerForm.copy(
                    isPasswordVisible = !currentState.registerForm.isPasswordVisible
                )
            )
        }
    }

    fun toggleRegisterConfirmPasswordVisibility() {
        _uiState.update { currentState ->
            currentState.copy(
                registerForm = currentState.registerForm.copy(
                    isConfirmPasswordVisible = !currentState.registerForm.isConfirmPasswordVisible
                )
            )
        }
    }

    fun toggleAgreeToTerms() {
        _uiState.update { currentState ->
            currentState.copy(
                registerForm = currentState.registerForm.copy(
                    agreeToTerms = !currentState.registerForm.agreeToTerms,
                    termsError = null
                )
            )
        }
    }

    fun register() {
        val form = _uiState.value.registerForm
        val fullName = form.fullName.trim()
        val email = form.email.trim()
        val password = form.password
        val confirmPassword = form.confirmPassword
        val agreeToTerms = form.agreeToTerms

        var hasError = false
        var fullNameErr: String? = null
        var emailErr: String? = null
        var passwordErr: String? = null
        var confirmPasswordErr: String? = null
        var termsErr: String? = null

        if (fullName.isEmpty()) {
            fullNameErr = "Full name is required"
            hasError = true
        } else if (fullName.length < 2) {
            fullNameErr = "Please enter your full name"
            hasError = true
        }

        if (email.isEmpty()) {
            emailErr = "Email address is required"
            hasError = true
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailErr = "Please enter a valid email address"
            hasError = true
        } else if (registeredUsers.containsKey(email.lowercase())) {
            emailErr = "An account with this email already exists"
            hasError = true
        }

        if (password.isEmpty()) {
            passwordErr = "Password is required"
            hasError = true
        } else if (password.length < 6) {
            passwordErr = "Password must be at least 6 characters"
            hasError = true
        }

        if (confirmPassword.isEmpty()) {
            confirmPasswordErr = "Please confirm your password"
            hasError = true
        } else if (password != confirmPassword) {
            confirmPasswordErr = "Passwords do not match"
            hasError = true
        }

        if (!agreeToTerms) {
            termsErr = "You must agree to the Terms and Conditions"
            hasError = true
        }

        if (hasError) {
            _uiState.update { currentState ->
                currentState.copy(
                    registerForm = currentState.registerForm.copy(
                        fullNameError = fullNameErr,
                        emailError = emailErr,
                        passwordError = passwordErr,
                        confirmPasswordError = confirmPasswordErr,
                        termsError = termsErr
                    )
                )
            }
            return
        }

        // Simulate network API request for registration
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(registerForm = currentState.registerForm.copy(isLoading = true))
            }

            delay(1200) // Simulated network latency

            registeredUsers[email.lowercase()] = Pair(fullName, password)
            val newUser = User(fullName = fullName, email = email)

            _uiState.update { currentState ->
                currentState.copy(
                    currentScreen = AuthScreenMode.HOME,
                    currentUser = newUser,
                    registerForm = RegisterFormState(), // Reset form
                    userMessage = "Account created successfully! Welcome, $fullName."
                )
            }
        }
    }

    fun navigateTo(screen: AuthScreenMode) {
        _uiState.update { currentState ->
            currentState.copy(
                currentScreen = screen,
                loginForm = LoginFormState(),
                registerForm = RegisterFormState()
            )
        }
    }

    fun logout() {
        _uiState.update { currentState ->
            currentState.copy(
                currentScreen = AuthScreenMode.LOGIN,
                currentUser = null,
                userMessage = "You have been logged out."
            )
        }
    }

    fun clearUserMessage() {
        _uiState.update { currentState ->
            currentState.copy(userMessage = null)
        }
    }
}
