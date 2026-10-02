package com.bdysvik.workhome.localization

enum class AppLanguage(
    val code: String,
    val displayName: String,
    val flagEmoji: String,
) {
    ENGLISH("en", "English", "🇬🇧"),
    NORWEGIAN("no", "Norsk", "🇳🇴");

    companion object {
        val DEFAULT = ENGLISH

        fun fromCode(code: String?): AppLanguage =
            values().firstOrNull { it.code.equals(code, ignoreCase = true) } ?: DEFAULT
    }
}
