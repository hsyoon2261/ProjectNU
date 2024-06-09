package org.example.projectnu.common.annotation

import org.example.projectnu.account.status.Domain
import org.example.projectnu.account.status.UserRole

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class AccessLevel(
    val userRole: UserRole = UserRole.GUEST,
    val domain: Domain = Domain.PUBLIC
)
