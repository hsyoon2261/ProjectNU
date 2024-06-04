package org.example.projectnu.common.exception

import org.example.projectnu.common.`object`.Response
import org.example.projectnu.common.`object`.ResultCode
import org.example.projectnu.common.exception.custom.BadRequestException
import org.example.projectnu.common.exception.custom.NotFoundException
import org.example.projectnu.common.exception.custom.InternalServerErrorException
import org.example.projectnu.common.exception.custom.NotUsedException
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequestException(ex: BadRequestException): ResponseEntity<Response<Unit>> {
        val response = Response<Unit>(
            code = ResultCode.INVALID_REQUEST,
            message = ex.message
        )
        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(ex: NotFoundException): ResponseEntity<Response<Unit>> {
        val response = Response<Unit>(
            code = ResultCode.NOT_FOUND,
            message = ex.message
        )
        return ResponseEntity(response, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(NotUsedException::class)
    fun handleNotUsedException(ex: NotUsedException): ResponseEntity<Response<Unit>> {
        val response = Response<Unit>(
            code = ResultCode.NOT_USED,
            message = ex.message
        )
        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(InternalServerErrorException::class)
    fun handleInternalServerErrorException(ex: InternalServerErrorException): ResponseEntity<Response<Unit>> {
        val response = Response<Unit>(
            code = ResultCode.FAILURE,
            message = ex.message
        )
        return ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR)
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
        val response = Response<Unit>(
            code = ResultCode.FAILURE,
            message = "An unexpected error occurred: ${ex.message}"
        )
        return ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
