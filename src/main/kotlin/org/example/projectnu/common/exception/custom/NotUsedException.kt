package org.example.projectnu.common.exception.custom

import org.example.projectnu.common.`object`.ResultCode

class NotUsedException : BasicException {
    constructor() : super(ResultCode.NOT_USED)
    constructor(errorMessage: String) : super(ResultCode.NOT_USED, errorMessage)
}
