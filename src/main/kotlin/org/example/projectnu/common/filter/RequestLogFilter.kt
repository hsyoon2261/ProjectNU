package org.example.projectnu.common.filter

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpFilter
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.io.BufferedReader
import java.io.IOException

@Component
class RequestLoggingFilter : HttpFilter() {
    private val logger = LoggerFactory.getLogger(RequestLoggingFilter::class.java)

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val curlCommand = buildCurlCommand(request)
        logger.info(curlCommand)
        chain.doFilter(request, response)
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
        val stringBuilder = StringBuilder()
        var bufferedReader: BufferedReader? = null
        try {
            val inputStream = request.inputStream
            if (inputStream != null) {
                bufferedReader = BufferedReader(inputStream.reader())
                var line: String? = bufferedReader.readLine()
                while (line != null) {
                    stringBuilder.append(line)
                    line = bufferedReader.readLine()
                }
            }
        } catch (ex: IOException) {
            // Handle the exception
        } finally {
            bufferedReader?.close()
        }
        val body = stringBuilder.toString()
        return if (body.isNotEmpty()) "--data '$body'" else ""
    }
}
