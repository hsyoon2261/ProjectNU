package org.example.projectnu.common.exception.custom

class NotFoundException : RuntimeException {
    constructor() : super()
    constructor(errorMessage: String) : super(errorMessage)
}
