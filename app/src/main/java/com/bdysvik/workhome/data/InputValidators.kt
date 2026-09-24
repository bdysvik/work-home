package com.bdysvik.workhome.data

object InputValidators {
    private val emailRegex = Regex("""^[^@\s]+@[^@\s]+\.[^@\s]+$""")

    fun normalizeEmailKey(email: String): String = email.trim().lowercase()

    fun validateCredentials(email: String, password: String, createAccount: Boolean): String? {
        if (!emailRegex.matches(email.trim())) {
            return "Enter a valid email address."
        }
        if (password.length < 6) {
            return if (createAccount) {
                "Passwords must be at least 6 characters."
            } else {
                "Enter your password."
            }
        }
        return null
    }

    fun parseReward(rewardText: String): Long? = rewardText.trim().toLongOrNull()?.takeIf { it > 0 }

    fun validateChore(description: String, rewardText: String): String? {
        if (description.isBlank()) {
            return "Enter a chore title."
        }
        if (parseReward(rewardText) == null) {
            return "Reward must be a whole number greater than zero."
        }
        return null
    }

    fun validatePendingUser(name: String, email: String): String? {
        if (name.isBlank()) {
            return "Enter a name."
        }
        if (!emailRegex.matches(email.trim())) {
            return "Enter a valid email for the invited user."
        }
        return null
    }
}
