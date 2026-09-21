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

    @Test
    fun bootstrapProfileData_returnsNullWithoutPendingInvite() {
        assertNull(bootstrapProfileData("uid-123", null))
    }

    @Test
    fun bootstrapProfileData_buildsUserDocumentPayload() {
        val payload = bootstrapProfileData(
            userId = "uid-123",
            pendingUser = PendingUser(
                emailKey = "ava@example.com",
                name = "Ava",
                email = "ava@example.com",
                role = UserRole.ADMIN,
            ),
        )

        assertEquals("Ava", payload?.get("name"))
        assertEquals("ava@example.com", payload?.get("email"))
        assertEquals("admin", payload?.get("role"))
        assertEquals("uid-123", payload?.get("authUid"))
        assertEquals(0L, payload?.get("currentRewardTotal"))
    }
}
