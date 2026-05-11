package com.mh.restaurantchainreservation.feature.profile.hub

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.i18n.R as I18nR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvatarPickerModal(
    visible: Boolean,
    pendingAvatar: String?,
    onSelect: (String?) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
) {
    if (!visible) return
    val palette = LocalRestaurantPalette.current
    val sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = palette.cardSurface,
        contentColor = palette.foreground,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        dragHandle = null,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 16.dp, top = 24.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(modifier = Modifier.padding(end = 12.dp)) {
                    Text(
                        text = stringResource(I18nR.string.profile_avatar_picker_title),
                        color = palette.foreground,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = stringResource(I18nR.string.profile_avatar_picker_subtitle),
                        color = palette.mutedForeground,
                        fontSize = 14.sp,
                    )
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(palette.mutedSurface)
                        .clickable(
                            role = Role.Button,
                            onClickLabel = stringResource(I18nR.string.common_action_close),
                            onClick = onDismiss,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = stringResource(I18nR.string.common_action_close),
                        tint = palette.foreground,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(PresetAvatars) { avatar ->
                    AvatarTile(
                        avatar = avatar,
                        selected = pendingAvatar == avatar.src,
                        onClick = { onSelect(avatar.src) },
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, color = palette.border, shape = RoundedCornerShape(0.dp))
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FooterButton(
                    text = stringResource(I18nR.string.profile_avatar_picker_reset),
                    background = palette.mutedSurface,
                    foreground = palette.foreground,
                    onClick = { onSelect(null) },
                    modifier = Modifier.weight(1f),
                )
                FooterButton(
                    text = stringResource(I18nR.string.profile_avatar_picker_save),
                    background = palette.brand,
                    foreground = Color.White,
                    onClick = onSave,
                    modifier = Modifier.weight(2f),
                )
            }
        }
    }
}

@Composable
private fun AvatarTile(
    avatar: PresetAvatar,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val ringColor = if (selected) palette.foreground else palette.border
    val ringWidth = if (selected) 2.dp else 1.dp
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            .border(ringWidth, ringColor, CircleShape)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        val context = LocalContext.current
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(avatar.src)
                .crossfade(true)
                .build(),
            contentDescription = avatar.label,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        if (selected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.20f)),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color.Black),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun FooterButton(
    text: String,
    background: Color,
    foreground: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(background)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = foreground,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
