package org.example.projectnu.account.controller

import jakarta.annotation.security.PermitAll
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.example.projectnu.account.dto.response.AccountResponseDto
import org.example.projectnu.account.dto.request.RegisterAccountRequestDto
import org.example.projectnu.account.dto.request.SignInRequestDto
import org.example.projectnu.account.dto.response.SignInResponseDto
import org.example.projectnu.account.service.AccountService
import org.example.projectnu.common.dto.Response
import org.example.projectnu.common.exception.custom.BadRequestException
import org.example.projectnu.common.`object`.ResultCode
import org.example.projectnu.common.util.mapper.Authorize
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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

    @PostMapping("/signin")
    fun signIn(@RequestBody signInRequest: SignInRequestDto, request: HttpServletRequest): ResponseEntity<Response<SignInResponseDto>> {

        val jSessionId = Authorize.getJSession(request)

        val res = Response(ResultCode.SUCCESS, data = accountService.signIn(signInRequest,jSessionId))

        return ResponseEntity(res, res.code.httpStatus)
    }
}
