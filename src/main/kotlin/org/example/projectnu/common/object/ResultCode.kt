package org.example.projectnu.common.`object`

enum class ResultCode(val description: String) {
    SUCCESS("Operation was successful"),
    FAILURE("Operation failed"),
    NOT_FOUND("Resource not found"),
    INVALID_REQUEST("Invalid request"),
    NOT_USED("Not used"),
    NOT_VALID("Not valid"),
    BAD_REQUEST("Bad request"),
    UNAUTHORIZED("Unauthorized access"),
    FORBIDDEN("Forbidden access"),
    INTERNAL_SERVER_ERROR("Internal server error")
}
