package org.example.projectnu.common.dto

import org.example.projectnu.common.`object`.ResultCode

data class Response<T>(
    val code: ResultCode,
    val subcode: String? = null,
    val message: String? = null,
    val data: T? = null,
) {
    constructor(resultCode: ResultCode, message: String? = null, data: T? = null) : this(
        code = resultCode,
        subcode = resultCode.description,
        message = message,
        data = data
    )
}
