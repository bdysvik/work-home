package com.bdysvik.workhome.ui

import com.bdysvik.workhome.data.formatCompletionDate
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone

class WorkHomeAppTest {
    @Test
    fun daysLeftInCurrentMonth_returnsRemainingDaysBeforeMonthEnds() {
        assertEquals(6, daysLeftInCurrentMonth(LocalDate.of(2026, 9, 24)))
    }

    @Test
    fun daysLeftInCurrentMonth_handlesThirtyOneDayMonths() {
        assertEquals(16, daysLeftInCurrentMonth(LocalDate.of(2026, 1, 15)))
    }

    @Test
    fun daysLeftInCurrentMonth_handlesLeapYearFebruary() {
        assertEquals(1, daysLeftInCurrentMonth(LocalDate.of(2028, 2, 28)))
    }

    @Test
    fun daysLeftInCurrentMonth_handlesNonLeapYearFebruary() {
        assertEquals(0, daysLeftInCurrentMonth(LocalDate.of(2027, 2, 28)))
    }

    @Test
    fun daysLeftInCurrentMonth_returnsZeroOnLastDayOfMonth() {
        assertEquals(0, daysLeftInCurrentMonth(LocalDate.of(2026, 9, 30)))
    }

    @Test
    fun formatCompletionDate_formatsMondaySeptember4() {
        val timeZone = TimeZone.getTimeZone("UTC")
        val calendar = Calendar.getInstance(timeZone).apply {
            set(2023, Calendar.SEPTEMBER, 4, 12, 0, 0)
        }
        assertEquals("monday september 4",
            formatCompletionDate(calendar.timeInMillis, timeZone = timeZone)
        )
    }

    @Test
    fun formatCompletionDate_formatsTuesdaySeptember5() {
        val timeZone = TimeZone.getTimeZone("UTC")
        val calendar = Calendar.getInstance(timeZone).apply {
            set(2023, Calendar.SEPTEMBER, 5, 14, 30, 0)
        }
        assertEquals("tuesday september 5",
            formatCompletionDate(calendar.timeInMillis, timeZone = timeZone)
        )
    }
}
