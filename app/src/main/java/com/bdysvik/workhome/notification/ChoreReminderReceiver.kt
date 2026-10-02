package com.bdysvik.workhome.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bdysvik.workhome.data.FirebaseFamilyRepository
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ChoreReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            ChoreReminderScheduler.scheduleDaily4PmReminder(context)
            return
        }

        // Reschedule for next day at 4 PM
        ChoreReminderScheduler.scheduleDaily4PmReminder(context)

        // Strict 4 PM check: only show warning if within the 4 PM hour
        if (!ChoreReminderUtils.is4PmWindow()) {
            return
        }

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (FirebaseApp.getApps(context).isEmpty()) {
                    FirebaseApp.initializeApp(context)
                }

                // Check and auto-generate any due chores from scheduled templates
                val firestore = FirebaseFirestore.getInstance()
                runCatching {
                    FirebaseFamilyRepository(firestore).generateScheduledChores()
                }

                val currentUser = FirebaseAuth.getInstance().currentUser ?: return@launch
                val userDoc = firestore
                    .collection("users")
                    .document(currentUser.uid)
                    .get()
                    .await()

                if (!userDoc.exists()) {
                    return@launch
                }

                val lastCompletedAtMillis = userDoc.getTimestamp("lastCompletedAt")?.toDate()?.time
                    ?: userDoc.getLong("lastCompletedAt")

                if (ChoreReminderUtils.shouldSendChoreWarning(lastCompletedAtMillis)) {
                    val message = ChoreReminderUtils.buildChoreWarningMessage(lastCompletedAtMillis)
                    NotificationHelper.showChoreWarningNotification(context, message)
                }
            } catch (_: Exception) {
                // Ignore background network/firestore fetch errors
            } finally {
                pendingResult.finish()
            }
        }
    }
}
