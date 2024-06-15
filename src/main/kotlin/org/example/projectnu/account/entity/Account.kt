package org.example.projectnu.account.entity

import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size
import org.example.projectnu.account.dto.request.RegisterAccountRequestDto
import org.example.projectnu.account.dto.response.AccountResponseDto
import org.example.projectnu.account.status.UserRole
import org.example.projectnu.common.util.AesUtil

@Entity
@Table(name = "account")
class Account(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @Column(nullable = false, length = 30, unique = true)
    @Size(max = 30, message = "Login ID must be 30 characters or less")
    val loginId: String,
    @Column(nullable = false)
    val password: String,
    @Column(nullable = false, unique = true)
    @Email(regexp = "^[A-Za-z0-9+_.-]+@(.+)\$", message = "Email should be valid")
    val email: String,
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val role: UserRole
) {
    companion object {

        fun create(loginId: String, password: String, email: String, role: UserRole): Account {
            return Account(loginId = loginId, password = AesUtil.encrypt(password, email), email = email, role = role)
        }

        fun createMemberSimple(accountDto : RegisterAccountRequestDto): Account {
            return Account(loginId = accountDto.loginId, password = AesUtil.encrypt(accountDto.password, accountDto.email), email = accountDto.email, role = UserRole.MEMBER)
        }

        fun toResponseDto(account: Account): AccountResponseDto {
            return AccountResponseDto(
                id = account.id,
                loginId = account.loginId,
                email = account.email,
                role = account.role
            )
        }
    }
}
