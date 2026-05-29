package com.mh.restaurantchainreservation.feature.booking

import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColors
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.model.Restaurant

@Composable
internal fun BookingStepHeader(
    restaurant: Restaurant,
    stepIndex: Int,
    title: String,
    onBack: () -> Unit,
    onClose: () -> Unit,
    onStepSelect: ((Int) -> Unit)? = null,
    maxSelectableStepIndex: Int = stepIndex,
) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(palette.cardSurface.copy(alpha = 0.94f))
            .border(width = 1.dp, color = palette.border)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(palette.mutedSurface)
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = palette.foreground,
                    modifier = Modifier.size(20.dp),
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
            ) {
                Text(
                    text = title,
                    color = palette.foreground,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = restaurant.name,
                    color = palette.mutedForeground,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                text = "${stepIndex + 1}/${PROGRESS_STEPS.size}",
                color = palette.foreground,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(palette.foreground.copy(alpha = 0.1f))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            )
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(palette.mutedSurface)
                    .clickable(onClick = onClose),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Filled.Close,
                    contentDescription = "Close",
                    tint = palette.foreground,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            PROGRESS_STEPS.forEachIndexed { index, _ ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(
                            if (index <= stepIndex) palette.foreground else palette.border,
                        )
                        .then(
                            if (onStepSelect != null && index <= maxSelectableStepIndex) {
                                Modifier.clickable { onStepSelect(index) }
                            } else {
                                Modifier
                            },
                        ),
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(palette.mutedSurface.copy(alpha = 0.65f))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = restaurant.image,
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp),
            ) {
                Text(
                    text = restaurant.name,
                    color = palette.foreground,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = restaurant.cuisine,
                    color = palette.mutedForeground,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Icon(
                imageVector = Icons.Outlined.Shield,
                contentDescription = null,
                tint = palette.foreground,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

@Composable
internal fun BookingSectionTitle(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(palette.foreground.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = palette.foreground, modifier = Modifier.size(16.dp))
        }
        Column(modifier = Modifier.padding(start = 8.dp)) {
            Text(title, color = palette.foreground, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = palette.mutedForeground,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }
    }
}

@Composable
internal fun BookingCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(24.dp), ambientColor = RestaurantColors.Base.black.copy(0.05f))
            .clip(RoundedCornerShape(24.dp))
            .background(palette.cardSurface)
            .border(1.dp, palette.border, RoundedCornerShape(24.dp))
            .padding(16.dp),
    ) {
        content()
    }
}

@Composable
internal fun BookingDetailRow(
    label: String,
    value: String,
    icon: ImageVector? = null,
) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(palette.mutedSurface.copy(alpha = 0.6f))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = palette.mutedForeground, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(label, color = palette.mutedForeground, fontSize = 13.sp, modifier = Modifier.weight(1f))
        Text(
            value,
            color = palette.foreground,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.End,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
internal fun BookingPrimaryButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(if (enabled) palette.foreground else palette.foreground.copy(alpha = 0.35f))
            .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = palette.pageBackground, fontSize = 15.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
internal fun BookingOutlineButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(999.dp))
            .border(1.dp, palette.border, RoundedCornerShape(999.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = palette.foreground, fontSize = 15.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
internal fun PreferenceChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Text(
        text = label,
        color = if (selected) palette.pageBackground else palette.foreground,
        fontSize = 13.sp,
        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(if (selected) palette.foreground else palette.cardSurface)
            .border(1.dp, if (selected) palette.foreground else palette.border, RoundedCornerShape(999.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
    )
}
