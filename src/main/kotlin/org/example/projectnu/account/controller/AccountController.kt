package org.example.projectnu.account.controller

import org.example.projectnu.account.dto.AccountResponseDto
import org.example.projectnu.account.dto.RegisterAccountRequestDto
import org.example.projectnu.account.service.AccountService
import org.example.projectnu.common.`object`.Response
import org.example.projectnu.common.`object`.ResultCode
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/accounts")
class AccountController(
    private val accountService: AccountService
) {

    @PostMapping("/register")
    fun register(@RequestBody @Valid accountDto: RegisterAccountRequestDto): ResponseEntity<Response<AccountResponseDto>> {
        val registeredAccount = accountService.register(accountDto)
        return ResponseEntity.status(201).body(Response(ResultCode.SUCCESS, data = registeredAccount))
    }

    // 다른 엔드포인트들...
}
