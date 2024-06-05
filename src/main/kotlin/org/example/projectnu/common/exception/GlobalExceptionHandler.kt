package org.example.projectnu.common.exception

import jakarta.validation.ConstraintViolationException
import org.example.projectnu.common.dto.Response
import org.example.projectnu.common.exception.custom.*
import org.example.projectnu.common.`object`.ResultCode
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(BasicException::class)
    fun handleBasicException(ex: BasicException): ResponseEntity<Response<Unit>> {
        val response = Response<Unit>(ex.resultCode, message = ex.message)
        return ResponseEntity(response, ex.resultCode.httpStatus)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<Response<String>> {
        val errors = ex.bindingResult.allErrors.joinToString(", ") { it.defaultMessage ?: "Invalid value" }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Response(ResultCode.NOT_VALID, message = "Validation failed: $errors"))
    }
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationExceptions(ex: ConstraintViolationException): ResponseEntity<Response<String>> {
        val errors = ex.constraintViolations.joinToString(", ") { it.message }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Response(ResultCode.FAILURE, message = "Validation failed: $errors"))
    }

    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<Response<Unit>> {
        val response = Response<Unit>(ResultCode.FAILURE, message = ex.message)
        return ResponseEntity(response, ResultCode.FAILURE.httpStatus)
    }
}
