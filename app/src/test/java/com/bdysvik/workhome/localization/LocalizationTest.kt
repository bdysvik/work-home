package com.bdysvik.workhome.localization

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LocalizationTest {

    @Test
    fun appLanguage_fromCode_resolvesEnglish() {
        assertEquals(AppLanguage.ENGLISH, AppLanguage.fromCode("en"))
        assertEquals(AppLanguage.ENGLISH, AppLanguage.fromCode("EN"))
        assertEquals(AppLanguage.ENGLISH, AppLanguage.fromCode("en_US"))
    }

    @Test
    fun appLanguage_fromCode_resolvesNorwegian() {
        assertEquals(AppLanguage.NORWEGIAN, AppLanguage.fromCode("no"))
        assertEquals(AppLanguage.NORWEGIAN, AppLanguage.fromCode("NO"))
        assertEquals(AppLanguage.NORWEGIAN, AppLanguage.fromCode("nb"))
        assertEquals(AppLanguage.NORWEGIAN, AppLanguage.fromCode("nn"))
        assertEquals(AppLanguage.NORWEGIAN, AppLanguage.fromCode("nb_NO"))
    }

    @Test
    fun appLanguage_fromCode_defaultsToEnglishForUnknownOrNull() {
        assertEquals(AppLanguage.ENGLISH, AppLanguage.fromCode(null))
        assertEquals(AppLanguage.ENGLISH, AppLanguage.fromCode(""))
        assertEquals(AppLanguage.ENGLISH, AppLanguage.fromCode("fr"))
        assertEquals(AppLanguage.ENGLISH, AppLanguage.fromCode("de"))
    }

    @Test
    fun appStrings_forLanguage_returnsCorrectImplementation() {
        assertTrue(AppStrings.forLanguage(AppLanguage.ENGLISH) is EnglishStrings)
        assertTrue(AppStrings.forLanguage(AppLanguage.NORWEGIAN) is NorwegianStrings)
        assertTrue(getStrings(AppLanguage.ENGLISH) is EnglishStrings)
        assertTrue(getStrings(AppLanguage.NORWEGIAN) is NorwegianStrings)
    }

    @Test
    fun englishStrings_containsExpectedTranslations() {
        val strings = EnglishStrings
        assertEquals("WorkHome", strings.appName)
        assertEquals("Sign in", strings.signIn)
        assertEquals("Create invited account", strings.createInvitedAccount)
        assertEquals("Email", strings.email)
        assertEquals("Password", strings.password)
        assertEquals("Chores", strings.navChores)
        assertEquals("Templates", strings.navTemplates)
        assertEquals("Minutes", strings.navMinutes)
        assertEquals("Users", strings.navUsers)
        assertEquals("Sign out", strings.signOut)
        assertEquals("Active chores", strings.activeChores)
        assertEquals("Delete", strings.delete)
        assertEquals("Cancel", strings.cancel)
        assertEquals("Confirm", strings.confirm)
        assertEquals("Save", strings.save)
        assertEquals("Creating...", strings.creating)
        assertEquals("Every 4 days", strings.recurrenceEveryNDays(4))
        assertEquals("Total reward eligible", strings.totalRewardEligible)
        assertEquals("Accumulated reward: 20 / 200", strings.accumulatedReward(20, 200))
    }

    @Test
    fun norwegianStrings_containsExpectedTranslations() {
        val strings = NorwegianStrings
        assertEquals("WorkHome", strings.appName)
        assertEquals("Logg inn", strings.signIn)
        assertEquals("Opprett invitert konto", strings.createInvitedAccount)
        assertEquals("E-post", strings.email)
        assertEquals("Passord", strings.password)
        assertEquals("Gjøremål", strings.navChores)
        assertEquals("Maler", strings.navTemplates)
        assertEquals("Minutter", strings.navMinutes)
        assertEquals("Brukere", strings.navUsers)
        assertEquals("Logg ut", strings.signOut)
        assertEquals("Aktive gjøremål", strings.activeChores)
        assertEquals("Slett", strings.delete)
        assertEquals("Avbryt", strings.cancel)
        assertEquals("Bekreft", strings.confirm)
        assertEquals("Lagre", strings.save)
        assertEquals("Oppretter...", strings.creating)
        assertEquals("Hver 4. dag", strings.recurrenceEveryNDays(4))
        assertEquals("Maksimal belønning tilgjengelig", strings.totalRewardEligible)
        assertEquals("Opptjent belønning: 20 / 200", strings.accumulatedReward(20, 200))
    }

    @Test
    fun allStrings_areNonEmpty() {
        listOf(EnglishStrings, NorwegianStrings).forEach { strings ->
            assertNotNull(strings.appName)
            assertTrue(strings.appName.isNotBlank())
            assertTrue(strings.signIn.isNotBlank())
            assertTrue(strings.createInvitedAccount.isNotBlank())
            assertTrue(strings.email.isNotBlank())
            assertTrue(strings.password.isNotBlank())
            assertTrue(strings.navChores.isNotBlank())
            assertTrue(strings.navTemplates.isNotBlank())
            assertTrue(strings.navMinutes.isNotBlank())
            assertTrue(strings.navUsers.isNotBlank())
            assertTrue(strings.signOut.isNotBlank())
            assertTrue(strings.selectLanguage.isNotBlank())
            assertTrue(strings.english.isNotBlank())
            assertTrue(strings.norwegian.isNotBlank())
            assertTrue(strings.cancel.isNotBlank())
            assertTrue(strings.confirm.isNotBlank())
            assertTrue(strings.save.isNotBlank())
            assertTrue(strings.delete.isNotBlank())
            assertTrue(strings.creating.isNotBlank())
            assertTrue(strings.setGoalTitle.isNotBlank())
            assertTrue(strings.targetMinutesGoal.isNotBlank())
            assertTrue(strings.totalRewardEligible.isNotBlank())
            assertTrue(strings.rewardEligiblePlaceholder.isNotBlank())
            assertTrue(strings.accumulatedReward(10, 100).isNotBlank())
            assertTrue(strings.saveTemplateHeader.isNotBlank())
            assertTrue(strings.autoCreationSchedule.isNotBlank())
            assertTrue(strings.inviteFamilyMember.isNotBlank())
            assertTrue(strings.adminControls.isNotBlank())
            assertTrue(strings.resetDescription.isNotBlank())
        }
    }
}
