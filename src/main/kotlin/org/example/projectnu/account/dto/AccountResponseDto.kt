package org.example.projectnu.account.dto

import org.example.projectnu.account.status.UserRole

data class AccountResponseDto(
    val id: Long?,
    val loginId: String,
    val email: String,
    val role: UserRole
)
