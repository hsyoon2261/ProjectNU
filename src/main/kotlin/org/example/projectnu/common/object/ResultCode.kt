package org.example.projectnu.common.`object`

import org.springframework.http.HttpStatus

enum class ResultCode(val description: String, val httpStatus: HttpStatus) {
    SUCCESS("Operation was successful", HttpStatus.OK),
    FAILURE("Operation failed", HttpStatus.INTERNAL_SERVER_ERROR),
    NOT_FOUND("Resource not found", HttpStatus.NOT_FOUND),
    INVALID_REQUEST("Invalid request", HttpStatus.BAD_REQUEST),
    NOT_USED("Not used", HttpStatus.BAD_REQUEST),
    NOT_VALID("Not valid", HttpStatus.BAD_REQUEST),
    BAD_REQUEST("Bad request", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED("Unauthorized access", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN("Invalid token", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED("Token expired, use generated new token", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("Forbidden access", HttpStatus.FORBIDDEN),
    INVALID_DEVICE_TYPE("Invalid device type", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR)
}
