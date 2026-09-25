package com.bdysvik.workhome.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.TimeUnit

class ChoreTemplateTest {

    private fun createTemplate(
        repeatIntervalDays: Int?,
        lastSpawnedAtMillis: Long?,
    ): ChoreTemplate {
        return ChoreTemplate(
            id = "template1",
            title = "Wash Dishes",
            reward = 10,
            createdBy = "admin1",
            repeatIntervalDays = repeatIntervalDays,
            lastSpawnedAtMillis = lastSpawnedAtMillis,
        )
    }

    @Test
    fun isDue_manualTemplate_returnsFalse() {
        val template = createTemplate(repeatIntervalDays = null, lastSpawnedAtMillis = null)
        assertFalse(template.isDue())

        val templateZero = createTemplate(repeatIntervalDays = 0, lastSpawnedAtMillis = null)
        assertFalse(templateZero.isDue())
    }

    @Test
    fun isDue_firstTimeWithInterval_returnsTrue() {
        // When never spawned before, it is immediately due
        val templateDaily = createTemplate(repeatIntervalDays = 1, lastSpawnedAtMillis = null)
        assertTrue(templateDaily.isDue())

        val templateTwoDays = createTemplate(repeatIntervalDays = 2, lastSpawnedAtMillis = null)
        assertTrue(templateTwoDays.isDue())
    }

    @Test
    fun isDue_dailyInterval_evaluatesCorrectly() {
        val now = 10_000_000_000L
        val twelveHoursAgo = now - TimeUnit.HOURS.toMillis(12)
        val exactlyOneDayAgo = now - TimeUnit.DAYS.toMillis(1)
        val twoDaysAgo = now - TimeUnit.DAYS.toMillis(2)

        val notDue = createTemplate(repeatIntervalDays = 1, lastSpawnedAtMillis = twelveHoursAgo)
        assertFalse(notDue.isDue(now))

        val dueExact = createTemplate(repeatIntervalDays = 1, lastSpawnedAtMillis = exactlyOneDayAgo)
        assertTrue(dueExact.isDue(now))

        val dueOverdue = createTemplate(repeatIntervalDays = 1, lastSpawnedAtMillis = twoDaysAgo)
        assertTrue(dueOverdue.isDue(now))
    }

    @Test
    fun isDue_everyTwoDaysInterval_evaluatesCorrectly() {
        val now = 10_000_000_000L
        val oneDayAgo = now - TimeUnit.DAYS.toMillis(1)
        val exactlyTwoDaysAgo = now - TimeUnit.DAYS.toMillis(2)
        val threeDaysAgo = now - TimeUnit.DAYS.toMillis(3)

        val notDue = createTemplate(repeatIntervalDays = 2, lastSpawnedAtMillis = oneDayAgo)
        assertFalse(notDue.isDue(now))

        val dueExact = createTemplate(repeatIntervalDays = 2, lastSpawnedAtMillis = exactlyTwoDaysAgo)
        assertTrue(dueExact.isDue(now))

        val dueOverdue = createTemplate(repeatIntervalDays = 2, lastSpawnedAtMillis = threeDaysAgo)
        assertTrue(dueOverdue.isDue(now))
    }

    @Test
    fun recurrenceLabel_formatsCorrectly() {
        assertEquals("Manual", createTemplate(null, null).recurrenceLabel())
        assertEquals("Manual", createTemplate(0, null).recurrenceLabel())
        assertEquals("Every day", createTemplate(1, null).recurrenceLabel())
        assertEquals("Every 2 days", createTemplate(2, null).recurrenceLabel())
        assertEquals("Every 3 days", createTemplate(3, null).recurrenceLabel())
        assertEquals("Every week", createTemplate(7, null).recurrenceLabel())
        assertEquals("Every 5 days", createTemplate(5, null).recurrenceLabel())
    }
}
