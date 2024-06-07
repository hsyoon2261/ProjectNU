package org.example.projectnu.test.service

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.example.projectnu.common.event.args.TaskEvent
import org.example.projectnu.common.scheduler.MultiTaskScheduler
import org.example.projectnu.test.event.args.TestTask
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class TestService(
    private val taskScheduler: MultiTaskScheduler

) {
    var taskCount : Int = 0
    fun scheduleTest(i : Int) {
        taskCount = i
    }
}
