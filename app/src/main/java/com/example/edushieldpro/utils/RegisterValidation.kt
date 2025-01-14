package com.example.edushieldpro.utils

sealed class RegisterValidation {
    object Success : RegisterValidation()
    data class Failed(val message : String) : RegisterValidation()
}

data class RegisterFieldState(
    val email : RegisterValidation,
    val password : RegisterValidation,
    val rePass : RegisterValidation,
    val cnic : RegisterValidation,
    val phone : RegisterValidation
)