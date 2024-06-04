package org.example.projectnu.account.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RegisterAccountRequestDto(
    @field:NotBlank
    @field:Size(max = 30, message = "Login ID must be 30 characters or less")
    val loginId: String,

    @field:NotBlank
    val password: String,

    @field:NotBlank
    @field:Email(regexp = "^[A-Za-z0-9+_.-]+@(.+)\$", message = "Email should be valid")
    val email: String
)