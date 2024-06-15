package org.example.projectnu.account.controller

import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.example.projectnu.account.dto.request.GoogleOAuthCallbackParams
import org.example.projectnu.account.dto.request.RegisterAccountRequestDto
import org.example.projectnu.account.dto.request.SignInRequestDto
import org.example.projectnu.account.dto.response.AccountResponseDto
import org.example.projectnu.account.dto.response.SignInResponseDto
import org.example.projectnu.account.oauth2.GoogleOAuth2Service
import org.example.projectnu.account.service.AccountService
import org.example.projectnu.common.dto.Res
import org.example.projectnu.common.dto.Response
import org.example.projectnu.common.`object`.ResultCode
import org.example.projectnu.common.util.mapper.Authorize
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/accounts")
class AccountController(
    private val accountService: AccountService,
    private val googleOAuth2Service: GoogleOAuth2Service
) {
    @PostMapping("/register")
    fun register(@RequestBody @Valid accountDto: RegisterAccountRequestDto): Res<AccountResponseDto> {
        val registeredAccount = accountService.register(accountDto)
        return ResponseEntity.status(201).body(Response(ResultCode.SUCCESS, data = registeredAccount))
    }

    @PostMapping("/signin")
    fun signIn(@RequestBody signInRequest: SignInRequestDto, request: HttpServletRequest): Res<SignInResponseDto> {
        val jSessionId = Authorize.getJSession(request)
        val res = Response(ResultCode.SUCCESS, data = accountService.signIn(signInRequest, jSessionId))
        return res.toResponseEntity()
    }

    @PostMapping("/signin/oauth2/google")
    fun redirectToGoogleLogin(request: HttpServletRequest): Res<String> {
        val redirectUrl = googleOAuth2Service.getRedirectGoogleSignInUrl(request)
        var res = Response(ResultCode.SUCCESS, data = redirectUrl)
        return res.toResponseEntity()
    }

    @GetMapping("/oauth/google/callback")
    fun handleGoogleOAuthCallback(
        @ModelAttribute params: GoogleOAuthCallbackParams
    ): Res<String> {
        val accessToken = googleOAuth2Service.getGoogleAccessToken(params.code)
        return Response(
            ResultCode.SUCCESS,
            data = "OAuth callback handled successfully $accessToken"
        ).toResponseEntity()
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

