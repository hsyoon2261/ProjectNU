package org.example.projectnu.account.dto.request

data class GoogleOAuthCallbackParams(
    val state: String,
    val code: String,
    val scope: String?,
    val authuser: String?,
    val prompt: String?
)
