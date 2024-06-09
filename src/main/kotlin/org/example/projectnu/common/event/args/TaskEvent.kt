package org.example.projectnu.common.event.args

import kotlinx.coroutines.CompletableDeferred

internal data class TaskEvent<T>(val task: Task<T>, val result: CompletableDeferred<T>)


internal interface Task<T> {
    val name: String
    suspend fun execute(): T
}


