package org.example.projectnu.common.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import java.io.IOException

@Component
class RequestLoggingFilter : OncePerRequestFilter() {

    @Throws(IOException::class, ServletException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val wrappedRequest = ContentCachingRequestWrapper(request)
        filterChain.doFilter(wrappedRequest, response)
        val curlCommand = buildCurlCommand(wrappedRequest)
        logger.info(curlCommand)
    }

    private fun buildCurlCommand(request: HttpServletRequest): String {
        val headers = StringBuilder()
        val headerNames = request.headerNames
        while (headerNames.hasMoreElements()) {
            val headerName = headerNames.nextElement()
            val headerValue = request.getHeader(headerName)
            headers.append("-H '$headerName: $headerValue' ")
        }

        val method = request.method
        val requestUri = request.requestURI
        val queryString = request.queryString
        val fullUrl = if (queryString == null) requestUri else "$requestUri?$queryString"

        val body = getBody(request)

        return "curl -X $method $headers '$fullUrl' $body"
    }

    private fun getBody(request: HttpServletRequest): String {
        val inputStream = request.inputStream
        val cachedRequest = request as ContentCachingRequestWrapper
        val body = cachedRequest.contentAsByteArray
        return if (body.isNotEmpty()) "--data '${String(body)}'" else ""
    }
}
