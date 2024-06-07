package org.example.projectnu.test.service

import org.example.projectnu.common.event.args.TaskEvent
import org.example.projectnu.common.scheduler.MultiTaskScheduler
import org.example.projectnu.test.event.args.TestTask
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class TestService(
    private val taskScheduler: MultiTaskScheduler

) {
    var taskCount : Int = 1
    fun scheduleTest(i : Int) {
        taskCount = i
    }

    @Scheduled(fixedRate = 1000)
    fun scheduleTest() {
        for (i in 1..taskCount) {
            val testTask = TestTask("test$i")
            taskScheduler.onApplicationEvent(TaskEvent(testTask))
        }
    }
}
