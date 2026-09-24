package com.bdysvik.workhome.ui

import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

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
    fun daysLeftInCurrentMonth_returnsZeroOnLastDayOfMonth() {
        assertEquals(0, daysLeftInCurrentMonth(LocalDate.of(2026, 9, 30)))
    }
}
