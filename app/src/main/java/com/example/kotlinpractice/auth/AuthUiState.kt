package com.example.kotlinpractice.auth

enum class AuthScreenMode {
    LOGIN,
    REGISTER,
    HOME
}

data class User(
    val fullName: String,
    val email: String
)

data class LoginFormState(
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val isPasswordVisible: Boolean = false,
    val rememberMe: Boolean = false,
    val isLoading: Boolean = false,
    val generalError: String? = null
)

data class RegisterFormState(
    val fullName: String = "",
    val fullNameError: String? = null,
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val confirmPassword: String = "",
    val confirmPasswordError: String? = null,
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val agreeToTerms: Boolean = false,
    val termsError: String? = null,
    val isLoading: Boolean = false,
    val generalError: String? = null
)

data class AuthUiState(
    val currentScreen: AuthScreenMode = AuthScreenMode.LOGIN,
    val loginForm: LoginFormState = LoginFormState(),
    val registerForm: RegisterFormState = RegisterFormState(),
    val currentUser: User? = null,
    val userMessage: String? = null
)
