package org.example.projectnu.account.oauth2

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.servlet.http.HttpServletRequest
import org.example.projectnu.account.dto.request.GoogleTokenRequest
import org.example.projectnu.account.oauth2.data.GoogleTokenInfo
import org.example.projectnu.common.config.GoogleProperties
import org.example.projectnu.common.config.OAuth2Properties
import org.example.projectnu.common.util.JwtDecoder
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

object GoogleConstants {
    const val oAuthUrl: String = "https://accounts.google.com/o/oauth2/auth"
}

@Service
class GoogleOAuth2Service(
    oAuth2Properties: OAuth2Properties,
    private val mapper: ObjectMapper
) : OAuth2Base() {
    private val googleProperties: GoogleProperties = oAuth2Properties.google
    fun getRedirectGoogleSignInUrl(request: HttpServletRequest): String {
        val redirectUrl = buildRedirectUrl()
        return redirectUrl
    }

    fun getGoogleAccessToken(code: String): String {
        val headers = HttpHeaders()
        headers.add("Content-Type", "application/x-www-form-urlencoded")

        val body = GoogleTokenRequest.toRequestBodySimple(googleProperties, code)
        val requestEntity = HttpEntity(body, headers)
        val restTemplate = RestTemplate()
        val responseEntity: ResponseEntity<String> = restTemplate.exchange(
            "https://oauth2.googleapis.com/token",
            HttpMethod.POST,
            requestEntity,
            String::class.java
        )
        val resBody = extractResponseBody(responseEntity.body!!)
        val accessToken = getGoogleTokenInfo(resBody["id_token"])
        return mapper.writeValueAsString(accessToken)

    }

    private fun extractResponseBody(responseBody: String): Map<String, String> {
        val tokenResponse: Map<String, String> = mapper.readValue(responseBody)
        return tokenResponse
    }

    private fun getGoogleTokenInfo(responseBody: String?): GoogleTokenInfo {
        val claim = JwtDecoder.decodeJWT(responseBody!!)
        return mapper.convertValue(claim, GoogleTokenInfo::class.java)
    }


    private fun buildRedirectUrl(): String {
        val state = generateRandomState() // 상태 토큰 생성을 위한 메서드

        return UriComponentsBuilder.fromUriString(GoogleConstants.oAuthUrl)
            .queryParam("client_id", googleProperties.clientId)
            .queryParam("redirect_uri", googleProperties.redirectUri)
            .queryParam("response_type", "code")
            .queryParam("scope", "openid profile email")
            .queryParam("state", state)
            .build()
            .toUriString()
    }
}
