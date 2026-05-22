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
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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

@Composable
fun ProfileEditPage(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val palette = LocalRestaurantPalette.current
    var name by rememberSaveable { mutableStateOf("Alex Chen") }
    var username by rememberSaveable { mutableStateOf("alexchen") }
    var phone by rememberSaveable { mutableStateOf("+82 10 1234 5678") }

    SubpageScaffold(
        title = stringResource(I18nR.string.profile_edit_title),
        onBack = onBack,
        modifier = modifier,
    ) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(96.dp)) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(palette.mutedSurface)
                        .border(2.dp, palette.cardSurface, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = name.firstOrNull()?.uppercase() ?: "A",
                        color = palette.foreground,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(palette.brand)
                        .border(2.dp, palette.cardSurface, CircleShape)
                        .clickable { /* avatar picker hookup later */ },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Outlined.PhotoCamera, null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(I18nR.string.profile_edit_change_photo),
                color = palette.brand,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { /* picker */ },
            )
        }

        Spacer(Modifier.height(24.dp))
        EditField(label = stringResource(I18nR.string.profile_edit_full_name), value = name, onValueChange = { name = it })
        Spacer(Modifier.height(12.dp))
        EditField(label = stringResource(I18nR.string.profile_edit_username), value = username, onValueChange = { username = it })
        Spacer(Modifier.height(12.dp))
        EditField(label = stringResource(I18nR.string.profile_edit_phone), value = phone, onValueChange = { phone = it })

        Spacer(Modifier.height(24.dp))
        TierBenefitsPanel()

        Spacer(Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(palette.brand)
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(I18nR.string.profile_edit_save_changes),
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun EditField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = LocalRestaurantPalette.current.brand,
            unfocusedIndicatorColor = LocalRestaurantPalette.current.border,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
        ),
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun TierBenefitsPanel() {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.dp, palette.border, shape)
            .background(palette.cardSurface)
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier.size(28.dp).clip(CircleShape).background(palette.gold.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.WorkspacePremium, null, tint = palette.gold, modifier = Modifier.size(16.dp))
            }
            Text(
                text = stringResource(I18nR.string.profile_edit_tier_benefits),
                color = palette.foreground,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(I18nR.string.profile_edit_pts_summary),
            color = palette.mutedForeground,
            fontSize = 13.sp,
        )
        Spacer(Modifier.height(12.dp))
        listOf(
            stringResource(I18nR.string.profile_tier_gold_perk_1),
            stringResource(I18nR.string.profile_tier_gold_perk_2),
            stringResource(I18nR.string.profile_tier_gold_perk_3),
            stringResource(I18nR.string.profile_tier_gold_perk_4),
        ).forEach { perk ->
            Row(
                modifier = Modifier.padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(Icons.Outlined.Check, null, tint = palette.gold, modifier = Modifier.size(14.dp).padding(top = 4.dp))
                Text(perk, color = palette.foreground, fontSize = 13.sp, lineHeight = 18.sp)
            }
        }
    }
}
