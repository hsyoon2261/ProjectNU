package org.example.projectnu.common.exception.custom

import org.example.projectnu.common.`object`.ResultCode

class BadRequestException : BasicException {
    constructor() : super(ResultCode.BAD_REQUEST)
    constructor(errorMessage: String) : super(ResultCode.BAD_REQUEST, errorMessage)
}
