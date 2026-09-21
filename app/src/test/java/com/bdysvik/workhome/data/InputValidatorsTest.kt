package com.bdysvik.workhome.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class InputValidatorsTest {
    @Test
    fun parseReward_rejectsBlankOrNonPositiveValues() {
        assertNull(InputValidators.parseReward(""))
        assertNull(InputValidators.parseReward("0"))
        assertNull(InputValidators.parseReward("-1"))
    }

    @Test
    fun parseReward_acceptsPositiveWholeNumbers() {
        assertEquals(25L, InputValidators.parseReward("25"))
    }

    @Test
    fun normalizeEmailKey_trimsAndLowercases() {
        assertEquals("parent@example.com", InputValidators.normalizeEmailKey(" Parent@Example.com "))
    }

    @Test
    fun validatePendingUser_acceptsSimpleInviteData() {
        assertNull(InputValidators.validatePendingUser("Ava", "ava@example.com"))
    }
}
