package org.example.projectnu.common.exception.custom

import org.example.projectnu.common.`object`.ResultCode

open class BasicException : RuntimeException {
    var resultCode: ResultCode

    constructor(resultCode: ResultCode) : super() {
        this.resultCode = resultCode
    }

    constructor(errorMessage: String) : super(errorMessage) {
        this.resultCode = ResultCode.FAILURE
    }

    constructor(resultCode: ResultCode, errorMessage: String) : super(errorMessage) {
        this.resultCode = resultCode
    }
}
