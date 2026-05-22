package com.mh.restaurantchainreservation.feature.booking

import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColors
import android.content.Context
import android.content.Intent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.IosShare
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.model.Restaurant
import kotlinx.coroutines.launch

private data class PhotoShareContact(
    val id: String,
    val name: String,
    val handle: String,
)

private val PhotoShareContacts = listOf(
    PhotoShareContact("c1", "Alex Kim", "@alexkim"),
    PhotoShareContact("c2", "Jordan Lee", "@jordanlee"),
    PhotoShareContact("c3", "Sam Rivera", "@samrivera"),
    PhotoShareContact("c4", "Taylor Chen", "@taylorchen"),
    PhotoShareContact("c5", "Morgan Patel", "@morganpatel"),
)

private fun buildPhotoShareMessage(restaurant: Restaurant, friendFirstName: String? = null): String {
    val greeting = friendFirstName?.let { "Hey $it,\n\n" }.orEmpty()
    return buildString {
        append(greeting)
        append("Check out ${restaurant.name} on RestaurantChain")
        if (!restaurant.cuisine.isBlank()) {
            append(" — ${restaurant.cuisine}")
        }
        append(" · ★ ${"%.1f".format(restaurant.rating)}")
        append("\n\nI'd love to go here with you. Open the app to see photos and book a table!")
    }
}

private fun sharePhotosToContact(context: Context, restaurant: Restaurant, contact: PhotoShareContact) {
    val message = buildPhotoShareMessage(restaurant, contact.name.substringBefore(' '))
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, restaurant.name)
        putExtra(Intent.EXTRA_TEXT, message)
    }
    context.startActivity(
        Intent.createChooser(intent, "Send ${restaurant.name} to ${contact.name}"),
    )
}

private fun sharePhotosWithSystemChooser(context: Context, restaurant: Restaurant) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, restaurant.name)
        putExtra(Intent.EXTRA_TEXT, buildPhotoShareMessage(restaurant))
    }
    context.startActivity(Intent.createChooser(intent, "Share photos"))
}

@Composable
fun RestaurantPhotoShareSheet(
    restaurant: Restaurant,
    onDismiss: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val context = LocalContext.current
    val backdropAlpha = remember { Animatable(0f) }
    val sheetOffset = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()
    var dismissing by remember { mutableStateOf(false) }

    val triggerDismiss: () -> Unit = remember(onDismiss) {
        {
            if (!dismissing) {
                dismissing = true
                scope.launch {
                    launch { backdropAlpha.animateTo(0f, tween(180)) }
                    launch { sheetOffset.animateTo(1f, tween(220)) }
                    onDismiss()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        launch { backdropAlpha.animateTo(1f, tween(220, easing = LinearOutSlowInEasing)) }
        launch {
            sheetOffset.animateTo(0f, spring(dampingRatio = 0.85f, stiffness = 270f))
        }
    }

    Dialog(
        onDismissRequest = triggerDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
        ),
    ) {
        val density = LocalDensity.current
        val sheetTranslateY = with(density) { sheetOffset.value * 520.dp.toPx() }

        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(RestaurantColors.Base.black.copy(alpha = backdropAlpha.value * 0.4f))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                    ) { triggerDismiss() },
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .widthIn(max = 560.dp)
                    .graphicsLayer { translationY = sheetTranslateY }
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .background(palette.cardSurface)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                    ) {}
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(bottom = 12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp, horizontal = 16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Share photos",
                            color = palette.foreground,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = "Send ${restaurant.name} to a contact",
                            color = palette.mutedForeground,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(top = 4.dp),
                        )
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable { triggerDismiss() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = palette.foreground,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
                HorizontalDivider(color = palette.border)
                PhotoShareContacts.forEach { contact ->
                    PhotoShareContactRow(
                        contact = contact,
                        onSend = {
                            sharePhotosToContact(context, restaurant, contact)
                            triggerDismiss()
                        },
                    )
                    HorizontalDivider(color = palette.border)
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            sharePhotosWithSystemChooser(context, restaurant)
                            triggerDismiss()
                        }
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.IosShare,
                        contentDescription = null,
                        tint = palette.foreground,
                        modifier = Modifier.size(22.dp),
                    )
                    Text(
                        text = "Share another way",
                        color = palette.foreground,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }
    }
}

@Composable
private fun PhotoShareContactRow(
    contact: PhotoShareContact,
    onSend: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val initial = contact.name.firstOrNull()?.uppercase() ?: "?"
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(palette.mutedSurface)
                .border(1.dp, palette.border, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = initial,
                color = palette.foreground,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = contact.name,
                color = palette.foreground,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = contact.handle,
                color = palette.mutedForeground,
                fontSize = 13.sp,
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(palette.foreground)
                .clickable(onClick = onSend)
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            Text(
                text = "Send",
                color = palette.cardSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}
