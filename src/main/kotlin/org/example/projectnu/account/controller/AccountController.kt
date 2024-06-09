package org.example.projectnu.account.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import jakarta.annotation.security.PermitAll
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.example.projectnu.account.dto.response.AccountResponseDto
import org.example.projectnu.account.dto.request.RegisterAccountRequestDto
import org.example.projectnu.account.dto.request.SignInRequestDto
import org.example.projectnu.account.dto.response.SignInResponseDto
import org.example.projectnu.account.service.AccountService
import org.example.projectnu.account.status.UserRole
import org.example.projectnu.common.annotation.AccessLevel
import org.example.projectnu.common.config.OAuth2Properties
import org.example.projectnu.common.dto.Res
import org.example.projectnu.common.dto.Response
import org.example.projectnu.common.exception.custom.BadRequestException
import org.example.projectnu.common.`object`.ResultCode
import org.example.projectnu.common.util.mapper.Authorize
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import org.springframework.web.servlet.view.RedirectView
import org.springframework.web.util.UriComponentsBuilder
import java.util.*



@RestController
@RequestMapping("/api/accounts")
class AccountController(
    private val accountService: AccountService,
    private val oAuth2Properties: OAuth2Properties
) {
    @PostMapping("/register")
    fun register(@RequestBody @Valid accountDto: RegisterAccountRequestDto): Res<AccountResponseDto> {
        val registeredAccount = accountService.register(accountDto)
        return ResponseEntity.status(201).body(Response(ResultCode.SUCCESS, data = registeredAccount))
    }

    @PostMapping("/signin")
    fun signIn(@RequestBody signInRequest: SignInRequestDto, request: HttpServletRequest): Res<SignInResponseDto> {

        val jSessionId = Authorize.getJSession(request)

        val res = Response(ResultCode.SUCCESS, data = accountService.signIn(signInRequest,jSessionId))

        return res.toResponseEntity()
    }

    @PostMapping("/signin/oauth2/google")
    fun redirectToGoogleLogin(request: HttpServletRequest): Res<String> {
        val redirectUrl = buildRedirectUrl(request)
        var res = Response(ResultCode.SUCCESS, data = redirectUrl)
        return res.toResponseEntity()
    }

    private fun buildRedirectUrl(request: HttpServletRequest): String {
        val googleProperties = oAuth2Properties.google
        val state = generateRandomState() // 상태 토큰 생성을 위한 메서드

        return UriComponentsBuilder.fromUriString("https://accounts.google.com/o/oauth2/v2/auth")
            .queryParam("response_type", "code")
            .queryParam("client_id", googleProperties.clientId)
            .queryParam("redirect_uri", googleProperties.redirectUri)
            .queryParam("scope", "email profile")
            .queryParam("state", state)
            .build()
            .toUriString()
    }

    private fun generateRandomState(): String {
        return UUID.randomUUID().toString()
    }

    @GetMapping("/oauth/google/callback")
    fun handleGoogleOAuthCallback(
        @RequestParam("state") state: String,
        @RequestParam("code") code: String,
        @RequestParam("scope") scope: String,
        @RequestParam("authuser") authuser: String,
        @RequestParam("prompt") prompt: String
    ): Res<String> {
        val accessToken = getAccessToken(code)
        return Response(ResultCode.SUCCESS, data = "OAuth callback handled successfully $accessToken").toResponseEntity()
    }

    private fun getAccessToken(code: String): String {
        val googleProperties = oAuth2Properties.google
        val headers = HttpHeaders()
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

    fun extractResponseBody(responseBody: String): Map<String, String> {
        val mapper = jacksonObjectMapper()
        val tokenResponse: Map<String, String> = mapper.readValue(responseBody)
        return tokenResponse
    }
    private fun extractAccessToken(responseBody: String?): String {
        var claim = decodeJWT(responseBody!!)
        return claim["email"] as String
    }

    fun decodeJWT(idTokenString: String): Map<String, String>  {
        var payload =  extractPayload(idTokenString)
        val mapper = jacksonObjectMapper()
        val claimsString = mapper.readValue<Map<String, String>>(payload)

        return claimsString
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

    fun extractPayload(token: String): String {
        val parts = token.split(".")
        if (parts.size != 3) {
            throw IllegalArgumentException("Invalid JWT token format.")
        }
        return decodeBase64UrlSafe(parts[1])
    }

}
//todo 리다이렉션 처리 익셉션도 적용해야함. 둘중에 필요한거 갖다 쓸건데 not found 는 base 로 보낼까..
//val redirectUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
//    .path("/another-page")
//    .toUriString()
//
//return ResponseEntity.status(HttpStatus.SEE_OTHER)
//.header("Location", redirectUrl)
//.build()
//}
//랑
//fun redirectToGoogleLogin(request: HttpServletRequest): RedirectView {
//    val redirectUrl = buildRedirectUrl(request)
//    return RedirectView(redirectUrl)
//}
//import com.auth0.jwt.JWT
//import com.auth0.jwt.interfaces.DecodedJWT
//
//fun decodeJWT(idTokenString: String): DecodedJWT {
//    return JWT.decode(idTokenString)
//}
//val jwt = decodeJWT(idTokenString)
//val userEmail = jwt.getClaim("email").asString()
//val userName = jwt.getClaim("name").asString()

