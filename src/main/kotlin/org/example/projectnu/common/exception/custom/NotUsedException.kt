package org.example.projectnu.common.exception.custom

class NotUsedException : RuntimeException {
    constructor() : super()
    constructor(errorMessage: String) : super(errorMessage)
}