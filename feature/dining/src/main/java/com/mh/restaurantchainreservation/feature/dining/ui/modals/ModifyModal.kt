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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Chair
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mh.restaurantchainreservation.core.designsystem.components.BottomModalSheet
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.i18n.R as I18nR
import com.mh.restaurantchainreservation.feature.dining.data.Booking

private val timeSlots = listOf("17:30", "18:00", "18:30", "19:00", "19:30", "20:00", "20:30", "21:00")
private val seatingOptions = listOf("Indoor", "Outdoor", "Terrace", "Bar", "Any")

@Composable
fun ModifyModal(
    booking: Booking,
    onDismiss: () -> Unit,
    onSave: (Booking) -> Unit,
) {
    val palette = LocalRestaurantPalette.current

    var guests by remember(booking.id) { mutableIntStateOf(booking.guests) }
    var time by remember(booking.id) { mutableStateOf(booking.time) }
    var seating by remember(booking.id) { mutableStateOf(booking.seating) }
    var note by remember(booking.id) { mutableStateOf(booking.specialRequest ?: "") }

    LaunchedEffect(booking.id) {
        guests = booking.guests
        time = booking.time
        seating = booking.seating
        note = booking.specialRequest ?: ""
    }

    BottomModalSheet(onDismiss = onDismiss) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState()),
        ) {
            // Hero image with gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
            ) {
                AsyncImage(
                    model = booking.image,
                    contentDescription = booking.restaurant,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(140.dp),
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.20f),
                                    Color.Black.copy(alpha = 0.75f),
                                ),
                            ),
                        ),
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 20.dp, end = 20.dp, bottom = 16.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(percent = 50))
                            .background(Color.White.copy(alpha = 0.18f))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp),
                        )
                        Text(
                            text = stringResource(I18nR.string.modify_title_chip),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = booking.restaurant,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                    )
                    Text(
                        text = "${booking.date} · ${booking.time}",
                        color = Color.White.copy(alpha = 0.78f),
                        fontSize = 13.sp,
                        maxLines = 1,
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                // Guests
                Column {
                    ControlLabel(icon = Icons.Outlined.Group, text = stringResource(I18nR.string.modify_label_guests))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        for (n in 1..6) {
                            PillToggle(
                                text = n.toString(),
                                selected = guests == n,
                                onClick = { guests = n },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }

                // Time
                Column {
                    ControlLabel(icon = Icons.Outlined.AccessTime, text = stringResource(I18nR.string.modify_label_time))
                    val rows = timeSlots.chunked(4)
                    rows.forEachIndexed { rowIndex, row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            row.forEach { slot ->
                                PillToggle(
                                    text = slot,
                                    selected = time == slot,
                                    onClick = { time = slot },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                        if (rowIndex < rows.lastIndex) Spacer(Modifier.height(8.dp))
                    }
                }

                // Seating
                Column {
                    ControlLabel(icon = Icons.Outlined.Chair, text = stringResource(I18nR.string.modify_label_seating))
                    FlowRowSpaced {
                        seatingOptions.forEach { option ->
                            PillToggle(
                                text = option,
                                selected = seating == option,
                                onClick = { seating = option },
                                horizontal = 14.dp,
                                vertical = 8.dp,
                            )
                        }
                    }
                }

                // Note
                Column {
                    ControlLabel(icon = Icons.Outlined.ChatBubbleOutline, text = stringResource(I18nR.string.modify_label_note))
                    val noteShape = RoundedCornerShape(20.dp)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(96.dp)
                            .clip(noteShape)
                            .background(palette.mutedSurface.copy(alpha = 0.45f))
                            .border(1.dp, palette.border, noteShape)
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                    ) {
                        BasicTextField(
                            value = note,
                            onValueChange = { note = it },
                            cursorBrush = SolidColor(palette.brand),
                            textStyle = TextStyle(
                                color = palette.foreground,
                                fontSize = 14.sp,
                            ),
                            decorationBox = { inner ->
                                if (note.isEmpty()) {
                                    Text(
                                        text = stringResource(I18nR.string.modify_note_hint),
                                        color = palette.mutedForeground,
                                        fontSize = 14.sp,
                                    )
                                }
                                inner()
                            },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ModifyButton(
                    text = stringResource(I18nR.string.cancel_confirm_cancel),
                    primary = false,
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                )
                ModifyButton(
                    text = stringResource(I18nR.string.modify_save),
                    primary = true,
                    icon = Icons.Filled.Check,
                    onClick = {
                        onSave(
                            booking.copy(
                                guests = guests,
                                time = time,
                                seating = seating,
                                specialRequest = note.trim().ifEmpty { null },
                            )
                        )
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ControlLabel(icon: ImageVector, text: String) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier.padding(bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(palette.brand.copy(alpha = 0.10f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = palette.brand,
                modifier = Modifier.size(14.dp),
            )
        }
        Text(
            text = text,
            color = palette.foreground,
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold,
        )
    }
}

@Composable
private fun PillToggle(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    horizontal: androidx.compose.ui.unit.Dp = 0.dp,
    vertical: androidx.compose.ui.unit.Dp = 0.dp,
) {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(percent = 50)
    val container = if (selected) palette.brand else palette.mutedSurface
    val content = if (selected) Color.White else palette.foreground
    Box(
        modifier = modifier
            .height(40.dp)
            .clip(shape)
            .background(container)
            .clickable(onClick = onClick)
            .padding(horizontal = horizontal, vertical = vertical),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = content,
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FlowRowSpaced(content: @Composable () -> Unit) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        content()
    }
}

@Composable
private fun ModifyButton(
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
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = content,
                modifier = Modifier.size(16.dp),
            )
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text = text,
            color = content,
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
        )
    }
}

