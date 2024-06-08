package org.example.projectnu.common.filter

import jakarta.servlet.*
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.example.projectnu.common.exception.custom.NotFoundException
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerExecutionChain
import org.springframework.web.servlet.HandlerMapping
import java.util.concurrent.ConcurrentHashMap

@Component
class ForbiddenCheckFilter(
    @Qualifier("requestMappingHandlerMapping") private val handlerMapping: HandlerMapping
) : Filter {

    private val urlCache = ConcurrentHashMap<String, Boolean>()

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpRequest = request as HttpServletRequest
        val uri = httpRequest.requestURI

        val isValid = urlCache.computeIfAbsent(uri) { key ->
            val handlerExecutionChain: HandlerExecutionChain? = handlerMapping.getHandler(httpRequest)
            handlerExecutionChain != null
        }

        if (isValid) {
            chain.doFilter(request, response)
        } else {
            throw NotFoundException("요청하신 페이지를 찾을 수 없습니다.")
        }
    }

    override fun destroy() {
        urlCache.clear()  // 필터 소멸 시 캐시 정리
    }
}
