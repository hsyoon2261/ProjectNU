package org.example.projectnu.common.filter

import io.jsonwebtoken.Claims
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.example.projectnu.account.repository.AccountRepository
import org.example.projectnu.common.exception.custom.TokenExpiredException
import org.example.projectnu.common.exception.custom.UnAuthorizedException
import org.example.projectnu.common.`object`.JwtConstants
import org.example.projectnu.common.security.CustomUserDetails
import org.example.projectnu.common.security.JwtTokenProvider
import org.example.projectnu.common.util.mapper.Authorize
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import java.util.*

class JwtTokenFilter(
    private val jwtTokenProvider: JwtTokenProvider,
    private val accountRepository: AccountRepository
) : OncePerRequestFilter() {

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = resolveToken(request)
        if (token != null) {
            val claims = jwtTokenProvider.getAuthentication(token)
            validateClaims(claims, request)
            val userDetails = getUserDetailsFromClaims(claims)
            val auth = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
            SecurityContextHolder.getContext().authentication = auth
        }
        filterChain.doFilter(request, response)
    }


    private fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader(JwtConstants.AUTH_HEADER)
        return if (bearerToken != null && bearerToken.startsWith(JwtConstants.TOKEN_PREFIX)) {
            bearerToken.substring(JwtConstants.TOKEN_PREFIX.length)
        } else null
    }

    private fun getUserDetailsFromClaims(claims: Claims): CustomUserDetails {
        val username = claims["loginId"] as String
        val email = claims["email"] as String
        val role = claims["role"] as String
        val sessionId = claims["sessionId"] as String
        val authorities = listOf(SimpleGrantedAuthority(role))
        return CustomUserDetails(username, "", authorities, email, role, sessionId)
    }

    private fun validateClaims(claims: Claims, request: HttpServletRequest) {

        if (claims.expiration.after(Date())) return;

        val username = claims["loginId"] as String
        var account = accountRepository.findByLoginId(username)
            ?: throw UnAuthorizedException("Account not found")

        val currentSession = Authorize.getJSession(request)
            ?: throw UnAuthorizedException("Session not found")

        if (claims["sessionId"] != currentSession) throw UnAuthorizedException("Another device")

        val token = jwtTokenProvider.generateToken(Authorize.getCustomUserDetails(account, currentSession))

        throw TokenExpiredException(token)
    }
}
