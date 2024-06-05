package org.example.projectnu.common.exception.custom

import org.example.projectnu.common.`object`.ResultCode

class InternalServerErrorException : BasicException {
    constructor() : super(ResultCode.INTERNAL_SERVER_ERROR)
    constructor(errorMessage: String) : super(ResultCode.INTERNAL_SERVER_ERROR, errorMessage)
}

