package com.bdysvik.workhome.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

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
    val lastCompletedAtMillis: Long? = null,
    val rewardGoal: Long? = null,
) {
    val isAdmin: Boolean = role == UserRole.ADMIN

    fun daysSinceLastCompleted(nowMillis: Long = System.currentTimeMillis()): Long? {
        val millis = lastCompletedAtMillis ?: return null
        val diffMillis = nowMillis - millis
        if (diffMillis < 0) return 0L
        return TimeUnit.MILLISECONDS.toDays(diffMillis)
    }

    fun rewardProgressText(): String {
        val goal = rewardGoal
        return if ((goal != null) && (goal > 0)) {
            val percentage = Math.round((currentRewardTotal.toDouble() / goal) * 100)
            "$currentRewardTotal / $goal ($percentage%)"
        } else {
            currentRewardTotal.toString()
        }
    }
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
    val repeatIntervalDays: Int? = null,
    val lastSpawnedAtMillis: Long? = null,
) {
    fun isDue(nowMillis: Long = System.currentTimeMillis()): Boolean {
        val interval = repeatIntervalDays ?: return false
        if (interval <= 0) return false
        val lastSpawned = lastSpawnedAtMillis ?: return true
        val intervalMillis = interval * 24L * 60L * 60L * 1000L
        return (nowMillis - lastSpawned) >= intervalMillis
    }

    fun recurrenceLabel(): String = when (repeatIntervalDays) {
        null, 0 -> "Manual"
        1 -> "Every day"
        2 -> "Every 2 days"
        3 -> "Every 3 days"
        7 -> "Every week"
        else -> "Every $repeatIntervalDays days"
    }
}

data class Chore(
    val id: String,
    val title: String,
    val reward: Long,
    val createdBy: String,
    val active: Boolean,
    val assignedToUserId: String = "",
)

data class CompletedChore(
    val id: String,
    val choreId: String,
    val title: String,
    val reward: Long,
    val userId: String,
    val userName: String = "",
    val completedAtMillis: Long? = null,
) {
    fun formattedDate(
        nowMillis: Long = System.currentTimeMillis(),
        timeZone: TimeZone = TimeZone.getDefault(),
    ): String = formatCompletionDate(completedAtMillis, nowMillis, timeZone)
}

fun formatCompletionDate(
    millis: Long?,
    nowMillis: Long = System.currentTimeMillis(),
    timeZone: TimeZone = TimeZone.getDefault(),
): String {
    val epoch = millis ?: nowMillis
    val formatter = SimpleDateFormat("EEEE MMMM d", Locale.US).apply {
        this.timeZone = timeZone
    }
    return formatter.format(Date(epoch)).lowercase(Locale.US)
}
