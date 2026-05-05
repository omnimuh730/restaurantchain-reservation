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
import androidx.compose.material.icons.outlined.AlternateEmail
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Schedule
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.i18n.R as I18nR

@Composable
fun ContactSupportPage(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val palette = LocalRestaurantPalette.current
    var subject by rememberSaveable { mutableStateOf("") }
    var message by rememberSaveable { mutableStateOf("") }
    var showThanks by rememberSaveable { mutableStateOf(false) }

    SubpageScaffold(
        title = stringResource(I18nR.string.contact_title),
        onBack = onBack,
        modifier = modifier,
    ) {
        ContactRow(
            icon = Icons.Outlined.AlternateEmail,
            label = stringResource(I18nR.string.contact_email),
            value = stringResource(I18nR.string.contact_email_value),
        )
        Spacer(Modifier.height(8.dp))
        ContactRow(
            icon = Icons.Outlined.Call,
            label = stringResource(I18nR.string.contact_call),
            value = stringResource(I18nR.string.contact_call_value),
        )
        Spacer(Modifier.height(8.dp))
        ContactRow(
            icon = Icons.Outlined.ChatBubbleOutline,
            label = stringResource(I18nR.string.contact_chat),
            value = stringResource(I18nR.string.contact_chat_value),
        )

        Spacer(Modifier.height(20.dp))
        HoursCard()

        Spacer(Modifier.height(24.dp))
        Text(stringResource(I18nR.string.contact_subject), color = palette.foreground, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = subject,
            onValueChange = { subject = it },
            placeholder = { Text(stringResource(I18nR.string.contact_subject_hint)) },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = textFieldColorsForContact(),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(16.dp))
        Text(stringResource(I18nR.string.contact_message), color = palette.foreground, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            placeholder = { Text(stringResource(I18nR.string.contact_message_hint)) },
            shape = RoundedCornerShape(14.dp),
            colors = textFieldColorsForContact(),
            modifier = Modifier.fillMaxWidth().height(140.dp),
        )

        Spacer(Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(if (subject.isBlank()) palette.brand.copy(alpha = 0.4f) else palette.brand)
                .clickable(enabled = subject.isNotBlank()) { showThanks = true },
            contentAlignment = Alignment.Center,
        ) {
            Text(stringResource(I18nR.string.contact_send), color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(40.dp))
    }

    if (showThanks) {
        AlertDialog(
            onDismissRequest = { showThanks = false; onBack() },
            title = { Text(stringResource(I18nR.string.contact_thanks_title)) },
            text = { Text(stringResource(I18nR.string.contact_thanks_body), color = palette.mutedForeground) },
            confirmButton = {
                TextButton(onClick = {
                    showThanks = false
                    onBack()
                }) {
                    Text(stringResource(I18nR.string.common_continue), color = palette.brand, fontWeight = FontWeight.Bold)
                }
            },
        )
    }
}

@Composable
private fun ContactRow(icon: ImageVector, label: String, value: String) {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(16.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.dp, palette.border.copy(alpha = 0.6f), shape)
            .background(palette.cardSurface)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier.size(40.dp).clip(CircleShape).background(palette.brand.copy(alpha = 0.10f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = palette.brand, modifier = Modifier.size(20.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(label, color = palette.foreground, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text(value, color = palette.mutedForeground, fontSize = 12.sp)
        }
        Icon(Icons.Outlined.Check, null, tint = Color.Transparent, modifier = Modifier.size(0.dp))
    }
}

@Composable
private fun HoursCard() {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(14.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(palette.brandSoftSurface)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(Icons.Outlined.Schedule, null, tint = palette.brand, modifier = Modifier.size(20.dp))
        Column {
            Text(stringResource(I18nR.string.contact_hours_label), color = palette.foreground, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text(stringResource(I18nR.string.contact_hours_value), color = palette.mutedForeground, fontSize = 12.sp)
        }
    }
}

@Composable
private fun textFieldColorsForContact() = TextFieldDefaults.colors(
    focusedIndicatorColor = LocalRestaurantPalette.current.brand,
    unfocusedIndicatorColor = LocalRestaurantPalette.current.border,
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
)
