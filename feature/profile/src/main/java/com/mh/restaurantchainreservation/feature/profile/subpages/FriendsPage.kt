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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.PersonRemove
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.i18n.R as I18nR

private enum class FriendsTab { Friends, Requests, Blocked }

private data class FriendContact(
    val id: String,
    val name: String,
    val handle: String,
    val initial: String,
)

private val DemoFriends = listOf(
    FriendContact("u1", "Sarah Kim", "@sarah", "S"),
    FriendContact("u2", "Min Park", "@min", "M"),
    FriendContact("u3", "James Lee", "@james", "J"),
    FriendContact("u4", "Yuki Tan", "@yuki", "Y"),
)
private val DemoRequests = listOf(
    FriendContact("u5", "Hana Choi", "@hana", "H"),
    FriendContact("u6", "Daniel Cho", "@daniel", "D"),
)
private val DemoBlocked = listOf(
    FriendContact("u7", "Anonymous", "@anon", "A"),
)

@Composable
fun FriendsPage(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val palette = LocalRestaurantPalette.current
    var tab by rememberSaveable { mutableStateOf(FriendsTab.Friends) }
    var query by rememberSaveable { mutableStateOf("") }

    SubpageScaffold(
        title = stringResource(I18nR.string.friends_title),
        onBack = onBack,
        modifier = modifier,
    ) {
        TabsRow(active = tab, onChange = { tab = it })
        Spacer(Modifier.height(16.dp))
        SearchField(value = query, onValueChange = { query = it })
        Spacer(Modifier.height(16.dp))

        val items = when (tab) {
            FriendsTab.Friends -> DemoFriends
            FriendsTab.Requests -> DemoRequests
            FriendsTab.Blocked -> DemoBlocked
        }
        val filtered = items.filter { query.isBlank() || it.name.contains(query, ignoreCase = true) }

        if (filtered.isEmpty()) {
            EmptyState(tab = tab)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                filtered.forEach { contact ->
                    ContactRow(contact = contact, tab = tab)
                }
            }
        }
        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun TabsRow(active: FriendsTab, onChange: (FriendsTab) -> Unit) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(palette.mutedSurface)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        TabChip(
            label = stringResource(I18nR.string.friends_tab_friends),
            active = active == FriendsTab.Friends,
            onClick = { onChange(FriendsTab.Friends) },
            modifier = Modifier.weight(1f),
        )
        TabChip(
            label = stringResource(I18nR.string.friends_tab_requests),
            active = active == FriendsTab.Requests,
            onClick = { onChange(FriendsTab.Requests) },
            modifier = Modifier.weight(1f),
        )
        TabChip(
            label = stringResource(I18nR.string.friends_tab_blocked),
            active = active == FriendsTab.Blocked,
            onClick = { onChange(FriendsTab.Blocked) },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun TabChip(label: String, active: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (active) palette.cardSurface else androidx.compose.ui.graphics.Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = if (active) palette.foreground else palette.mutedForeground,
            fontSize = 13.sp,
            fontWeight = if (active) FontWeight.SemiBold else FontWeight.Medium,
        )
    }
}

@Composable
private fun SearchField(value: String, onValueChange: (String) -> Unit) {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(14.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(palette.mutedSurface)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            imageVector = Icons.Outlined.Search,
            contentDescription = null,
            tint = palette.mutedForeground,
            modifier = Modifier.size(18.dp),
        )
        Box(modifier = Modifier.weight(1f)) {
            if (value.isEmpty()) {
                Text(
                    text = stringResource(I18nR.string.friends_search_hint),
                    color = palette.mutedForeground,
                    fontSize = 14.sp,
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                cursorBrush = SolidColor(palette.brand),
                textStyle = LocalTextStyle.current.merge(
                    TextStyle(color = palette.foreground, fontSize = 14.sp),
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun ContactRow(contact: FriendContact, tab: FriendsTab) {
    val palette = LocalRestaurantPalette.current
    val rowShape = RoundedCornerShape(16.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(rowShape)
            .border(1.dp, palette.border.copy(alpha = 0.6f), rowShape)
            .background(palette.cardSurface)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(palette.mutedSurface),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = contact.initial,
                color = palette.foreground,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = contact.name,
                color = palette.foreground,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = contact.handle,
                color = palette.mutedForeground,
                fontSize = 12.sp,
            )
        }
        when (tab) {
            FriendsTab.Friends -> ActionIcon(Icons.Outlined.PersonRemove, palette.mutedForeground)
            FriendsTab.Requests -> Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                ActionIcon(Icons.Outlined.Check, palette.success)
                ActionIcon(Icons.Outlined.Close, palette.mutedForeground)
            }
            FriendsTab.Blocked -> ActionIcon(Icons.Outlined.Block, palette.destructive)
        }
    }
}

@Composable
private fun ActionIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: androidx.compose.ui.graphics.Color,
) {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(palette.mutedSurface),
        contentAlignment = Alignment.Center,
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun EmptyState(tab: FriendsTab) {
    val palette = LocalRestaurantPalette.current
    val (titleRes, bodyRes) = when (tab) {
        FriendsTab.Friends -> I18nR.string.friends_empty_friends to I18nR.string.friends_empty_friends_body
        FriendsTab.Requests -> I18nR.string.friends_empty_requests to I18nR.string.friends_empty_requests_body
        FriendsTab.Blocked -> I18nR.string.friends_empty_blocked to I18nR.string.friends_empty_blocked_body
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(palette.mutedSurface),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = if (tab == FriendsTab.Blocked) Icons.Outlined.Block else if (tab == FriendsTab.Requests) Icons.Outlined.PersonAdd else Icons.Outlined.PersonOutline,
                contentDescription = null,
                tint = palette.mutedForeground,
                modifier = Modifier.size(24.dp),
            )
        }
        Spacer(Modifier.height(12.dp))
        Text(
            text = stringResource(titleRes),
            color = palette.foreground,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(bodyRes),
            color = palette.mutedForeground,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp),
        )
    }
}
