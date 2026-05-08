package com.mh.restaurantchainreservation.feature.profile.subpages.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mh.restaurantchainreservation.core.designsystem.components.DeterministicQrCode

/**
 * Deterministic faux QR matching the React `QRCodeSVG` placeholder pattern in
 * the rest of the app. 25 cell grid with three corner finder patterns and a
 * content area filled by a per-character hash. Used purely as a visual asset.
 */
@Composable
fun QrCanvas(code: String, modifier: Modifier = Modifier) {
    DeterministicQrCode(code = code, modifier = modifier)
}
