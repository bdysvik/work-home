package com.bdysvik.workhome.data

import org.junit.Assert.assertEquals
import org.junit.Test

class AppUserTest {

    private fun createUser(
        currentRewardTotal: Long,
        rewardGoal: Long?,
        rewardEligible: Long? = null,
    ): AppUser {
        return AppUser(
            id = "user1",
            name = "UserA",
            email = "usera@example.com",
            role = UserRole.MEMBER,
            authUid = "uid1",
            currentRewardTotal = currentRewardTotal,
            rewardGoal = rewardGoal,
            rewardEligible = rewardEligible,
        )
    }

    @Test
    fun rewardProgressText_goal500With50Reward_showsTenPercent() {
        val user = createUser(currentRewardTotal = 50, rewardGoal = 500)
        assertEquals("50 / 500 (10%)", user.rewardProgressText())
    }

    @Test
    fun rewardProgressText_goal100With20Reward_showsTwentyPercent() {
        val user = createUser(currentRewardTotal = 20, rewardGoal = 100)
        assertEquals("20 / 100 (20%)", user.rewardProgressText())
    }

    @Test
    fun rewardProgressText_goal100WithZeroReward_showsZeroPercent() {
        val user = createUser(currentRewardTotal = 0, rewardGoal = 100)
        assertEquals("0 / 100 (0%)", user.rewardProgressText())
    }

    @Test
    fun rewardProgressText_goalExceeded_showsOverHundredPercent() {
        val user = createUser(currentRewardTotal = 150, rewardGoal = 100)
        assertEquals("150 / 100 (150%)", user.rewardProgressText())
    }

    @Test
    fun rewardProgressText_roundingBehavior() {
        // 1 / 3 = 33.333% -> 33%
        val user1 = createUser(currentRewardTotal = 1, rewardGoal = 3)
        assertEquals("1 / 3 (33%)", user1.rewardProgressText())

        // 2 / 3 = 66.666% -> 67%
        val user2 = createUser(currentRewardTotal = 2, rewardGoal = 3)
        assertEquals("2 / 3 (67%)", user2.rewardProgressText())
    }

    @Test
    fun rewardProgressText_nullGoal_showsTotalOnly() {
        val user = createUser(currentRewardTotal = 50, rewardGoal = null)
        assertEquals("50", user.rewardProgressText())
    }

    @Test
    fun rewardProgressText_zeroOrNegativeGoal_showsTotalOnly() {
        val userZero = createUser(currentRewardTotal = 50, rewardGoal = 0)
        assertEquals("50", userZero.rewardProgressText())

        val userNegative = createUser(currentRewardTotal = 50, rewardGoal = -10)
        assertEquals("50", userNegative.rewardProgressText())
    }

    @Test
    fun accumulatedReward_eligible200AndTenPercent_returns20() {
        val user = createUser(currentRewardTotal = 20, rewardGoal = 200, rewardEligible = 200)
        assertEquals(20L, user.accumulatedReward())
    }

    @Test
    fun accumulatedReward_eligible500AndTenPercent_returns50() {
        val user = createUser(currentRewardTotal = 50, rewardGoal = 500, rewardEligible = 500)
        assertEquals(50L, user.accumulatedReward())
    }

    @Test
    fun accumulatedReward_roundsUpToNextInteger() {
        // 10 / 300 = 0.033333..., 200 * 0.033333... = 6.666..., ceil -> 7
        val user = createUser(currentRewardTotal = 10, rewardGoal = 300, rewardEligible = 200)
        assertEquals(7L, user.accumulatedReward())
    }

    @Test
    fun accumulatedReward_nullOrZeroInputs_returnsNull() {
        val noGoal = createUser(currentRewardTotal = 50, rewardGoal = null, rewardEligible = 200)
        assertEquals(null, noGoal.accumulatedReward())

        val noEligible = createUser(currentRewardTotal = 50, rewardGoal = 100, rewardEligible = null)
        assertEquals(null, noEligible.accumulatedReward())

        val zeroGoal = createUser(currentRewardTotal = 50, rewardGoal = 0, rewardEligible = 200)
        assertEquals(null, zeroGoal.accumulatedReward())

        val zeroEligible = createUser(currentRewardTotal = 50, rewardGoal = 100, rewardEligible = 0)
        assertEquals(null, zeroEligible.accumulatedReward())
    }
}
