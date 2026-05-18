package com.mh.restaurantchainreservation.feature.dining.ui.modals

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.components.BottomModalSheet
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.i18n.R as I18nR
import com.mh.restaurantchainreservation.feature.dining.data.Booking
import com.mh.restaurantchainreservation.feature.dining.data.BookingStatus
import com.mh.restaurantchainreservation.feature.dining.ui.BookingCard

@Composable
fun AddBookingCodeModal(
    bookings: List<Booking>,
    checkedInIds: Set<String>,
    onDismiss: () -> Unit,
    onAdded: (Booking) -> Unit,
    onView: (Booking) -> Unit,
) {
    val palette = LocalRestaurantPalette.current

    var code by remember { mutableStateOf("") }
    var candidate by remember { mutableStateOf<Booking?>(null) }
    var added by remember { mutableStateOf<Booking?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var done by remember { mutableStateOf(false) }

    val notFoundMsg = stringResource(I18nR.string.add_code_error_not_found)
    val notActiveMsg = stringResource(I18nR.string.add_code_error_not_active)

    fun verify() {
        val normalized = code.trim().uppercase()
        val match = bookings.firstOrNull { it.confirmationNo.equals(normalized, ignoreCase = true) }
        if (match == null) {
            error = notFoundMsg
            candidate = null
            added = null
            return
        }
        if (match.status != BookingStatus.Confirmed) {
            error = notActiveMsg
            candidate = null
            added = null
            return
        }
        val existingInvite = bookings.firstOrNull {
            it.confirmationNo.equals("${match.confirmationNo}-G", ignoreCase = true)
        }
        val verifiedBooking = if (match.confirmationNo.endsWith("-G")) match
        else existingInvite ?: match.copy(
            id = "invite-${match.id}-${System.currentTimeMillis()}",
            confirmationNo = "${match.confirmationNo}-G",
            specialRequest = "Shared invitation from ${match.confirmationNo}",
        )
        error = null
        candidate = match
        added = verifiedBooking
        if (existingInvite == null && !match.confirmationNo.endsWith("-G")) onAdded(verifiedBooking)
        done = true
    }

    BottomModalSheet(onDismiss = onDismiss) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(0.dp, Color.Transparent)
                    .padding(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 16.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(palette.brand.copy(alpha = 0.10f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = if (done) Icons.Filled.CheckCircle else Icons.Outlined.ConfirmationNumber,
                            contentDescription = null,
                            tint = palette.brand,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                    Column {
                        Text(
                            text = stringResource(if (done) I18nR.string.add_code_done_title else I18nR.string.add_code_title),
                            color = palette.foreground,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                        )
                        Text(
                            text = stringResource(if (done) I18nR.string.add_code_done_subtitle else I18nR.string.add_code_subtitle),
                            color = palette.mutedForeground,
                            fontSize = 13.sp,
                        )
                    }
                }
            }
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(palette.border))

            if (done && added != null) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    VerifiedBanner(candidateCode = candidate?.confirmationNo ?: "")
                    BookingCard(
                        booking = added!!,
                        onTap = { onView(added!!) },
                        checkedInIds = checkedInIds,
                        onManage = { onView(added!!) },
                        onScanQR = { onView(added!!) },
                        onShowQR = { onView(added!!) },
                        onInvite = { onView(added!!) },
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        SmallChipButton(
                            text = stringResource(I18nR.string.add_code_done),
                            primary = false,
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                        )
                        SmallChipButton(
                            text = stringResource(I18nR.string.add_code_view),
                            primary = true,
                            onClick = { onView(added!!) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    Column {
                        Text(
                            text = stringResource(I18nR.string.add_code_label),
                            color = palette.mutedForeground,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp,
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            val inputShape = RoundedCornerShape(16.dp)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .clip(inputShape)
                                    .background(palette.mutedSurface.copy(alpha = 0.5f))
                                    .border(1.dp, palette.border, inputShape)
                                    .padding(horizontal = 16.dp),
                                contentAlignment = Alignment.CenterStart,
                            ) {
                                BasicTextField(
                                    value = code,
                                    onValueChange = {
                                        code = it
                                        error = null
                                        candidate = null
                                        added = null
                                    },
                                    cursorBrush = SolidColor(palette.brand),
                                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                                    textStyle = TextStyle(
                                        color = palette.foreground,
                                        fontSize = 15.sp,
                                    ),
                                    decorationBox = { inner ->
                                        if (code.isEmpty()) {
                                            Text(
                                                text = stringResource(I18nR.string.add_code_placeholder),
                                                color = palette.mutedForeground,
                                                fontSize = 15.sp,
                                            )
                                        }
                                        inner()
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                            SmallChipButton(
                                text = stringResource(I18nR.string.add_code_verify),
                                primary = true,
                                onClick = ::verify,
                                modifier = Modifier.width(96.dp),
                            )
                        }
                        if (error != null) {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = error!!,
                                color = palette.destructive,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }

                    if (candidate != null) {
                        VerifiedCandidateCard(booking = candidate!!)
                    }
                }
            }
        }
    }
}

@Composable
private fun VerifiedBanner(candidateCode: String) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(palette.brand.copy(alpha = 0.06f))
            .border(1.dp, palette.brand.copy(alpha = 0.20f), RoundedCornerShape(20.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = Icons.Outlined.Shield,
            contentDescription = null,
            tint = palette.brand,
            modifier = Modifier.size(18.dp),
        )
        Column {
            Text(
                text = stringResource(I18nR.string.add_code_verified_title),
                color = palette.brand,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                text = stringResource(I18nR.string.add_code_verified_subtitle, candidateCode),
                color = palette.mutedForeground,
                fontSize = 12.sp,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun VerifiedCandidateCard(booking: Booking) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(palette.brand.copy(alpha = 0.06f))
            .border(1.dp, palette.brand.copy(alpha = 0.20f), RoundedCornerShape(20.dp))
            .padding(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(
                imageVector = Icons.Outlined.Shield,
                contentDescription = null,
                tint = palette.brand,
                modifier = Modifier.size(16.dp),
            )
            Text(
                text = stringResource(I18nR.string.add_code_verified_title),
                color = palette.brand,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = booking.restaurant,
            color = palette.foreground,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
        )
        Text(
            text = "${booking.date} · ${booking.time} · ${booking.seating}",
            color = palette.mutedForeground,
            fontSize = 13.sp,
            maxLines = 1,
        )
    }
}

@Composable
private fun SmallChipButton(
    text: String,
    primary: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
) {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(percent = 50)
    val container = if (primary) palette.brand else palette.cardSurface
    val content = if (primary) Color.White else palette.foreground
    Row(
        modifier = modifier
            .height(48.dp)
            .clip(shape)
            .let { if (!primary) it.border(1.dp, palette.border, shape) else it }
            .background(container)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null, tint = content, modifier = Modifier.size(16.dp))
            Spacer(Modifier.size(8.dp))
        }
        Text(
            text = text,
            color = content,
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
        )
    }
}
