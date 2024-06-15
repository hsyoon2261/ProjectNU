package org.example.projectnu.account.dto.request

import org.example.projectnu.common.config.GoogleProperties
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap

data class GoogleTokenRequest(
    val client_id: String,
    val client_secret: String,
    val code: String,
    val redirect_uri: String,
    val grant_type: String = "authorization_code"
) {
    companion object {
        fun toRequestBodySimple(config: GoogleProperties, code: String): MultiValueMap<String, String> {
            val map = LinkedMultiValueMap<String, String>().apply {
                add("client_id", config.clientId)
                add("client_secret", config.clientSecret)
                add("code", code)
                add("redirect_uri", config.redirectUri)
                add("grant_type", "authorization_code")
            }
            return map
        }
    }
}

