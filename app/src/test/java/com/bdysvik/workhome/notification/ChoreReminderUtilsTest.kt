package com.bdysvik.workhome.notification

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar
import java.util.concurrent.TimeUnit

class ChoreReminderUtilsTest {

    @Test
    fun calculateDaysSince_returnsNullWhenTimestampIsNull() {
        assertNull(ChoreReminderUtils.calculateDaysSince(null))
    }

    @Test
    fun calculateDaysSince_returnsZeroWithinSameDay() {
        val now = 1_000_000_000L
        val lastCompleted = now - TimeUnit.HOURS.toMillis(12)
        assertEquals(0L, ChoreReminderUtils.calculateDaysSince(lastCompleted, now))
    }

    @Test
    fun calculateDaysSince_returnsCorrectDaysElapsed() {
        val now = 1_000_000_000L
        val oneDayAgo = now - TimeUnit.DAYS.toMillis(1)
        val twoDaysAgo = now - TimeUnit.DAYS.toMillis(2)
        val fourDaysAgo = now - TimeUnit.DAYS.toMillis(4)

        assertEquals(1L, ChoreReminderUtils.calculateDaysSince(oneDayAgo, now))
        assertEquals(2L, ChoreReminderUtils.calculateDaysSince(twoDaysAgo, now))
        assertEquals(4L, ChoreReminderUtils.calculateDaysSince(fourDaysAgo, now))
    }

    @Test
    fun shouldSendChoreWarning_trueWhenNeverCompleted() {
        assertTrue(ChoreReminderUtils.shouldSendChoreWarning(null))
    }

    @Test
    fun shouldSendChoreWarning_falseWhenLessThanTwoDays() {
        val now = 1_000_000_000L
        val sameDay = now - TimeUnit.HOURS.toMillis(6)
        val oneDayAgo = now - TimeUnit.HOURS.toMillis(24)
        val almostTwoDays = now - TimeUnit.HOURS.toMillis(47)

        assertFalse(ChoreReminderUtils.shouldSendChoreWarning(sameDay, now))
        assertFalse(ChoreReminderUtils.shouldSendChoreWarning(oneDayAgo, now))
        assertFalse(ChoreReminderUtils.shouldSendChoreWarning(almostTwoDays, now))
    }

    @Test
    fun shouldSendChoreWarning_trueWhenTwoOrMoreDays() {
        val now = 1_000_000_000L
        val exactlyTwoDays = now - TimeUnit.DAYS.toMillis(2)
        val threeDaysAgo = now - TimeUnit.DAYS.toMillis(3)
        val fourDaysAgo = now - TimeUnit.DAYS.toMillis(4)

        assertTrue(ChoreReminderUtils.shouldSendChoreWarning(exactlyTwoDays, now))
        assertTrue(ChoreReminderUtils.shouldSendChoreWarning(threeDaysAgo, now))
        assertTrue(ChoreReminderUtils.shouldSendChoreWarning(fourDaysAgo, now))
    }

    @Test
    fun buildChoreWarningMessage_formatsTwoDays() {
        val now = 1_000_000_000L
        val twoDaysAgo = now - TimeUnit.DAYS.toMillis(2)
        assertEquals("It's been 2 days since your last chore", ChoreReminderUtils.buildChoreWarningMessage(twoDaysAgo, now))
    }

    @Test
    fun buildChoreWarningMessage_formatsFourDays() {
        val now = 1_000_000_000L
        val fourDaysAgo = now - TimeUnit.DAYS.toMillis(4)
        assertEquals("It's been 4 days since your last chore", ChoreReminderUtils.buildChoreWarningMessage(fourDaysAgo, now))
    }

    @Test
    fun buildChoreWarningMessage_formatsNoChoresCompleted() {
        assertEquals("You haven't completed a chore yet!", ChoreReminderUtils.buildChoreWarningMessage(null))
    }

    @Test
    fun is4PmWindow_trueOnlyDuring16Hour() {
        val cal = Calendar.getInstance()

        cal.set(Calendar.HOUR_OF_DAY, 16)
        cal.set(Calendar.MINUTE, 0)
        assertTrue(ChoreReminderUtils.is4PmWindow(cal))

        cal.set(Calendar.HOUR_OF_DAY, 16)
        cal.set(Calendar.MINUTE, 59)
        assertTrue(ChoreReminderUtils.is4PmWindow(cal))

        cal.set(Calendar.HOUR_OF_DAY, 15)
        cal.set(Calendar.MINUTE, 59)
        assertFalse(ChoreReminderUtils.is4PmWindow(cal))

        cal.set(Calendar.HOUR_OF_DAY, 17)
        cal.set(Calendar.MINUTE, 0)
        assertFalse(ChoreReminderUtils.is4PmWindow(cal))

        cal.set(Calendar.HOUR_OF_DAY, 9)
        assertFalse(ChoreReminderUtils.is4PmWindow(cal))
    }

    @Test
    fun calculateNext4PmMillis_before4PmSchedulesForToday() {
        val now = Calendar.getInstance().apply {
            set(2026, Calendar.MARCH, 25, 10, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val resultMillis = ChoreReminderUtils.calculateNext4PmMillis(now)
        val expected = Calendar.getInstance().apply {
            set(2026, Calendar.MARCH, 25, 16, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }

        assertEquals(expected.timeInMillis, resultMillis)
    }

    @Test
    fun calculateNext4PmMillis_atOrAfter4PmSchedulesForTomorrow() {
        val at4Pm = Calendar.getInstance().apply {
            set(2026, Calendar.MARCH, 25, 16, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val resultMillisFrom4Pm = ChoreReminderUtils.calculateNext4PmMillis(at4Pm)
        val expected = Calendar.getInstance().apply {
            set(2026, Calendar.MARCH, 26, 16, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }

        assertEquals(expected.timeInMillis, resultMillisFrom4Pm)

        val after4Pm = Calendar.getInstance().apply {
            set(2026, Calendar.MARCH, 25, 18, 30, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val resultMillisFromAfter4Pm = ChoreReminderUtils.calculateNext4PmMillis(after4Pm)
        assertEquals(expected.timeInMillis, resultMillisFromAfter4Pm)
    }
}
