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

        fun fromCode(code: String?): AppLanguage {
            if (code.isNullOrBlank()) return DEFAULT
            val normalized = code.trim().lowercase()
            return when {
                normalized == "no" || normalized == "nb" || normalized == "nn" ||
                    normalized.startsWith("no_") || normalized.startsWith("no-") ||
                    normalized.startsWith("nb_") || normalized.startsWith("nb-") ||
                    normalized.startsWith("nn_") || normalized.startsWith("nn-") -> NORWEGIAN
                normalized == "en" || normalized.startsWith("en_") || normalized.startsWith("en-") -> ENGLISH
                else -> values().firstOrNull { it.code.equals(normalized, ignoreCase = true) } ?: DEFAULT
            }
        }
    }
}
