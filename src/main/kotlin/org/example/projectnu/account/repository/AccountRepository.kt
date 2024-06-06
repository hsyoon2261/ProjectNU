package org.example.projectnu.account.repository

import org.example.projectnu.account.entity.Account
import org.example.projectnu.account.status.UserRole
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface AccountRepository : JpaRepository<Account, Long> {
    fun existsByLoginId(loginId: String): Boolean
    fun existsByEmail(email: String): Boolean
    fun findByRole(role: UserRole): List<Account>

    @Query("SELECT COUNT(a) FROM Account a WHERE a.role = :role")
    fun countByRole(role: UserRole): Long

    fun findByLoginId(loginId: String): Account?
}
