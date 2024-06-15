package org.example.projectnu.account.service.internal

import org.example.projectnu.account.entity.Account
import org.example.projectnu.account.repository.AccountRepository
import org.example.projectnu.common.exception.custom.BadRequestException
import org.example.projectnu.common.util.AesUtil

internal class AccountCore(
    private val repository: AccountRepository,
) {
    fun getDecryptedPassword(account: Account): String {
        return AesUtil.decrypt(account.password, account.email)
    }

    fun validateDuplicateAccount(loginId: String, email: String) {
        if (repository.existsByLoginId(loginId)) {
            throw BadRequestException("Account with login ID '$loginId' already exists")
        }
        if (repository.existsByEmail(email)) {
            throw BadRequestException("Account with email '$email' already exists")
        }
    }
}
