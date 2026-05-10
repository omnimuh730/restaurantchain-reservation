package com.mh.restaurantchainreservation.core.designsystem.tokens

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.R

val RestaurantFontFamily = FontFamily(
    Font(R.font.inter_thin, FontWeight.Thin),
    Font(R.font.inter_thinitalic, FontWeight.Thin, FontStyle.Italic),
    Font(R.font.inter_extralight, FontWeight.ExtraLight),
    Font(R.font.inter_extralightitalic, FontWeight.ExtraLight, FontStyle.Italic),
    Font(R.font.inter_light, FontWeight.Light),
    Font(R.font.inter_lightitalic, FontWeight.Light, FontStyle.Italic),
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_mediumitalic, FontWeight.Medium, FontStyle.Italic),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
    Font(R.font.inter_semibolditalic, FontWeight.SemiBold, FontStyle.Italic),
    Font(R.font.inter_bold, FontWeight.Bold),
    Font(R.font.inter_bolditalic, FontWeight.Bold, FontStyle.Italic),
    Font(R.font.inter_extrabold, FontWeight.ExtraBold),
    Font(R.font.inter_extrabolditalic, FontWeight.ExtraBold, FontStyle.Italic),
    Font(R.font.inter_black, FontWeight.Black),
    Font(R.font.inter_blackitalic, FontWeight.Black, FontStyle.Italic),
)

private fun interStyle(
    weight: FontWeight = FontWeight.Normal,
    size: Int,
    lineHeight: Int,
) = TextStyle(
    fontFamily = RestaurantFontFamily,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = lineHeight.sp,
)

val RestaurantTypography = Typography(
    displayLarge = interStyle(FontWeight.Bold, size = 57, lineHeight = 64),
    displayMedium = interStyle(FontWeight.Bold, size = 45, lineHeight = 52),
    displaySmall = interStyle(FontWeight.Bold, size = 36, lineHeight = 44),
    headlineLarge = interStyle(FontWeight.Bold, size = 30, lineHeight = 36),
    headlineMedium = interStyle(FontWeight.Bold, size = 28, lineHeight = 34),
    headlineSmall = interStyle(FontWeight.Bold, size = 24, lineHeight = 30),
    titleLarge = interStyle(FontWeight.Bold, size = 20, lineHeight = 24),
    titleMedium = interStyle(FontWeight.SemiBold, size = 16, lineHeight = 20),
    titleSmall = interStyle(FontWeight.SemiBold, size = 14, lineHeight = 18),
    bodyLarge = interStyle(size = 14, lineHeight = 20),
    bodyMedium = interStyle(size = 13, lineHeight = 18),
    bodySmall = interStyle(size = 12, lineHeight = 16),
    labelLarge = interStyle(FontWeight.Medium, size = 14, lineHeight = 18),
    labelMedium = interStyle(FontWeight.Medium, size = 12, lineHeight = 16),
    labelSmall = interStyle(FontWeight.Medium, size = 11, lineHeight = 14),
)
