package org.example.projectnu.test.event.args

import kotlinx.coroutines.delay
import org.example.projectnu.common.event.args.Task

class TestTask(override val name: String) : Task<Int> {
    override suspend fun execute(): Int {
        delay(10)
        return 1
    }
}
