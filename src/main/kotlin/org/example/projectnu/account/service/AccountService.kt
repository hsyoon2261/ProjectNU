package org.example.projectnu.account.service

import org.example.projectnu.account.dto.request.RegisterAccountRequestDto
import org.example.projectnu.account.dto.request.SignInRequestDto
import org.example.projectnu.account.dto.response.AccountResponseDto
import org.example.projectnu.account.dto.response.SignInResponseDto
import org.example.projectnu.account.entity.Account
import org.example.projectnu.account.repository.AccountRepository
import org.example.projectnu.account.service.internal.AccountCore
import org.example.projectnu.account.status.UserRole
import org.example.projectnu.common.exception.custom.BadRequestException
import org.example.projectnu.common.security.JwtTokenProvider
import org.example.projectnu.common.service.SlackService
import org.example.projectnu.common.util.mapper.Authorize
import org.springframework.stereotype.Service

@Service
class AccountService(
    private val accountRepository: AccountRepository,
    private val slackService: SlackService,
    private val jwtTokenProvider: JwtTokenProvider
) {
    private val core = AccountCore(accountRepository)

    fun register(accountDto: RegisterAccountRequestDto): AccountResponseDto {
        core.validateDuplicateAccount(accountDto.loginId, accountDto.email)
        val account = Account.createMemberSimple(accountDto)
        val savedAccount = accountRepository.save(account)
        return Account.toResponseDto(savedAccount)
    }

    fun sendSlackMessageToAdmin(message: String) {
        val adminAccounts = accountRepository.findByRole(UserRole.ADMIN)
        if (adminAccounts.isEmpty()) {
            throw BadRequestException("No ADMIN user found")
        }

        adminAccounts.forEach { adminAccount ->
            val channel = "@${adminAccount.loginId}"
            slackService.sendMessage(channel, message)
        }
    }

    fun issueToken(account: Account, jSessionId: String): String {
        return jwtTokenProvider.generateToken(Authorize.getCustomUserDetails(account, jSessionId))
    }

    fun getAdminToken(): String {
        val adminAccount = accountRepository.findByRole(UserRole.ADMIN).firstOrNull()
            ?: throw BadRequestException("No ADMIN user found")
        return issueToken(adminAccount, "ALL_PASS")
    }

    fun getAccountByUserName(username: String): Account? {
        return accountRepository.findByLoginId(username)
    }

    fun signIn(signInRequest: SignInRequestDto, jSessionId: String): SignInResponseDto {
        val account = getAccountByUserName(signInRequest.loginId)
            ?: throw BadRequestException("Account with login ID '${signInRequest.loginId}' does not exist")

        val decryptedPassword = core.getDecryptedPassword(account)
        if (decryptedPassword != signInRequest.password) {
            throw BadRequestException("Invalid password")
        }

        val jwtToken = issueToken(account, jSessionId)

        return SignInResponseDto(jwtToken = jwtToken)
    }
}
