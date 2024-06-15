package org.example.projectnu.common.util.mapper

import jakarta.servlet.http.HttpServletRequest
import org.example.projectnu.account.entity.Account
import org.example.projectnu.common.security.CustomUserDetails
import org.springframework.security.core.authority.SimpleGrantedAuthority

object Authorize {
    fun getJSession(request: HttpServletRequest): String {
        val cookies = request.cookies
        val jSessionId = cookies?.firstOrNull { it.name == "JSESSIONID" }?.value
        val userAgent = request.getHeader("User-Agent")
        val device = when {
            userAgent.contains("Chrome") && userAgent.contains("Edg") -> "MicrosoftEdge"
            userAgent.contains("Chrome") -> "GoogleChrome"
            else -> "Unknown"
        }
        //return device+jSessionId
        return "ALL_PASS"
    }

    fun getCustomUserDetails(account: Account, sessionId: String): CustomUserDetails {
        val authorities = listOf(SimpleGrantedAuthority(account.role.name))
        val userDetails = CustomUserDetails(
            username = account.loginId,
            password = account.password,
            authorities = authorities,
            email = account.email,
            role = account.role.name,
            jSessionId = sessionId
        )
        return userDetails
    }
}
