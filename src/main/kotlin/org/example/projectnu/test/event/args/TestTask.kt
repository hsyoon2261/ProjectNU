package org.example.projectnu.test.event.args

import org.example.projectnu.common.event.args.Task

class TestTask(override val name: String) : Task {
    override fun execute() {
        Thread.sleep(15)
        println("Executing task: $name")
    }
}
