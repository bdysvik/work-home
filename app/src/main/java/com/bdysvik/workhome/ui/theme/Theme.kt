package com.bdysvik.workhome.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object WorkHomeColors {
    val Background = Color(0xFF030817)
    val BackgroundElevated = Color(0xFF061126)
    val CardBackground = Color(0xFF08162B)
    val CardBackgroundEnd = Color(0xFF0D1D38)
    val PrimaryBlue = Color(0xFF2979FF)
    val CyanAccent = Color(0xFF18D7FF)
    val PurpleAccent = Color(0xFF8B3DFF)
    val MagentaAccent = Color(0xFFC43CFF)
    val PrimaryText = Color(0xFFF5F7FF)
    val SecondaryText = Color(0xFFAAB4CC)
    val RewardStar = Color(0xFFFFC542)
    val CardBorder = Color(0x3318D7FF)
    val CardBorderBlue = Color(0x332979FF)
    val GlowBlue = Color(0x262979FF)

    val CardGradient = Brush.linearGradient(
        colors = listOf(CardBackground, CardBackgroundEnd)
    )

    val CardBorderBrush = Brush.linearGradient(
        colors = listOf(CardBorder, CardBorderBlue)
    )

    val CompleteButtonGradient = Brush.horizontalGradient(
        colors = listOf(PurpleAccent, PrimaryBlue, CyanAccent)
    )

    val NavBorderBrush = Brush.horizontalGradient(
        colors = listOf(CardBorderBlue, CardBorder, PurpleAccent.copy(alpha = 0.2f))
    )
}

object WorkHomeDimens {
    val CardCornerRadius = 20.dp
    val CardPadding = 14.dp
    val SpacingBetweenCards = 12.dp
    val ScreenHorizontalPadding = 16.dp
}

object WorkHomeShapes {
    val CardShape = RoundedCornerShape(WorkHomeDimens.CardCornerRadius)
    val PillShape = RoundedCornerShape(25.dp)
    val BadgeShape = RoundedCornerShape(12.dp)
    val IconContainerShape = RoundedCornerShape(12.dp)
    val BottomNavShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
}

private val FuturisticDarkColorScheme = darkColorScheme(
    primary = WorkHomeColors.PrimaryBlue,
    onPrimary = WorkHomeColors.PrimaryText,
    primaryContainer = Color(0xFF142850),
    onPrimaryContainer = WorkHomeColors.PrimaryText,
    secondary = WorkHomeColors.CyanAccent,
    onSecondary = Color(0xFF030817),
    secondaryContainer = Color(0xFF0C3854),
    onSecondaryContainer = WorkHomeColors.CyanAccent,
    tertiary = WorkHomeColors.PurpleAccent,
    onTertiary = WorkHomeColors.PrimaryText,
    background = WorkHomeColors.Background,
    onBackground = WorkHomeColors.PrimaryText,
    surface = WorkHomeColors.BackgroundElevated,
    onSurface = WorkHomeColors.PrimaryText,
    surfaceVariant = WorkHomeColors.CardBackgroundEnd,
    onSurfaceVariant = WorkHomeColors.SecondaryText,
    outline = WorkHomeColors.CardBorder,
    outlineVariant = WorkHomeColors.CardBorderBlue,
)

@Composable
fun WorkHomeTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = FuturisticDarkColorScheme,
        content = content,
    )
}
