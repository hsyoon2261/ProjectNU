package org.example.projectnu.account.status

enum class UserRole(val order: Int) {
    ADMIN(5),
    MANAGER(4),
    VIP(3),
    MEMBER(2),
    GUEST(1),
    DISABLED(0);

    companion object {
        fun fromString(role: String): UserRole {
            return entries.firstOrNull {
                it.name.equals(role, ignoreCase = true)
            } ?: GUEST
        }
    }
}


enum class Domain {
    PUBLIC,
    NSUS,
    THIRD_PARTY,
}
