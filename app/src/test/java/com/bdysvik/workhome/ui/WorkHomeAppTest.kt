package com.bdysvik.workhome.ui

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

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
}
