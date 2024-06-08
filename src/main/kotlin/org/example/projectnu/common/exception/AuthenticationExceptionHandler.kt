package org.example.projectnu.common.exception

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.example.projectnu.common.exception.custom.UnAuthorizedException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

@Component
class AuthenticationExceptionHandler : AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException?
    ) {
        throw UnAuthorizedException("요청할 권한이 없습니다")
    }
}
