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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.components.BottomModalSheet
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.i18n.R as I18nR

@Composable
fun ManageSheet(
    onDismiss: () -> Unit,
    onModify: () -> Unit,
    onCancel: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    BottomModalSheet(onDismiss = onDismiss) {
        Column(
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(bottom = 8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(palette.brand.copy(alpha = 0.10f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Shield,
                        contentDescription = null,
                        tint = palette.brand,
                        modifier = Modifier.size(20.dp),
                    )
                }
                Column {
                    Text(
                        text = stringResource(I18nR.string.manage_title),
                        color = palette.foreground,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Text(
                        text = stringResource(I18nR.string.manage_subtitle),
                        color = palette.mutedForeground,
                        fontSize = 13.sp,
                    )
                }
            }

            ManageActionRow(
                icon = Icons.Outlined.Edit,
                title = stringResource(I18nR.string.manage_modify),
                desc = stringResource(I18nR.string.manage_modify_desc),
                tone = ManageTone.Primary,
                onClick = onModify,
            )
            ManageActionRow(
                icon = Icons.Outlined.Delete,
                title = stringResource(I18nR.string.manage_cancel),
                desc = stringResource(I18nR.string.manage_cancel_desc),
                tone = ManageTone.Destructive,
                onClick = onCancel,
            )

            Spacer(Modifier.height(2.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(percent = 50))
                    .clickable(onClick = onDismiss),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(I18nR.string.manage_close),
                    color = palette.mutedForeground,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
        }
    }
}

private enum class ManageTone { Primary, Destructive }

@Composable
private fun ManageActionRow(
    icon: ImageVector,
    title: String,
    desc: String,
    tone: ManageTone,
    onClick: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val (iconBg, iconFg) = when (tone) {
        ManageTone.Primary -> palette.brand.copy(alpha = 0.10f) to palette.brand
        ManageTone.Destructive -> palette.destructive.copy(alpha = 0.10f) to palette.destructive
    }
    val rowShape = RoundedCornerShape(20.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(rowShape)
            .border(1.dp, palette.border, rowShape)
            .background(palette.cardSurface)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(iconBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconFg,
                modifier = Modifier.size(20.dp),
            )
        }
        Column(modifier = Modifier.padding(end = 4.dp)) {
            Text(
                text = title,
                color = palette.foreground,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                text = desc,
                color = palette.mutedForeground,
                fontSize = 12.sp,
            )
        }
    }
}
