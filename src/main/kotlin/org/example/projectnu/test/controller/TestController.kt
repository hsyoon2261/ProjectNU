package org.example.projectnu.test.controller

import io.swagger.v3.oas.annotations.Operation
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.example.projectnu.account.service.AccountService
import org.example.projectnu.common.annotation.RedisIndex
import org.example.projectnu.common.dto.Response
import org.example.projectnu.common.`object`.ResultCode
import org.example.projectnu.common.scheduler.MultiTaskScheduler
import org.example.projectnu.common.service.EmailService
import org.example.projectnu.common.service.RedisService
import org.example.projectnu.common.service.SlackService
import org.example.projectnu.common.util.AesUtil
import org.example.projectnu.jira.service.CaptureJiraService
import org.example.projectnu.test.event.args.TestTask
import org.example.projectnu.test.service.TestService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import kotlin.system.measureTimeMillis

@RedisIndex(3)
@RestController
@RequestMapping("/test")
class TestController(
    private val slackService: SlackService,
    private val emailService: EmailService,
    private val accountService: AccountService,
    private val redisService: RedisService,
    private val jiraService: CaptureJiraService,
    private val taskScheduler: MultiTaskScheduler,
    private val testService: TestService,
) {

    @GetMapping("/success")
    fun getSuccess(): ResponseEntity<Response<String>> {
        val data = "This is a successful response"
        return ResponseEntity.ok(Response(ResultCode.SUCCESS, data = data))
    }

    @Operation(summary = "Get failure response")
    @GetMapping("/failure")
    fun getFailure(): ResponseEntity<Response<String>> {
        val message = "This is a failure response"
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            Response(
                code = ResultCode.FAILURE,
                subcode = ResultCode.FAILURE.description,
                message = message
            )
        )
    }

    @GetMapping("/slack")
    fun getSlack(): ResponseEntity<Response<String>> {
        return ResponseEntity.ok(Response(ResultCode.SUCCESS, data = slackService.getSlackUrlString()))
    }

    @GetMapping("/slack/test")
    fun getSlackTest(@RequestParam testMessage: String): ResponseEntity<Response<String>> {
        slackService.sendTestMessage(testMessage)
        return ResponseEntity.ok(Response(ResultCode.SUCCESS, data = "This is a successful response"))
    }

    @GetMapping("/email")
    fun sendEmail(
        @RequestParam to: String,
        @RequestParam subject: String,
        @RequestParam text: String
    ): ResponseEntity<Response<String>> {
        emailService.sendSimpleMessage(to, subject, text)
        return ResponseEntity.ok(Response(ResultCode.SUCCESS, data = "Email sent successfully"))
    }

    @GetMapping("/encrypt")
    fun encryptPassword(
        @RequestParam password: String,
        @RequestParam email: String
    ): ResponseEntity<Response<String>> {
        val encryptedPassword = AesUtil.encrypt(password, email)
        return ResponseEntity.ok(Response(ResultCode.SUCCESS, data = encryptedPassword))
    }

    @GetMapping("/sendSlackToAdmin")
    fun sendSlackToAdmin(@RequestParam message: String): ResponseEntity<Response<String>> {
        accountService.sendSlackMessageToAdmin(message)
        return ResponseEntity.ok(Response(ResultCode.SUCCESS, data = "Message sent to admin"))
    }

    @GetMapping("/getAdminToken")
    fun getAdminToken(): ResponseEntity<Response<String>> {
        val token = accountService.getAdminToken()
        return ResponseEntity.ok(Response(ResultCode.SUCCESS, data = token))
    }

    @RedisIndex(5)
    @PostMapping("/redis/set")
    fun setRedisValue(@RequestParam key: String, @RequestParam value: String): ResponseEntity<Response<String>> {
        redisService.set(key, value)
        return ResponseEntity.ok(Response(ResultCode.SUCCESS, data = "Value set successfully"))
    }

    // Redis String Get API
    @GetMapping("/redis/get")
    fun getRedisValue(@RequestParam key: String): ResponseEntity<Response<String>> {
        val value = redisService.get(key) as? String
        return if (value != null) {
            ResponseEntity.ok(Response(ResultCode.SUCCESS, data = value))
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(Response(ResultCode.FAILURE, message = "Key not found"))
        }
    }

    // Redis String Delete API
    @DeleteMapping("/redis/delete")
    fun deleteRedisValue(@RequestParam key: String): ResponseEntity<Response<String>> {
        redisService.delete(key)
        return ResponseEntity.ok(Response(ResultCode.SUCCESS, data = "Value deleted successfully"))
    }

    @GetMapping("/jira")
    fun getJira(): ResponseEntity<Response<String>> {
        val data = jiraService.getSamplePage()
        return ResponseEntity.ok(Response(ResultCode.SUCCESS, data = data))
    }

    @GetMapping("/workers/tasks")
    fun scheduleTasks(@RequestParam count: Int?): ResponseEntity<Response<String>> {
        val taskCount = count ?: 1
        testService.scheduleTest(taskCount)
        return ResponseEntity.ok(Response(ResultCode.SUCCESS, data = "Scheduled $taskCount tasks"))
    }

    @GetMapping("/workers/status")
    fun getWorkersStatus(): ResponseEntity<Response<Map<String, Any>>> {
        val status = taskScheduler.getWorkersStatus()
        return ResponseEntity.ok(Response(ResultCode.SUCCESS, data = status))
    }

    @GetMapping("/workers/pending-tasks")
    fun getTotalPendingTasks(): ResponseEntity<Response<Int>> {
        val pendingTasks = taskScheduler.getTotalPendingTasks()
        return ResponseEntity.ok(Response(ResultCode.SUCCESS, data = pendingTasks))
    }

    @GetMapping("/simpleTest")
    fun simpleTest(@RequestParam(defaultValue = "100") totalTasks: Int?): ResponseEntity<Response<Long>> {
        var result = 0
        val tasks = totalTasks ?: 100 // 기본값 설정
        val time = measureTimeMillis {
            runBlocking {
                for (i in 1..tasks) {
                    val testTask = TestTask("test$i")
                    result += testTask.execute()
                }
            }
        }
        val totalResult = totalTasks
        return if (result == totalResult) {
            ResponseEntity.ok(Response(ResultCode.SUCCESS, data = time))
        } else {
            ResponseEntity.ok(Response(ResultCode.FAILURE, data = time))
        }
    }

    @GetMapping("/schedulerTest")
    fun schedulerTest(@RequestParam(defaultValue = "100") totalTasks: Int?): ResponseEntity<Response<Long>> {
        var result = 0
        val tasks = totalTasks ?: 100 // 기본값 설정
        val batchSize = 50000
        val time = measureTimeMillis {
            runBlocking {
                val batches = (1..tasks).chunked(batchSize)
                for (batch in batches) {
                    val deferredResults = batch.map { i ->
                        val testTask = TestTask("test$i")
                        async {
                            taskScheduler.publishEvent(testTask)
                        }
                    }
                    result += deferredResults.awaitAll().sum()
                }
            }
        }
        return if (result == tasks) {
            ResponseEntity.ok(Response(ResultCode.SUCCESS, data = time))
        } else {
            ResponseEntity.ok(Response(ResultCode.FAILURE, data = time))
        }
    }

    @GetMapping("/schedulerTestBulk")
    fun schedulerTestBulk(@RequestParam(defaultValue = "100") totalTasks: Int?): ResponseEntity<Response<Long>> {
        var result = 0
        val tasks = totalTasks ?: 100 // 기본값 설정
        val batchSize = 50000
        val time = measureTimeMillis {
            runBlocking {
                val batches = (1..tasks).chunked(batchSize)
                for (batch in batches) {
                    val testTasks = batch.map { i -> TestTask("test$i") }
                    val batchResults = taskScheduler.publishEventBulk(testTasks)
                    result += batchResults.sum()
                }
            }
        }
        return if (result == tasks) {
            ResponseEntity.ok(Response(ResultCode.SUCCESS, data = time))
        } else {
            ResponseEntity.ok(Response(ResultCode.FAILURE, data = time))
        }
    }

    @GetMapping("/executeTestTask")
    fun executeTestTask(@RequestParam(defaultValue = "1") taskCount: Int?): ResponseEntity<Response<Long>> {
        val tasks = taskCount ?: 0
        var totalResult = 0
        val time = measureTimeMillis {
            runBlocking {
                val results = (1..tasks).map {
                    async {
                        taskScheduler.execute {
                            delay(10)
                            1
                        }
                    }
                }
                totalResult = results.awaitAll().sum()
            }
        }
        return if (totalResult == tasks) {
            ResponseEntity.ok(Response(ResultCode.SUCCESS, data = time))
        } else {
            ResponseEntity.ok(Response(ResultCode.FAILURE, data = time))
        }
    }

    @GetMapping("/executeBulkTestTask")
    fun executeBulkTestTask(@RequestParam(defaultValue = "100") totalTasks: Int?): ResponseEntity<Response<Long>> {
        val tasks = totalTasks ?: 100 // 기본값 설정
        var resultCode = ResultCode.FAILURE
        val time = measureTimeMillis {
            runBlocking {
                val results = taskScheduler.executeBulk((1..tasks).map {
                    suspend {
                        delay(10)
                        1
                    }
                })
                resultCode = if (results.all { it == 1 }) {
                    ResultCode.SUCCESS
                } else {
                    ResultCode.FAILURE
                }
            }
        }
        return ResponseEntity.ok(Response(resultCode, data = time))
    }





//    @DeleteMapping("/redis/flushall")
//    fun flushAllRedis(): ResponseEntity<Response<String>> {
//        redisService.f()
//        return ResponseEntity.ok(Response(ResultCode.SUCCESS, data = "All data deleted successfully"))
//    }
}
