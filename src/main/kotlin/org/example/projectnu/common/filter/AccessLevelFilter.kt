package org.example.projectnu.common.filter

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import org.example.projectnu.account.status.UserRole
import org.example.projectnu.common.annotation.AccessLevel
import org.example.projectnu.common.exception.custom.UnAuthorizedException
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerExecutionChain
import org.springframework.web.servlet.HandlerMapping

@Component
class AccessLevelFilter(
    @Qualifier("requestMappingHandlerMapping") private val handlerMapping: HandlerMapping
) : Filter {

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpRequest = request as HttpServletRequest
        val handlerExecutionChain: HandlerExecutionChain? = handlerMapping.getHandler(httpRequest)

        if (handlerExecutionChain != null) {
            val handler = handlerExecutionChain.handler
            if (handler is HandlerMethod) {
                val accessLevel = handler.method.getAnnotation(AccessLevel::class.java)
                if (accessLevel != null) {
                    val requestLevel = SecurityContextHolder.getContext().authentication.authorities.firstOrNull()
                    if (requestLevel == null || UserRole.fromString(requestLevel.authority).order < accessLevel.userRole.order) {
                        throw UnAuthorizedException("접근 권한이 없습니다. 필요 권한: ${accessLevel.userRole.name}")
                        //todo UserRole.fromstring 부분을 미리 변수로 받아서 처리하면 더 좋을 것 같습니다.
                    }
                }
            }
        }

        chain.doFilter(request, response)
    }
}
