package org.example.projectnu.common.exception.custom

import org.example.projectnu.common.`object`.ResultCode

class TokenExpiredException(errorMessage: String) : BasicException(ResultCode.TOKEN_EXPIRED, errorMessage) {
}
