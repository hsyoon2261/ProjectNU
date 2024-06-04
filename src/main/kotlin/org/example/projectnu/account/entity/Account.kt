package org.example.projectnu.account.entity

import org.example.projectnu.account.status.UserRole
import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size

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
)
