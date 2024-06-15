package org.example.projectnu.account.oauth2

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.servlet.http.HttpServletRequest
import org.example.projectnu.common.config.GoogleProperties
import org.example.projectnu.common.config.OAuth2Properties
import org.example.projectnu.common.util.JwtDecoder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.util.*

object GoogleConstants {
    const val oAuthUrl: String = "https://accounts.google.com/o/oauth2/auth"

}

@Service
class GoogleOAuth2Service(
    private val googleProperties: GoogleProperties = OAuth2Properties().google
) : OAuth2Base() {
    fun getRedirectGoogleSignInUrl(request: HttpServletRequest): String {
        val redirectUrl = buildRedirectUrl(request)
        return redirectUrl
    }

    fun getGoogleAccessToken(code: String): String {
        var headers = HttpHeaders()
        headers.add("Content-Type", "application/x-www-form-urlencoded")
        val body: MultiValueMap<String, String> = LinkedMultiValueMap()
        body.add("client_id", googleProperties.clientId)
        body.add("client_secret", googleProperties.clientSecret)
        body.add("code", code)
        body.add("redirect_uri", googleProperties.redirectUri)
        body.add("grant_type", "authorization_code")
        val requestEntity = HttpEntity(body, headers)
        val restTemplate = RestTemplate()
        val responseEntity: ResponseEntity<String> = restTemplate.exchange(
            "https://oauth2.googleapis.com/token",
            HttpMethod.POST,
            requestEntity,
            String::class.java
        )
        var cc = extractResponseBody(responseEntity.body!!)
        val accessToken = extractAccessToken(cc["id_token"])
        return accessToken

    }

    private fun extractResponseBody(responseBody: String): Map<String, String> {
        val mapper = jacksonObjectMapper()
        val tokenResponse: Map<String, String> = mapper.readValue(responseBody)
        return tokenResponse
    }

    private fun extractAccessToken(responseBody: String?): String {
        var claim = JwtDecoder.decodeJWT(responseBody!!)
        val mapper = jacksonObjectMapper()
        return mapper.writeValueAsString(claim)
    }


    private fun buildRedirectUrl(request: HttpServletRequest): String {
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
