package com.mh.restaurantchainreservation.feature.dining.ui.modals

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.PersonAdd
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.i18n.R as I18nR
import com.mh.restaurantchainreservation.feature.dining.data.Booking
import kotlinx.coroutines.delay

private val sampleContacts = listOf(
    "Alex" to "AL",
    "Jamie" to "JM",
    "Pat" to "PT",
    "Riley" to "RL",
    "Sam" to "SM",
    "Casey" to "CS",
)

@Composable
fun InviteFriendsSheet(
    booking: Booking,
    invited: Set<String>,
    onDismiss: () -> Unit,
    onInvitedChanged: (Set<String>) -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    var copied by remember { mutableStateOf(false) }
    var localInvited by remember(booking.id) { mutableStateOf(invited) }

    LaunchedEffect(copied) {
        if (copied) {
            delay(1500L)
            copied = false
        }
    }

    BottomModalSheet(onDismiss = onDismiss) {
        Column(modifier = Modifier.padding(20.dp)) {
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
                        imageVector = Icons.Outlined.PersonAdd,
                        contentDescription = null,
                        tint = palette.brand,
                        modifier = Modifier.size(20.dp),
                    )
                }
                Column {
                    Text(
                        text = stringResource(I18nR.string.invite_title),
                        color = palette.foreground,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Text(
                        text = stringResource(I18nR.string.invite_subtitle),
                        color = palette.mutedForeground,
                        fontSize = 13.sp,
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(palette.brand.copy(alpha = 0.06f))
                    .border(1.dp, palette.brand.copy(alpha = 0.20f), RoundedCornerShape(20.dp))
                    .padding(14.dp),
            ) {
                Text(
                    text = booking.restaurant,
                    color = palette.foreground,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                )
                Text(
                    text = "${booking.date} · ${booking.time}",
                    color = palette.mutedForeground,
                    fontSize = 13.sp,
                )
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(percent = 50))
                        .background(palette.cardSurface)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Link,
                        contentDescription = null,
                        tint = palette.brand,
                        modifier = Modifier.size(14.dp),
                    )
                    Text(
                        text = booking.confirmationNo,
                        color = palette.brand,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Group,
                    contentDescription = null,
                    tint = palette.foreground,
                    modifier = Modifier.size(18.dp),
                )
                Text(
                    text = stringResource(I18nR.string.invite_quick),
                    color = palette.foreground,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = stringResource(I18nR.string.invite_invited, localInvited.size),
                    color = palette.mutedForeground,
                    fontSize = 12.sp,
                )
            }
            Spacer(Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                sampleContacts.forEach { (name, initials) ->
                    val selected = localInvited.contains(name)
                    val checkScale by animateFloatAsState(
                        targetValue = if (selected) 1f else 0f,
                        animationSpec = spring(stiffness = 400f, dampingRatio = 0.55f),
                        label = "invite_check",
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(palette.cardSurface)
                            .border(1.dp, if (selected) palette.brand.copy(alpha = 0.3f) else palette.border, RoundedCornerShape(16.dp))
                            .clickable {
                                val next = if (selected) localInvited - name else localInvited + name
                                localInvited = next
                                onInvitedChanged(next)
                            }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(palette.brand.copy(alpha = 0.10f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(text = initials, color = palette.brand, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                        }
                        Text(
                            text = name,
                            color = palette.foreground,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.weight(1f),
                        )
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(if (selected) palette.brand else palette.mutedSurface),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier
                                    .size(16.dp)
                                    .graphicsLayer {
                                        scaleX = checkScale
                                        scaleY = checkScale
                                    },
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ActionPill(
                    text = if (copied) stringResource(I18nR.string.invite_copied) else stringResource(I18nR.string.invite_copy_code),
                    primary = false,
                    onClick = { copied = true },
                    modifier = Modifier.weight(1f),
                )
                ActionPill(
                    text = stringResource(I18nR.string.invite_share_link),
                    primary = false,
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(8.dp))
            ActionPill(
                text = stringResource(I18nR.string.invite_done),
                primary = true,
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun ActionPill(text: String, primary: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(percent = 50)
    val container = if (primary) palette.brand else palette.cardSurface
    val content = if (primary) Color.White else palette.foreground
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(shape)
            .let { if (!primary) it.border(1.dp, palette.border, shape) else it }
            .background(container)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, color = content, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
    }
}
