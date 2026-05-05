package com.mh.restaurantchainreservation.feature.profile.subpages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material.icons.outlined.SettingsBrightness
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Smartphone
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.theme.ThemePreference
import com.mh.restaurantchainreservation.core.designsystem.theme.rememberThemeController
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.i18n.LocaleManager
import com.mh.restaurantchainreservation.core.i18n.R as I18nR

@Composable
fun SettingsPageFull(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val palette = LocalRestaurantPalette.current
    val context = LocalContext.current
    val themeController = rememberThemeController(context.applicationContext)

    var orderUpdates by rememberSaveable { mutableStateOf(true) }
    var promotions by rememberSaveable { mutableStateOf(true) }
    var reservationReminders by rememberSaveable { mutableStateOf(true) }
    var rewardsPoints by rememberSaveable { mutableStateOf(false) }
    var newRestaurants by rememberSaveable { mutableStateOf(false) }
    var soundEnabled by rememberSaveable { mutableStateOf(true) }
    var volume by rememberSaveable { mutableFloatStateOf(35f) }
    var locationTracking by rememberSaveable { mutableStateOf(true) }
    var orderHistoryVisible by rememberSaveable { mutableStateOf(true) }
    var personalizedAds by rememberSaveable { mutableStateOf(false) }
    var dataSharing by rememberSaveable { mutableStateOf(false) }

    var showPasswordForm by rememberSaveable { mutableStateOf(false) }
    var currentPassword by rememberSaveable { mutableStateOf("") }
    var newPassword by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var showDeleteConfirm by rememberSaveable { mutableStateOf(false) }
    var currentLocale by rememberSaveable { mutableStateOf(LocaleManager.getLocale(context)) }

    SubpageScaffold(
        title = stringResource(I18nR.string.title_settings),
        onBack = onBack,
        modifier = modifier,
    ) {
        SettingsGroup(label = stringResource(I18nR.string.profile_settings_appearance)) {
            ThemePickerRow(
                selected = themeController.preference,
                onSelect = { themeController.setPreference(it) },
            )
        }

        LanguageGroup(
            currentLocale = currentLocale,
            onSelect = { tag ->
                currentLocale = tag
                LocaleManager.setLocale(context, tag)
            },
        )

        SettingsGroup(label = stringResource(I18nR.string.settings_section_notifications)) {
            ToggleRow(Icons.Outlined.NotificationsNone, stringResource(I18nR.string.settings_row_order_updates), stringResource(I18nR.string.settings_row_order_updates_desc), orderUpdates) { orderUpdates = it }
            DividerLine()
            ToggleRow(Icons.Outlined.CardGiftcard, stringResource(I18nR.string.settings_row_promotions), stringResource(I18nR.string.settings_row_promotions_desc), promotions) { promotions = it }
            DividerLine()
            ToggleRow(Icons.Outlined.CalendarToday, stringResource(I18nR.string.settings_row_reservation_reminders), stringResource(I18nR.string.settings_row_reservation_reminders_desc), reservationReminders) { reservationReminders = it }
            DividerLine()
            ToggleRow(Icons.Outlined.StarBorder, stringResource(I18nR.string.settings_row_rewards_points), stringResource(I18nR.string.settings_row_rewards_points_desc), rewardsPoints) { rewardsPoints = it }
            DividerLine()
            ToggleRow(Icons.Outlined.RestaurantMenu, stringResource(I18nR.string.settings_row_new_restaurants), stringResource(I18nR.string.settings_row_new_restaurants_desc), newRestaurants) { newRestaurants = it }
        }

        SettingsGroup(label = stringResource(I18nR.string.settings_section_sound_haptics)) {
            ToggleRow(Icons.Outlined.VolumeUp, stringResource(I18nR.string.settings_row_sound), stringResource(I18nR.string.settings_row_sound_desc), soundEnabled) { soundEnabled = it }
            AnimatedVisibility(
                visible = soundEnabled,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
            ) {
                Column {
                    DividerLine()
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Icon(Icons.Outlined.VolumeUp, null, tint = palette.foreground.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
                        Slider(
                            value = volume,
                            onValueChange = { volume = it },
                            valueRange = 0f..100f,
                            colors = SliderDefaults.colors(
                                thumbColor = palette.brand,
                                activeTrackColor = palette.brand,
                                inactiveTrackColor = palette.border,
                            ),
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }

        SettingsGroup(label = stringResource(I18nR.string.settings_section_privacy)) {
            ToggleRow(Icons.Outlined.LocationOn, stringResource(I18nR.string.settings_row_location), stringResource(I18nR.string.settings_row_location_desc), locationTracking) { locationTracking = it }
            DividerLine()
            ToggleRow(Icons.Outlined.Visibility, stringResource(I18nR.string.settings_row_order_history), stringResource(I18nR.string.settings_row_order_history_desc), orderHistoryVisible) { orderHistoryVisible = it }
            DividerLine()
            ToggleRow(Icons.Outlined.Smartphone, stringResource(I18nR.string.settings_row_personalized_ads), stringResource(I18nR.string.settings_row_personalized_ads_desc), personalizedAds) { personalizedAds = it }
            DividerLine()
            ToggleRow(Icons.Outlined.Share, stringResource(I18nR.string.settings_row_data_sharing), stringResource(I18nR.string.settings_row_data_sharing_desc), dataSharing) { dataSharing = it }
        }

        Text(
            text = stringResource(I18nR.string.settings_section_security),
            color = palette.foreground,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 4.dp, top = 16.dp, bottom = 8.dp),
        )

        if (!showPasswordForm) {
            OutlineButton(
                text = stringResource(I18nR.string.settings_change_password),
                icon = Icons.Outlined.Lock,
                onClick = { showPasswordForm = true },
            )
        } else {
            PasswordForm(
                current = currentPassword,
                next = newPassword,
                confirm = confirmPassword,
                onCurrentChange = { currentPassword = it },
                onNextChange = { newPassword = it },
                onConfirmChange = { confirmPassword = it },
                onClose = { showPasswordForm = false },
                onSave = {
                    if (newPassword.isNotEmpty() && newPassword == confirmPassword) {
                        showPasswordForm = false
                        currentPassword = ""
                        newPassword = ""
                        confirmPassword = ""
                    }
                },
            )
        }
        Spacer(Modifier.height(12.dp))
        BrandFilledButton(
            text = stringResource(I18nR.string.settings_delete_account),
            onClick = { showDeleteConfirm = true },
        )
        Spacer(Modifier.height(40.dp))
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text(stringResource(I18nR.string.settings_delete_modal_title)) },
            text = { Text(stringResource(I18nR.string.settings_delete_modal_body)) },
            confirmButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text(stringResource(I18nR.string.common_delete), color = palette.brand, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text(stringResource(I18nR.string.common_cancel))
                }
            },
        )
    }
}

@Composable
private fun SettingsGroup(label: String, content: @Composable () -> Unit) {
    val palette = LocalRestaurantPalette.current
    val groupShape = RoundedCornerShape(16.dp)
    Spacer(Modifier.height(20.dp))
    Text(label, color = palette.foreground, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp, bottom = 10.dp))
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(groupShape)
            .border(1.dp, palette.border, groupShape)
            .background(palette.cardSurface),
    ) {
        content()
    }
}

@Composable
private fun DividerLine() {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(palette.border.copy(alpha = 0.6f)),
    )
}

@Composable
private fun ToggleRow(
    icon: ImageVector,
    label: String,
    description: String,
    checked: Boolean,
    onToggle: (Boolean) -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle(!checked) }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier.size(32.dp).clip(CircleShape).background(palette.mutedSurface),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = palette.foreground, modifier = Modifier.size(18.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(label, color = palette.foreground, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text(description, color = palette.mutedForeground, fontSize = 12.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedTrackColor = palette.brand,
                checkedThumbColor = Color.White,
                uncheckedTrackColor = palette.mutedForeground.copy(alpha = 0.30f),
                uncheckedBorderColor = Color.Transparent,
            ),
        )
    }
}

@Composable
private fun ThemePickerRow(selected: ThemePreference, onSelect: (ThemePreference) -> Unit) {
    val palette = LocalRestaurantPalette.current
    Column(modifier = Modifier.padding(14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                modifier = Modifier.size(32.dp).clip(CircleShape).background(palette.mutedSurface),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.LightMode, null, tint = palette.foreground, modifier = Modifier.size(18.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(stringResource(I18nR.string.settings_color_theme), color = palette.foreground, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text(stringResource(I18nR.string.settings_color_theme_desc), color = palette.mutedForeground, fontSize = 12.sp)
            }
        }
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ThemeChip(
                label = stringResource(I18nR.string.profile_settings_theme_light),
                icon = Icons.Outlined.LightMode,
                selected = selected == ThemePreference.Light,
                onClick = { onSelect(ThemePreference.Light) },
                modifier = Modifier.weight(1f),
            )
            ThemeChip(
                label = stringResource(I18nR.string.profile_settings_theme_dark),
                icon = Icons.Outlined.Bedtime,
                selected = selected == ThemePreference.Dark,
                onClick = { onSelect(ThemePreference.Dark) },
                modifier = Modifier.weight(1f),
            )
            ThemeChip(
                label = stringResource(I18nR.string.profile_settings_theme_system),
                icon = Icons.Outlined.SettingsBrightness,
                selected = selected == ThemePreference.System,
                onClick = { onSelect(ThemePreference.System) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun ThemeChip(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(12.dp)
    Column(
        modifier = modifier
            .clip(shape)
            .background(if (selected) palette.foreground else palette.mutedSurface)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(icon, null, tint = if (selected) palette.cardSurface else palette.foreground, modifier = Modifier.size(18.dp))
        Spacer(Modifier.height(4.dp))
        Text(label, color = if (selected) palette.cardSurface else palette.foreground, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun LanguageGroup(currentLocale: String, onSelect: (String) -> Unit) {
    val palette = LocalRestaurantPalette.current
    val groupShape = RoundedCornerShape(16.dp)
    Spacer(Modifier.height(20.dp))
    Text(
        stringResource(I18nR.string.profile_menu_language),
        color = palette.foreground,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 4.dp, bottom = 10.dp),
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(groupShape)
            .border(1.dp, palette.border, groupShape)
            .background(palette.cardSurface)
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        LanguageChip(
            label = stringResource(I18nR.string.profile_lang_english),
            selected = currentLocale.startsWith("en"),
            onClick = { onSelect("en") },
            modifier = Modifier.weight(1f),
        )
        LanguageChip(
            label = stringResource(I18nR.string.profile_lang_korean),
            selected = currentLocale.startsWith("ko"),
            onClick = { onSelect("ko") },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun LanguageChip(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(12.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .background(if (selected) palette.foreground else palette.mutedSurface)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, color = if (selected) palette.cardSurface else palette.foreground, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun OutlineButton(text: String, icon: ImageVector, onClick: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(shape)
            .border(1.dp, palette.border, shape)
            .background(palette.cardSurface)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(icon, null, tint = palette.foreground, modifier = Modifier.size(16.dp))
        Text(text, color = palette.foreground, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun BrandFilledButton(text: String, onClick: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(palette.brand)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun PasswordForm(
    current: String,
    next: String,
    confirm: String,
    onCurrentChange: (String) -> Unit,
    onNextChange: (String) -> Unit,
    onConfirmChange: (String) -> Unit,
    onClose: () -> Unit,
    onSave: () -> Unit,
) {
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
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(I18nR.string.settings_update_password), color = palette.foreground, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = onClose) {
                Text(stringResource(I18nR.string.common_cancel), color = palette.mutedForeground)
            }
        }
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = current,
            onValueChange = onCurrentChange,
            label = { Text(stringResource(I18nR.string.settings_current_password)) },
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(12.dp),
            colors = textFieldColorsForSettings(),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = next,
            onValueChange = onNextChange,
            label = { Text(stringResource(I18nR.string.settings_new_password)) },
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(12.dp),
            colors = textFieldColorsForSettings(),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = confirm,
            onValueChange = onConfirmChange,
            label = { Text(stringResource(I18nR.string.settings_confirm_new_password)) },
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(12.dp),
            colors = textFieldColorsForSettings(),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(12.dp))
        BrandFilledButton(text = stringResource(I18nR.string.settings_save_new_password), onClick = onSave)
    }
}

@Composable
private fun textFieldColorsForSettings() = TextFieldDefaults.colors(
    focusedIndicatorColor = LocalRestaurantPalette.current.brand,
    unfocusedIndicatorColor = LocalRestaurantPalette.current.border,
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
)

@Suppress("unused")
private val unusedIcon: ImageVector = Icons.Outlined.Bookmark
