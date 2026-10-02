package com.bdysvik.workhome.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bdysvik.workhome.localization.AppLanguage
import com.bdysvik.workhome.ui.theme.WorkHomeColors
import com.bdysvik.workhome.ui.theme.WorkHomeShapes

@Composable
fun FlagIcon(
    language: AppLanguage,
    modifier: Modifier = Modifier,
    width: Dp = 22.dp,
    height: Dp = 15.dp,
) {
    val cornerRadius = 3.dp
    val description = when (language) {
        AppLanguage.ENGLISH -> "English flag"
        AppLanguage.NORWEGIAN -> "Norwegian flag"
    }

    Box(
        modifier = modifier
            .size(width = width, height = height)
            .clip(RoundedCornerShape(cornerRadius))
            .border(
                border = BorderStroke(0.5.dp, Color(0x33FFFFFF)),
                shape = RoundedCornerShape(cornerRadius),
            )
            .semantics { contentDescription = description },
    ) {
        when (language) {
            AppLanguage.NORWEGIAN -> NorwegianFlagCanvas(modifier = Modifier.matchParentSize())
            AppLanguage.ENGLISH -> EnglishFlagCanvas(modifier = Modifier.matchParentSize())
        }
    }
}

@Composable
private fun NorwegianFlagCanvas(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Red field (Norwegian flag standard red)
        drawRect(color = Color(0xFFBA0C2F))

        // White cross
        // Vertical arm (centered at 8/22, width 4/22)
        drawRect(
            color = Color.White,
            topLeft = Offset(x = w * (6f / 22f), y = 0f),
            size = Size(width = w * (4f / 22f), height = h),
        )
        // Horizontal arm (centered at 8/16, height 4/16)
        drawRect(
            color = Color.White,
            topLeft = Offset(x = 0f, y = h * (6f / 16f)),
            size = Size(width = w, height = h * (4f / 16f)),
        )

        // Dark blue cross (Norwegian flag standard dark blue)
        // Vertical arm (centered at 8/22, width 2/22)
        drawRect(
            color = Color(0xFF00205B),
            topLeft = Offset(x = w * (7f / 22f), y = 0f),
            size = Size(width = w * (2f / 22f), height = h),
        )
        // Horizontal arm (centered at 8/16, height 2/16)
        drawRect(
            color = Color(0xFF00205B),
            topLeft = Offset(x = 0f, y = h * (7f / 16f)),
            size = Size(width = w, height = h * (2f / 16f)),
        )
    }
}

@Composable
private fun EnglishFlagCanvas(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Deep blue field
        drawRect(color = Color(0xFF012169))

        // White diagonal cross
        drawLine(
            color = Color.White,
            start = Offset(0f, 0f),
            end = Offset(w, h),
            strokeWidth = h * 0.28f,
        )
        drawLine(
            color = Color.White,
            start = Offset(0f, h),
            end = Offset(w, 0f),
            strokeWidth = h * 0.28f,
        )

        // Red diagonal cross
        drawLine(
            color = Color(0xFFC8102E),
            start = Offset(0f, 0f),
            end = Offset(w, h),
            strokeWidth = h * 0.12f,
        )
        drawLine(
            color = Color(0xFFC8102E),
            start = Offset(0f, h),
            end = Offset(w, 0f),
            strokeWidth = h * 0.12f,
        )

        // White central cross
        drawRect(
            color = Color.White,
            topLeft = Offset(x = 0f, y = h * 0.35f),
            size = Size(width = w, height = h * 0.30f),
        )
        drawRect(
            color = Color.White,
            topLeft = Offset(x = w * 0.38f, y = 0f),
            size = Size(width = w * 0.24f, height = h),
        )

        // Red central cross
        drawRect(
            color = Color(0xFFC8102E),
            topLeft = Offset(x = 0f, y = h * 0.41f),
            size = Size(width = w, height = h * 0.18f),
        )
        drawRect(
            color = Color(0xFFC8102E),
            topLeft = Offset(x = w * 0.43f, y = 0f),
            size = Size(width = w * 0.14f, height = h),
        )
    }
}

@Composable
fun LanguageSelector(
    currentLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Surface(
            onClick = { expanded = true },
            shape = WorkHomeShapes.PillShape,
            color = WorkHomeColors.CardBackground.copy(alpha = 0.9f),
            border = BorderStroke(1.dp, WorkHomeColors.CardBorder),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                FlagIcon(language = currentLanguage)
                Text(
                    text = currentLanguage.displayName,
                    style = MaterialTheme.typography.labelMedium,
                    color = WorkHomeColors.PrimaryText,
                    fontWeight = FontWeight.SemiBold,
                )
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "Select language",
                    tint = WorkHomeColors.SecondaryText,
                    modifier = Modifier.size(18.dp),
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(WorkHomeColors.CardBackground),
        ) {
            AppLanguage.values().forEach { language ->
                val isSelected = language == currentLanguage
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            FlagIcon(language = language)
                            Text(
                                text = language.displayName,
                                color = if (isSelected) WorkHomeColors.CyanAccent else WorkHomeColors.PrimaryText,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            )
                        }
                    },
                    trailingIcon = if (isSelected) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                tint = WorkHomeColors.CyanAccent,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    } else null,
                    onClick = {
                        onLanguageSelected(language)
                        expanded = false
                    },
                )
            }
        }
    }
}
