package org.example.projectnu.common.event.args

import kotlinx.coroutines.CompletableDeferred

data class TaskEvent<T>(val task: Task<T>, val result: CompletableDeferred<T>)


interface Task<T> {
    val name: String
    suspend fun execute(): T
}


