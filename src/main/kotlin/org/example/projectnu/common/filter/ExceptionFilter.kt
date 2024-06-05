package org.example.projectnu.common.filter

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.example.projectnu.common.dto.Response
import org.example.projectnu.common.exception.custom.BasicException
import org.example.projectnu.common.`object`.ResultCode
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.filter.OncePerRequestFilter

class ExceptionFilter(
    private val objectMapper: ObjectMapper
): OncePerRequestFilter() {
    private val log = LoggerFactory.getLogger(this::class.simpleName)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            filterChain.doFilter(request, response)
        } catch (e: BasicException) {
            sendError(response, e.resultCode, e.message)
        } catch (e: Exception) {
            sendError(response, ResultCode.INTERNAL_SERVER_ERROR, e.message )
            throw e
        }

    }

    private fun sendError(res: HttpServletResponse, errorCode: ResultCode, message: String?) {
        val errorResponse = Response<Unit>(errorCode, message = message)
        val responseString = objectMapper.writeValueAsString(errorResponse)
        res.characterEncoding = "UTF-8"
        res.status = errorCode.httpStatus.value()
        res.contentType = MediaType.APPLICATION_JSON_VALUE
        res.writer.write(responseString)
    }
}
