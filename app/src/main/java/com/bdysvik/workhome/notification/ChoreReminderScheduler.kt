package com.bdysvik.workhome.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

object ChoreReminderScheduler {
    const val REQUEST_CODE = 4001
    const val ACTION_CHECK_CHORE_REMINDER = "com.bdysvik.workhome.notification.CHECK_CHORE_REMINDER"

    fun scheduleDaily4PmReminder(context: Context) {
        val alarmManager = (context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager) ?: return
        val intent = Intent(context, ChoreReminderReceiver::class.java).apply {
            action = ACTION_CHECK_CHORE_REMINDER
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val triggerAtMillis = ChoreReminderUtils.calculateNext4PmMillis()
        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pendingIntent,
        )
    }

    fun cancelDailyReminder(context: Context) {
        val alarmManager = (context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager) ?: return
        val intent = Intent(context, ChoreReminderReceiver::class.java).apply {
            action = ACTION_CHECK_CHORE_REMINDER
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE,
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }
}
