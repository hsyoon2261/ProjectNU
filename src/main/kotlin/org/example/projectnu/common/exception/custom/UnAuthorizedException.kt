package org.example.projectnu.common.exception.custom

import org.example.projectnu.common.`object`.ResultCode

class UnAuthorizedException : BasicException {
    constructor() : super(ResultCode.UNAUTHORIZED)
    constructor(errorMessage: String) : super(ResultCode.UNAUTHORIZED, errorMessage)
}

class InvalidTokenException : BasicException{
    constructor() : super(ResultCode.UNAUTHORIZED)
    constructor(errorMessage: String) : super(ResultCode.INVALID_TOKEN, errorMessage)
}
