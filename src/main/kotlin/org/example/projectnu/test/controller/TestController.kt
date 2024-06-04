package org.example.projectnu.test.controller

import org.example.projectnu.account.service.AccountService
import org.example.projectnu.common.`object`.Response
import org.example.projectnu.common.`object`.ResultCode
import org.example.projectnu.common.service.EmailService
import org.example.projectnu.common.service.SlackService
import org.example.projectnu.common.util.AesUtil
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/test")
class TestController(
    private val slackService: SlackService,
    private val emailService: EmailService,
    private val accountService: AccountService
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
}
