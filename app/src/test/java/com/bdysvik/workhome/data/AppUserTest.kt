package com.bdysvik.workhome.data

import org.junit.Assert.assertEquals
import org.junit.Test

class AppUserTest {

    private fun createUser(currentRewardTotal: Long, rewardGoal: Long?): AppUser {
        return AppUser(
            id = "user1",
            name = "UserA",
            email = "usera@example.com",
            role = UserRole.MEMBER,
            authUid = "uid1",
            currentRewardTotal = currentRewardTotal,
            rewardGoal = rewardGoal,
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
}
