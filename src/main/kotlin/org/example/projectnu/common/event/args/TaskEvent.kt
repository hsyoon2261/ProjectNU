package org.example.projectnu.common.event.args

import org.springframework.context.ApplicationEvent

class TaskEvent(val task: Task) : ApplicationEvent(task)

interface Task {
    val name: String
    fun execute()
}


