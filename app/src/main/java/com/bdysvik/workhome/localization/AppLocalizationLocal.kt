package com.bdysvik.workhome.localization

import androidx.compose.runtime.compositionLocalOf

val LocalAppLanguage = compositionLocalOf { AppLanguage.DEFAULT }
val LocalAppStrings = compositionLocalOf { EnglishStrings as AppStrings }
