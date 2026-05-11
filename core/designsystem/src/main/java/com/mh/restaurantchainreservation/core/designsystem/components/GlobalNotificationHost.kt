package com.mh.restaurantchainreservation.core.designsystem.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class GlobalNotificationTone { Success, Info, Warning, Error }

data class GlobalNotificationMessage(
    val id: Long,
    val title: String,
    val message: String,
    val tone: GlobalNotificationTone,
)

object GlobalNotificationCenter {
    private val _message = MutableStateFlow<GlobalNotificationMessage?>(null)
    val message: StateFlow<GlobalNotificationMessage?> = _message.asStateFlow()

    fun show(title: String, message: String, tone: GlobalNotificationTone = GlobalNotificationTone.Info) {
        _message.value = GlobalNotificationMessage(System.nanoTime(), title, message, tone)
    }

    fun success(title: String, message: String) = show(title, message, GlobalNotificationTone.Success)
    fun info(title: String, message: String) = show(title, message, GlobalNotificationTone.Info)
    fun warning(title: String, message: String) = show(title, message, GlobalNotificationTone.Warning)
    fun error(title: String, message: String) = show(title, message, GlobalNotificationTone.Error)

    fun dismiss(id: Long) {
        if (_message.value?.id == id) _message.value = null
    }
}

@Composable
fun GlobalNotificationHost(
    modifier: Modifier = Modifier,
    bottomInset: PaddingValues = PaddingValues(0.dp),
) {
    val current by GlobalNotificationCenter.message.collectAsState()

    LaunchedEffect(current?.id) {
        val id = current?.id ?: return@LaunchedEffect
        delay(3200)
        GlobalNotificationCenter.dismiss(id)
    }

    Box(
        modifier = modifier.fillMaxSize().padding(bottom = bottomInset.calculateBottomPadding()),
        contentAlignment = Alignment.BottomCenter,
    ) {
        AnimatedVisibility(
            visible = current != null,
            enter = slideInVertically(initialOffsetY = { it }, animationSpec = spring(dampingRatio = 0.78f, stiffness = 360f)) +
                fadeIn(tween(180)) + scaleIn(initialScale = 0.96f, animationSpec = tween(180)),
            exit = slideOutVertically(targetOffsetY = { it / 2 }, animationSpec = tween(170)) +
                fadeOut(tween(170)) + scaleOut(targetScale = 0.96f, animationSpec = tween(170)),
        ) {
            current?.let { ToastCard(it) }
        }
    }
}

@Composable
private fun ToastCard(message: GlobalNotificationMessage) {
    val palette = LocalRestaurantPalette.current
    val (container, icon, iconVector) = toneMeta(message.tone)
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .fillMaxWidth()
            .widthIn(max = 520.dp)
            .shadow(16.dp, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .background(palette.cardSurface)
            .border(1.dp, palette.borderSoft, RoundedCornerShape(18.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(Modifier.size(42.dp).clip(CircleShape).background(container), contentAlignment = Alignment.Center) {
            Icon(iconVector, null, tint = icon, modifier = Modifier.size(21.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(message.title, color = palette.foreground, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(message.message, color = palette.mutedForeground, fontSize = 12.sp, lineHeight = 16.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
        Icon(
            Icons.Outlined.Close,
            contentDescription = "Dismiss notification",
            tint = palette.mutedForeground,
            modifier = Modifier.size(28.dp).clip(CircleShape).clickable { GlobalNotificationCenter.dismiss(message.id) }.padding(6.dp),
        )
    }
}

@Composable
private fun toneMeta(tone: GlobalNotificationTone): Triple<Color, Color, ImageVector> {
    val palette = LocalRestaurantPalette.current
    return when (tone) {
        GlobalNotificationTone.Success -> Triple(palette.success.copy(alpha = 0.12f), palette.success, Icons.Outlined.CheckCircle)
        GlobalNotificationTone.Info -> Triple(palette.brand.copy(alpha = 0.10f), palette.brand, Icons.Outlined.Info)
        GlobalNotificationTone.Warning -> Triple(palette.warning.copy(alpha = 0.14f), palette.warning, Icons.Outlined.WarningAmber)
        GlobalNotificationTone.Error -> Triple(palette.destructive.copy(alpha = 0.12f), palette.destructive, Icons.Outlined.ErrorOutline)
    }
}
