package org.example.projectnu.account.oauth2

import java.util.*

open class OAuth2Base {

    open fun generateRandomState(): String {
        return UUID.randomUUID().toString()
    }

}
