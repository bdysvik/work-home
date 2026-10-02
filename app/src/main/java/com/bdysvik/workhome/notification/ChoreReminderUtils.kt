package com.bdysvik.workhome.notification

import java.util.Calendar
import java.util.concurrent.TimeUnit

object ChoreReminderUtils {
    const val TARGET_HOUR_OF_DAY: Int = 16 // 4:00 PM

    fun calculateDaysSince(
        lastCompletedAtMillis: Long?,
        nowMillis: Long = System.currentTimeMillis(),
    ): Long? {
        val millis = lastCompletedAtMillis ?: return null
        val diffMillis = nowMillis - millis
        if (diffMillis < 0L) return 0L
        return TimeUnit.MILLISECONDS.toDays(diffMillis)
    }

    fun shouldSendChoreWarning(
        lastCompletedAtMillis: Long?,
        nowMillis: Long = System.currentTimeMillis(),
    ): Boolean {
        val days = calculateDaysSince(lastCompletedAtMillis, nowMillis) ?: return true
        return days >= 2L
    }

    fun buildChoreWarningMessage(
        lastCompletedAtMillis: Long?,
        nowMillis: Long = System.currentTimeMillis(),
    ): String {
        val days = calculateDaysSince(lastCompletedAtMillis, nowMillis)
        return if (days != null) {
            "It's been $days ${if (days == 1L) "day" else "days"} since your last chore"
        } else {
            "You haven't completed a chore yet!"
        }
    }

    fun calculateNext4PmMillis(now: Calendar = Calendar.getInstance()): Long {
        val target = (now.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, TARGET_HOUR_OF_DAY)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (!after(now)) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        return target.timeInMillis
    }

    fun is4PmWindow(calendar: Calendar = Calendar.getInstance()): Boolean {
        return calendar[Calendar.HOUR_OF_DAY] == TARGET_HOUR_OF_DAY
    }
}
