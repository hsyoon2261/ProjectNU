package org.example.projectnu.common.util

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.util.*

object JwtDecoder {

    fun decodeJWT(idTokenString: String): Map<String, String>  {
        val payload =  extractPayload(idTokenString)
        val mapper = jacksonObjectMapper()
        val claimsString = mapper.readValue<Map<String, String>>(payload)

        return claimsString
    }

    fun extractPayload(token: String): String {
        val parts = token.split(".")
        if (parts.size != 3) {
            throw IllegalArgumentException("Invalid JWT token format.")
        }
        return decodeBase64UrlSafe(parts[1])
    }

    fun decodeBase64UrlSafe(input: String): String {
        val base64Encoded = input.replace('-', '+').replace('_', '/')
        val normalizedBase64 = when (base64Encoded.length % 4) {
            2 -> base64Encoded + "=="
            3 -> base64Encoded + "="
            else -> base64Encoded
        }
        return String(Base64.getDecoder().decode(normalizedBase64))
    }

}
