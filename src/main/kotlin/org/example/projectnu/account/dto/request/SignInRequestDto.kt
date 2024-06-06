package org.example.projectnu.account.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class SignInRequestDto(

    @field:NotBlank
    @field:Size(max = 30, message = "Login ID must be 30 characters or less")
    val loginId: String,

    @field:NotBlank
    val password: String

)
