package com.mh.restaurantchainreservation.feature.dining.ui.modals

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.feature.dining.data.Booking
import kotlinx.coroutines.delay
import kotlin.math.abs

private data class InviteFriend(
    val id: String,
    val name: String,
    val initials: String,
)

private val SAMPLE_FRIENDS = listOf(
    InviteFriend("f1", "Sarah Kim", "SK"),
    InviteFriend("f2", "Marcus Johnson", "MJ"),
    InviteFriend("f3", "Emma Chen", "EC"),
    InviteFriend("f4", "David Park", "DP"),
    InviteFriend("f5", "Olivia Tran", "OT"),
    InviteFriend("f6", "Mina Park", "MP"),
    InviteFriend("f7", "Noah Williams", "NW"),
    InviteFriend("f8", "Ryan O'Brien", "RO"),
)

private enum class InviteTab { NotInvited, Invited }

/**
 * Centered modal — search + segmented tabs (Not invited / Invited) + scrollable
 * friend list with tap-to-toggle. Mirrors React `shared/InviteFriends.tsx`.
 */
@Composable
fun InviteFriendsSheet(
    booking: Booking,
    invited: Set<String>,
    onDismiss: () -> Unit,
    onInvitedChanged: (Set<String>) -> Unit,
) {
    val palette = LocalRestaurantPalette.current

    // initially invited (locked baseline) vs current selection
    val baseline = remember(booking.id) { invited }
    val selected = remember(booking.id) { mutableStateOf(invited) }
    var query by remember { mutableStateOf("") }
    var tab by remember { mutableStateOf(InviteTab.NotInvited) }
    var sent by remember { mutableStateOf(false) }

    LaunchedEffect(sent) {
        if (sent) {
            delay(1500L)
            onDismiss()
        }
    }

    val newSelections = selected.value - baseline
    val removedSelections = baseline - selected.value
    val hasChanges = newSelections.isNotEmpty() || removedSelections.isNotEmpty()

    val (notInvited, invitedList) = remember(query, selected.value) {
        val needle = query.trim().lowercase()
        val matches = SAMPLE_FRIENDS.filter { needle.isEmpty() || it.name.lowercase().contains(needle) }
        matches.partition { selected.value.contains(it.id).not() }
    }

    BottomModalSheet(onDismiss = onDismiss) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header with icon + title + subtitle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(palette.brand.copy(alpha = 0.10f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PersonAdd,
                        contentDescription = null,
                        tint = palette.brand,
                        modifier = Modifier.size(20.dp),
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Invite friends",
                        color = palette.foreground,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Text(
                        text = "${booking.restaurant} · ${booking.date} · ${booking.time}",
                        color = palette.mutedForeground,
                        fontSize = 13.sp,
                        maxLines = 2,
                    )
                }
            }

            if (sent) {
                Spacer(Modifier.height(20.dp))
                SuccessState(count = newSelections.size)
                return@Column
            }

            Spacer(Modifier.height(16.dp))

            // Search
            SearchField(value = query, onChange = { query = it })

            Spacer(Modifier.height(12.dp))

            // Segmented tabs with counts
            SegmentedTabs(
                tab = tab,
                onChange = { tab = it },
                notInvitedCount = notInvited.size,
                invitedCount = invitedList.size,
            )

            Spacer(Modifier.height(12.dp))

            val visibleList = if (tab == InviteTab.NotInvited) notInvited else invitedList
            // Section header with counts
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = if (tab == InviteTab.Invited) "Already invited" else "Available friends",
                    color = palette.foreground,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
                Text(
                    text = "${selected.value.size} selected",
                    color = palette.mutedForeground,
                    fontSize = 12.sp,
                )
            }

            Spacer(Modifier.height(8.dp))

            // Scrollable friend list
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 0.dp, max = 320.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (visibleList.isEmpty()) {
                    EmptyFriendsState(query = query, tab = tab)
                } else {
                    visibleList.forEach { friend ->
                        FriendRow(
                            friend = friend,
                            isSelected = selected.value.contains(friend.id),
                            wasPreviouslyInvited = baseline.contains(friend.id),
                            onToggle = {
                                val next = if (selected.value.contains(friend.id)) {
                                    selected.value - friend.id
                                } else {
                                    selected.value + friend.id
                                }
                                selected.value = next
                                onInvitedChanged(next)
                            },
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Send CTA
            val ctaLabel = when {
                newSelections.isNotEmpty() -> "Send ${newSelections.size} invite${if (newSelections.size == 1) "" else "s"}"
                hasChanges -> "Update invites"
                else -> "Select friends"
            }
            PrimaryActionButton(
                label = ctaLabel,
                enabled = hasChanges || selected.value.isNotEmpty(),
                onClick = { sent = true },
            )
        }
    }
}

@Composable
private fun SearchField(value: String, onChange: (String) -> Unit) {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(percent = 50)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(shape)
            .border(1.dp, palette.border, shape)
            .background(palette.mutedSurface.copy(alpha = 0.55f))
            .padding(horizontal = 14.dp),
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
                    text = "Search friends",
                    color = palette.mutedForeground,
                    fontSize = 14.sp,
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onChange,
                singleLine = true,
                cursorBrush = SolidColor(palette.brand),
                textStyle = TextStyle(color = palette.foreground, fontSize = 14.sp),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        if (value.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(palette.cardSurface)
                    .clickable { onChange("") },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = null,
                    tint = palette.mutedForeground,
                    modifier = Modifier.size(12.dp),
                )
            }
        }
    }
}

@Composable
private fun SegmentedTabs(
    tab: InviteTab,
    onChange: (InviteTab) -> Unit,
    notInvitedCount: Int,
    invitedCount: Int,
) {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(percent = 50)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .clip(shape)
            .background(palette.mutedSurface.copy(alpha = 0.65f))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        TabPill(
            modifier = Modifier.weight(1f),
            label = "Not invited",
            count = notInvitedCount,
            active = tab == InviteTab.NotInvited,
            icon = Icons.Outlined.PersonAdd,
            onClick = { onChange(InviteTab.NotInvited) },
        )
        TabPill(
            modifier = Modifier.weight(1f),
            label = "Invited",
            count = invitedCount,
            active = tab == InviteTab.Invited,
            icon = Icons.Filled.Check,
            onClick = { onChange(InviteTab.Invited) },
        )
    }
}

@Composable
private fun TabPill(
    modifier: Modifier,
    label: String,
    count: Int,
    active: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(percent = 50)
    Row(
        modifier = modifier
            .clip(shape)
            .background(if (active) palette.cardSurface else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (active) palette.foreground else palette.mutedForeground,
            modifier = Modifier.size(14.dp),
        )
        Spacer(Modifier.size(6.dp))
        Text(
            text = label,
            color = if (active) palette.foreground else palette.mutedForeground,
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
        )
        Spacer(Modifier.size(6.dp))
        Box(
            modifier = Modifier
                .defaultMinSize(minWidth = 18.dp)
                .heightIn(min = 18.dp)
                .clip(CircleShape)
                .background(if (active) palette.brand else palette.cardSurface)
                .padding(horizontal = 6.dp, vertical = 2.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = count.toString(),
                color = if (active) Color.White else palette.mutedForeground,
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
            )
        }
    }
}

@Composable
private fun FriendRow(
    friend: InviteFriend,
    isSelected: Boolean,
    wasPreviouslyInvited: Boolean,
    onToggle: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(20.dp)
    val checkScale by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = spring(stiffness = 420f, dampingRatio = 0.55f),
        label = "friend_check",
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                if (isSelected) palette.brand.copy(alpha = 0.08f) else palette.cardSurface,
            )
            .border(
                width = 1.dp,
                color = if (isSelected) palette.brand.copy(alpha = 0.35f) else palette.border,
                shape = shape,
            )
            .clickable(onClick = onToggle)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        AvatarBubble(name = friend.name, initials = friend.initials)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = friend.name,
                color = palette.foreground,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                text = when {
                    wasPreviouslyInvited && isSelected -> "Previously invited"
                    isSelected -> "Ready to send"
                    else -> "Not invited"
                },
                color = palette.mutedForeground,
                fontSize = 12.sp,
            )
        }
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(if (isSelected) palette.brand else palette.mutedSurface),
            contentAlignment = Alignment.Center,
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(16.dp)
                        .scale(checkScale),
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.PersonAdd,
                    contentDescription = null,
                    tint = palette.mutedForeground,
                    modifier = Modifier.size(14.dp),
                )
            }
        }
    }
}

@Composable
private fun AvatarBubble(name: String, initials: String) {
    val palette = LocalRestaurantPalette.current
    val hue = (name.hashCode() and 0x7FFFFFFF) % 360
    val color = androidx.compose.ui.graphics.Color.hsv(
        hue = hue.toFloat(),
        saturation = 0.55f,
        value = 0.85f,
    )
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initials,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
        )
    }
}

@Composable
private fun EmptyFriendsState(query: String, tab: InviteTab) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(palette.mutedSurface.copy(alpha = 0.55f))
            .padding(horizontal = 20.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(palette.cardSurface),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.PersonOutline,
                contentDescription = null,
                tint = palette.mutedForeground,
                modifier = Modifier.size(20.dp),
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = when {
                query.isNotBlank() -> "No friends match your search"
                tab == InviteTab.Invited -> "No invited friends yet"
                else -> "All friends are already invited"
            },
            color = palette.foreground,
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
        )
    }
}

@Composable
private fun SuccessState(count: Int) {
    val palette = LocalRestaurantPalette.current
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(stiffness = 380f, dampingRatio = 0.5f),
        label = "success_scale",
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(palette.success.copy(alpha = 0.12f))
                .scale(scale),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = palette.success,
                modifier = Modifier.size(32.dp),
            )
        }
        Spacer(Modifier.height(12.dp))
        Text(
            text = if (count > 0) "Sent $count invite${if (count == 1) "" else "s"}" else "Updated",
            color = palette.foreground,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Your friends will see the reservation in their inbox.",
            color = palette.mutedForeground,
            fontSize = 13.sp,
        )
    }
}

@Composable
private fun PrimaryActionButton(label: String, enabled: Boolean, onClick: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(percent = 50)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(shape)
            .background(if (enabled) palette.brand else palette.mutedSurface)
            .clickable(enabled = enabled, onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Filled.Send,
            contentDescription = null,
            tint = if (enabled) Color.White else palette.mutedForeground,
            modifier = Modifier.size(16.dp),
        )
        Spacer(Modifier.size(8.dp))
        Text(
            text = label,
            color = if (enabled) Color.White else palette.mutedForeground,
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold,
        )
    }
}
