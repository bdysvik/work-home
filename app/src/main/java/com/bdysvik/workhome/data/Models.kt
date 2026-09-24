package com.bdysvik.workhome.data

enum class UserRole(val value: String) {
    ADMIN("admin"),
    MEMBER("member");

    companion object {
        fun from(value: String?): UserRole =
            if (value.equals(ADMIN.value, ignoreCase = true)) ADMIN else MEMBER
    }
}

data class AppUser(
    val id: String,
    val name: String,
    val email: String,
    val role: UserRole,
    val authUid: String,
    val currentRewardTotal: Long,
) {
    val isAdmin: Boolean = role == UserRole.ADMIN
}

data class PendingUser(
    val emailKey: String,
    val name: String,
    val email: String,
    val role: UserRole,
)

data class ChoreTemplate(
    val id: String,
    val title: String,
    val reward: Long,
    val createdBy: String,
)

data class Chore(
    val id: String,
    val title: String,
    val reward: Long,
    val createdBy: String,
    val active: Boolean,
)
