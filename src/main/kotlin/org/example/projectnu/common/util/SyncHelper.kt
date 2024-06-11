package org.example.projectnu.common.util

import kotlinx.coroutines.runBlocking

fun <T> (suspend () -> T).sync(): T? {
    var result: T? = null
    runBlocking {
        result = this@sync.invoke()
    }
    return result
}
