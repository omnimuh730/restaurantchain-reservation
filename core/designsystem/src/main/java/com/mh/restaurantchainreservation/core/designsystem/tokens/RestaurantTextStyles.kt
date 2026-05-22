package com.mh.restaurantchainreservation.core.designsystem.tokens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

/**
 * Airbnb-inspired text hierarchy (Cereal → Inter).
 *
 * **Color roles** ([RestaurantTextColor]):
 * - [RestaurantTextColor.Main] — Charcoal `#222222` titles, menu labels, icons
 * - [RestaurantTextColor.Body] — Charcoal `#222222` bold body copy
 * - [RestaurantTextColor.Sub] — Muted `#717171` metadata, subtitles, trailing values
 * - [RestaurantTextColor.Tertiary] — Muted `#717171` placeholders, inactive tabs
 *
 * **Type roles** ([RestaurantTextRole]): regular (400) by default; medium/semibold only for
 * section titles and price emphasis — matching Airbnb menu/settings lists.
 */
@Immutable
enum class RestaurantTextRole {
    /** 28sp / bold — screen titles */
    Display,
    /** 22sp / semibold — "Account settings", section headers */
    SectionTitle,
    /** 20sp / semibold — card titles, subsections */
    Title,
    /** 21sp / bold — prices, strong numerals */
    Price,
    /** 16sp / regular — settings menu rows (Airbnb profile list) */
    MenuRow,
    /** 16sp / medium — rare emphasized inline labels */
    LabelLarge,
    /** 14sp / regular — default UI body */
    Body,
    /** 13sp / regular — compact body */
    BodySmall,
    /** 12sp / regular — captions, metadata (use with [RestaurantTextColor.Sub]) */
    Caption,
    /** 11sp / regular — micro labels, version footer */
    Micro,
}

@Immutable
enum class RestaurantTextColor {
    Main,
    Body,
    Sub,
    Tertiary,
    Brand,
    Destructive,
}

object RestaurantTextStyles {

    fun trackingEm(fontSizeSp: Int, em: Float): TextUnit =
        (fontSizeSp * em).sp

    /** Airbnb: -0.02em at 20sp+, -0.009em at 14–16sp, +0.04em at 11–12sp */
    fun letterSpacingForRole(role: RestaurantTextRole): TextUnit = when (role) {
        RestaurantTextRole.Display,
        RestaurantTextRole.SectionTitle,
        RestaurantTextRole.Title,
        RestaurantTextRole.Price,
        -> trackingEm(
            when (role) {
                RestaurantTextRole.Display -> 28
                RestaurantTextRole.SectionTitle -> 22
                RestaurantTextRole.Title -> 20
                RestaurantTextRole.Price -> 21
                else -> 20
            },
            -0.02f,
        )
        RestaurantTextRole.MenuRow,
        RestaurantTextRole.LabelLarge,
        RestaurantTextRole.Body,
        -> trackingEm(
            when (role) {
                RestaurantTextRole.MenuRow, RestaurantTextRole.LabelLarge -> 16
                else -> 14
            },
            -0.009f,
        )
        RestaurantTextRole.BodySmall -> trackingEm(13, -0.009f)
        RestaurantTextRole.Caption,
        RestaurantTextRole.Micro,
        -> trackingEm(
            when (role) {
                RestaurantTextRole.Caption -> 12
                else -> 11
            },
            0.04f,
        )
    }

    fun styleForRole(role: RestaurantTextRole): TextStyle {
        val spacing = letterSpacingForRole(role)
        return when (role) {
            RestaurantTextRole.Display -> cereal(
                size = 28,
                line = 40,
                weight = FontWeight.Bold,
                spacing = spacing,
            )
            RestaurantTextRole.SectionTitle -> cereal(
                size = 22,
                line = 26,
                weight = FontWeight.SemiBold,
                spacing = spacing,
            )
            RestaurantTextRole.Title -> cereal(
                size = 20,
                line = 24,
                weight = FontWeight.SemiBold,
                spacing = spacing,
            )
            RestaurantTextRole.Price -> cereal(
                size = 21,
                line = 30,
                weight = FontWeight.Bold,
                spacing = spacing,
            )
            RestaurantTextRole.MenuRow -> cereal(
                size = 16,
                line = 20,
                weight = FontWeight.Normal,
                spacing = spacing,
            )
            RestaurantTextRole.LabelLarge -> cereal(
                size = 16,
                line = 20,
                weight = FontWeight.Medium,
                spacing = spacing,
            )
            RestaurantTextRole.Body -> cereal(
                size = 14,
                line = 20,
                weight = FontWeight.Normal,
                spacing = spacing,
            )
            RestaurantTextRole.BodySmall -> cereal(
                size = 13,
                line = 16,
                weight = FontWeight.Normal,
                spacing = spacing,
            )
            RestaurantTextRole.Caption -> cereal(
                size = 12,
                line = 16,
                weight = FontWeight.Normal,
                spacing = spacing,
            )
            RestaurantTextRole.Micro -> cereal(
                size = 11,
                line = 14,
                weight = FontWeight.Normal,
                spacing = spacing,
            )
        }
    }

    @Composable
    fun colorForRole(color: RestaurantTextColor): Color {
        val palette = LocalRestaurantPalette.current
        return when (color) {
            RestaurantTextColor.Main -> palette.foreground
            RestaurantTextColor.Body -> palette.bodyForeground
            RestaurantTextColor.Sub -> palette.mutedForeground
            RestaurantTextColor.Tertiary -> palette.tertiaryForeground
            RestaurantTextColor.Brand -> palette.brand
            RestaurantTextColor.Destructive -> palette.destructive
        }
    }

    @Composable
    fun resolve(role: RestaurantTextRole, color: RestaurantTextColor = RestaurantTextColor.Main): TextStyle =
        styleForRole(role).copy(color = colorForRole(color))

    private fun cereal(
        size: Int,
        line: Int,
        weight: FontWeight,
        spacing: TextUnit,
    ) = TextStyle(
        fontFamily = RestaurantFontFamily,
        fontWeight = weight,
        fontSize = size.sp,
        lineHeight = line.sp,
        letterSpacing = spacing,
    )
}

/** Semantic Material [androidx.compose.material3.Typography] slots mapped to Airbnb scale. */
fun airbnbMaterialTypography(): androidx.compose.material3.Typography {
    fun s(role: RestaurantTextRole) = RestaurantTextStyles.styleForRole(role)
    return androidx.compose.material3.Typography(
        displayLarge = s(RestaurantTextRole.Display),
        displayMedium = s(RestaurantTextRole.Title),
        displaySmall = s(RestaurantTextRole.SectionTitle),
        headlineLarge = s(RestaurantTextRole.SectionTitle),
        headlineMedium = s(RestaurantTextRole.Title),
        headlineSmall = s(RestaurantTextRole.MenuRow),
        titleLarge = s(RestaurantTextRole.LabelLarge),
        titleMedium = s(RestaurantTextRole.Body),
        titleSmall = s(RestaurantTextRole.Body),
        bodyLarge = s(RestaurantTextRole.MenuRow),
        bodyMedium = s(RestaurantTextRole.Body),
        bodySmall = s(RestaurantTextRole.Caption),
        labelLarge = s(RestaurantTextRole.Caption),
        labelMedium = s(RestaurantTextRole.Micro),
        labelSmall = s(RestaurantTextRole.Micro),
    )
}
