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
    fun validateChore_acceptsTemplateTitleAndReward() {
        assertNull(InputValidators.validateChore("Clean kitchen", "15"))
    }

    @Test
    fun validateChore_rejectsBlankTitle() {
        assertEquals(
            "Enter a chore title.",
            InputValidators.validateChore("", "15"),
        )
    }

    @Test
    fun validateChore_rejectsInvalidReward() {
        assertEquals(
            "Reward must be a whole number greater than zero.",
            InputValidators.validateChore("Clean kitchen", "0"),
        )
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
    fun validateCredentials_rejectsInvalidEmail() {
        assertEquals(
            "Enter a valid email address.",
            InputValidators.validateCredentials("not-an-email", "hunter2", createAccount = false),
        )
    }

    @Test
    fun validatePendingUser_rejectsInvalidEmail() {
        assertEquals(
            "Enter a valid email for the invited user.",
            InputValidators.validatePendingUser("Ava", "not-an-email"),
        )
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
        assertEquals("", payload?.get("lastCompletionId"))
    }

    @Test
    fun choreData_buildsActiveChorePayloadForTemplateActivation() {
        val payload = choreData(
            title = " Clean kitchen ",
            reward = 20L,
            createdBy = "admin-1",
        )

        assertEquals("Clean kitchen", payload["title"])
        assertEquals(20L, payload["reward"])
        assertEquals("admin-1", payload["createdBy"])
        assertEquals(true, payload["active"])
    }

    @Test
    fun choreTemplateData_buildsTemplatePayload() {
        val payload = choreTemplateData(
            title = " Clean kitchen ",
            reward = 20L,
            createdBy = "admin-1",
        )

        assertEquals("Clean kitchen", payload["title"])
        assertEquals(20L, payload["reward"])
        assertEquals("admin-1", payload["createdBy"])
        assertEquals(true, payload.containsKey("updatedAt"))
    }

    @Test
    fun choreTitle_fallsBackToLegacyDescription() {
        assertEquals("Legacy title", choreTitle(null, " Legacy title "))
    }

    @Test
    fun choreTitle_prefersNewTitleWhenBothFieldsExist() {
        assertEquals("New title", choreTitle(" New title ", "Old description"))
    }

    @Test
    fun choreTitle_fallsBackWhenTitleIsBlank() {
        assertEquals("Legacy title", choreTitle("   ", " Legacy title "))
    }
}
