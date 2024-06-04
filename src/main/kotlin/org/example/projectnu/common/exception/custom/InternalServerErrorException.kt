package org.example.projectnu.common.exception.custom

class InternalServerErrorException : RuntimeException {
    constructor() : super()
    constructor(errorMessage: String) : super(errorMessage)
}
