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
    fun daysLeftInCurrentMonth_returnsZeroOnLastDayOfMonth() {
        assertEquals(0, daysLeftInCurrentMonth(LocalDate.of(2026, 9, 30)))
    }
}
