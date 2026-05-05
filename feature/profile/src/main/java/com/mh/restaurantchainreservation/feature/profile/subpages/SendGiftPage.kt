package com.mh.restaurantchainreservation.feature.profile.subpages

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.i18n.R as I18nR

private val PresetAmounts = listOf(5000, 10000, 30000, 50000, 100000)

@Composable
fun SendGiftPage(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val palette = LocalRestaurantPalette.current
    var recipient by rememberSaveable { mutableStateOf("") }
    var amount by rememberSaveable { mutableStateOf(10000) }
    var message by rememberSaveable { mutableStateOf("") }
    var showPreview by rememberSaveable { mutableStateOf(false) }

    SubpageScaffold(
        title = stringResource(I18nR.string.send_gift_title),
        onBack = onBack,
        modifier = modifier,
    ) {
        SectionLabel(stringResource(I18nR.string.send_gift_recipient))
        OutlinedTextField(
            value = recipient,
            onValueChange = { recipient = it },
            placeholder = { Text(stringResource(I18nR.string.send_gift_recipient_hint)) },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = textFieldColors(),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(20.dp))
        SectionLabel(stringResource(I18nR.string.send_gift_amount))
        AmountGrid(selected = amount, onSelect = { amount = it })

        Spacer(Modifier.height(20.dp))
        SectionLabel(stringResource(I18nR.string.send_gift_message))
        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            placeholder = { Text(stringResource(I18nR.string.send_gift_message_hint)) },
            shape = RoundedCornerShape(14.dp),
            colors = textFieldColors(),
            modifier = Modifier.fillMaxWidth().height(120.dp),
        )

        Spacer(Modifier.height(28.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(if (recipient.isBlank()) palette.brand.copy(alpha = 0.45f) else palette.brand)
                .clickable(enabled = recipient.isNotBlank()) { showPreview = true },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(I18nR.string.send_gift_send),
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.height(40.dp))
    }

    if (showPreview) {
        PreviewDialog(
            recipient = recipient,
            amount = amount,
            message = message,
            onDismiss = { showPreview = false },
            onConfirm = {
                showPreview = false
                onBack()
            },
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    val palette = LocalRestaurantPalette.current
    Text(
        text = text,
        color = palette.foreground,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 10.dp),
    )
}

@Composable
private fun AmountGrid(selected: Int, onSelect: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        PresetAmounts.take(3).forEach { amt ->
            AmountChip(amount = amt, selected = amt == selected, onSelect = { onSelect(amt) }, modifier = Modifier.weight(1f))
        }
    }
    Spacer(Modifier.height(8.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        PresetAmounts.drop(3).forEach { amt ->
            AmountChip(amount = amt, selected = amt == selected, onSelect = { onSelect(amt) }, modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun AmountChip(amount: Int, selected: Boolean, onSelect: () -> Unit, modifier: Modifier = Modifier) {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(14.dp)
    val border = if (selected) palette.brand else palette.border.copy(alpha = 0.6f)
    Box(
        modifier = modifier
            .height(52.dp)
            .clip(shape)
            .border(if (selected) 2.dp else 1.dp, border, shape)
            .background(if (selected) palette.brand.copy(alpha = 0.06f) else palette.cardSurface)
            .clickable(onClick = onSelect),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "₩${"%,d".format(amount)}",
            color = if (selected) palette.brand else palette.foreground,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun PreviewDialog(
    recipient: String,
    amount: Int,
    message: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(I18nR.string.send_gift_preview)) },
        text = {
            Column {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(palette.brand.copy(alpha = 0.10f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CardGiftcard,
                        contentDescription = null,
                        tint = palette.brand,
                        modifier = Modifier.size(28.dp),
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "${stringResource(I18nR.string.send_gift_recipient)}: $recipient",
                    fontSize = 14.sp,
                )
                Text(
                    text = "${stringResource(I18nR.string.send_gift_amount)}: ₩${"%,d".format(amount)}",
                    fontSize = 14.sp,
                )
                if (message.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "“$message”",
                        color = palette.mutedForeground,
                        fontSize = 13.sp,
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(I18nR.string.send_gift_preview_subtitle),
                    color = palette.mutedForeground,
                    fontSize = 12.sp,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(I18nR.string.send_gift_send), color = palette.brand, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(I18nR.string.common_cancel))
            }
        },
    )
}

@Composable
private fun textFieldColors() = TextFieldDefaults.colors(
    focusedIndicatorColor = LocalRestaurantPalette.current.brand,
    unfocusedIndicatorColor = LocalRestaurantPalette.current.border,
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
)
