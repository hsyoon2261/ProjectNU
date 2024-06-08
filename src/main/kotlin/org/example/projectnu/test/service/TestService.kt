package org.example.projectnu.test.service

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.example.projectnu.common.annotation.Callee
import org.example.projectnu.common.annotation.Caller
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

    @Caller(CustomRequest::class, CustomResponse::class)
    fun invoke(req : CustomRequest) : List<CustomResponse> {
        return emptyList()
    }

    @Callee(CustomRequest::class, CustomResponse::class)
    fun someLogic(req: CustomRequest): CustomResponse {
        // 실제 로직
        return CustomResponse("Processed: ${req.data}, someLogic")
    }

    @Callee(CustomRequest::class, CustomResponse::class)
    fun someLogic2(req: CustomRequest): CustomResponse {
        // 실제 로직
        return CustomResponse("Processed: ${req.data}, someLogic2")
    }
}


data class CustomRequest(val data: String)
data class CustomResponse(val result: String)
