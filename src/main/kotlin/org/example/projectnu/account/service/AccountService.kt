package org.example.projectnu.account.service

import org.example.projectnu.account.dto.AccountResponseDto
import org.example.projectnu.account.dto.RegisterAccountRequestDto
import org.example.projectnu.account.entity.Account
import org.example.projectnu.account.repository.AccountRepository
import org.example.projectnu.account.status.UserRole
import org.example.projectnu.common.exception.custom.BadRequestException
import org.example.projectnu.common.security.CustomUserDetails
import org.example.projectnu.common.security.JwtTokenProvider
import org.example.projectnu.common.service.SlackService
import org.example.projectnu.common.util.AesUtil
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service

@Service
class AccountService(
    private val accountRepository: AccountRepository,
    private val slackService: SlackService,
    private val jwtTokenProvider: JwtTokenProvider
) {
    fun register(accountDto: RegisterAccountRequestDto): AccountResponseDto {
        if (accountRepository.existsByLoginId(accountDto.loginId)) {
            throw BadRequestException("Account with login ID '${accountDto.loginId}' already exists")
        }
        if (accountRepository.existsByEmail(accountDto.email)) {
            throw BadRequestException("Account with email '${accountDto.email}' already exists")
        }
        val encryptedPassword = AesUtil.encrypt(accountDto.password, accountDto.email)
        val account = Account(
            loginId = accountDto.loginId,
            password = encryptedPassword,
            email = accountDto.email,
            role = UserRole.MEMBER // 기본 역할을 USER로 설정
        )
        val savedAccount = accountRepository.save(account)
        return AccountResponseDto(
            id = savedAccount.id,
            loginId = savedAccount.loginId,
            email = savedAccount.email,
            role = savedAccount.role
        )
    }

    fun getDecryptedPassword(account: Account): String {
        return AesUtil.decrypt(account.password, account.email)
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

    fun issueToken(account: Account): String {
        val authorities = listOf(SimpleGrantedAuthority(account.role.name))
        val userDetails = CustomUserDetails(
            account.loginId,
            account.password,
            authorities,
            account.email,
            account.role.name
        )
        return jwtTokenProvider.generateToken(userDetails)
    }

    fun getAdminToken(): String {
        val adminAccount = accountRepository.findByRole(UserRole.ADMIN).firstOrNull()
            ?: throw BadRequestException("No ADMIN user found")
        return issueToken(adminAccount);
    }
}
