package org.example.projectnu.common.`object`

data class Response<T>(
    val code: ResultCode,
    val subcode: String? = null,
    val message: String? = null,
    val data: T? = null,
)