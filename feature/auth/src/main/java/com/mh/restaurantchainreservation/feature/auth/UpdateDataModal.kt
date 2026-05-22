@file:OptIn(ExperimentalHazeMaterialsApi::class)

package com.mh.restaurantchainreservation.feature.auth

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.util.lerp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.mh.restaurantchainreservation.core.designsystem.components.icons.LucideIcon
import com.mh.restaurantchainreservation.core.designsystem.components.icons.LucidePaths
import com.mh.restaurantchainreservation.core.designsystem.components.icons.TonightLogoMark
import com.mh.restaurantchainreservation.core.designsystem.R as DsR
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantPalette
import com.mh.restaurantchainreservation.core.i18n.R as I18nR
import com.mh.restaurantchainreservation.core.model.LocalDataSyncStore
import com.mh.restaurantchainreservation.core.model.UpdateDataModalStore
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Discover stays unobstructed before the overlay is mounted. */
private const val UpdateModalDiscoverSettleMs = 520
private const val UpdateModalBackdropFadeMs = 280
private const val UpdateModalCardEnterMs = 320
private const val UpdateModalCardEnterStaggerMs = 72
/** Mount tab bar only after overlay entrance covers the bottom chrome. */
private const val UpdateModalNavRevealDelayMs =
    UpdateModalBackdropFadeMs + UpdateModalCardEnterStaggerMs + UpdateModalCardEnterMs
private const val UpdateModalDismissMs = 200
private const val UpdateModalCompleteVisibleMs = 1500
private const val UpdateModalMaxHeightFraction = 0.88f
private const val UpdateModalMaxVisibleFeatures = 4
private val WhatsNewTitleCollapseRange = 36.dp
private val WhatsNewTitleToDividerGap = 6.dp
private val WhatsNewFeatureListEdgePadding = 10.dp
private const val WhatsNewTitleFontExpandedSp = 17f
private const val WhatsNewTitleFontCollapsedSp = 14f
private const val WhatsNewTitleLineExpandedSp = 22f
private const val WhatsNewTitleLineCollapsedSp = 18f
private val UpdateModalCardPadding = 40.dp
private val UpdateModalPromptHeaderEstimate = 272.dp
private val UpdateModalPromptFooterEstimate = 96.dp
private val UpdateModalActionRowHeight = 44.dp
private val UpdateModalUpdateButtonWidth = 168.dp
private val UpdateModalFeatureRowHeight = 46.dp

private val UpdateModalEnterEasing = FastOutSlowInEasing

private data class UpdateFeatureItem(
    val titleRes: Int,
    val bodyRes: Int,
)

private val DefaultUpdateFeatures = listOf(
    UpdateFeatureItem(I18nR.string.update_data_feature_search_title, I18nR.string.update_data_feature_search_body),
    UpdateFeatureItem(I18nR.string.update_data_feature_booking_title, I18nR.string.update_data_feature_booking_body),
    UpdateFeatureItem(I18nR.string.update_data_feature_rewards_title, I18nR.string.update_data_feature_rewards_body),
    UpdateFeatureItem(I18nR.string.update_data_feature_map_title, I18nR.string.update_data_feature_map_body),
    UpdateFeatureItem(I18nR.string.update_data_feature_offline_title, I18nR.string.update_data_feature_offline_body),
    UpdateFeatureItem(I18nR.string.update_data_feature_notifications_title, I18nR.string.update_data_feature_notifications_body),
    UpdateFeatureItem(I18nR.string.update_data_feature_payments_title, I18nR.string.update_data_feature_payments_body),
    UpdateFeatureItem(I18nR.string.update_data_feature_profile_title, I18nR.string.update_data_feature_profile_body),
    UpdateFeatureItem(I18nR.string.update_data_feature_menu_title, I18nR.string.update_data_feature_menu_body),
    UpdateFeatureItem(I18nR.string.update_data_feature_reviews_title, I18nR.string.update_data_feature_reviews_body),
)

private enum class UpdateDataPhase { Prompt, Syncing, Complete }

private enum class UpdateHeroBadge { Sync, SyncSpinning, ShieldCheck }

private enum class SyncStepState { Pending, Active, Done }

private data class SyncStep(val labelRes: Int)

private val SyncSteps = listOf(
    SyncStep(I18nR.string.update_data_step_connect),
    SyncStep(I18nR.string.update_data_step_catalog),
    SyncStep(I18nR.string.update_data_step_reservations),
    SyncStep(I18nR.string.update_data_step_recommendations),
    SyncStep(I18nR.string.update_data_step_cache),
)

@Composable
fun UpdateDataModalHost(
    authenticated: Boolean,
    hazeState: HazeState,
) {
    val pending by UpdateDataModalStore.pendingAfterLogin.collectAsState()
    var showModal by remember { mutableStateOf(false) }

    LaunchedEffect(authenticated, pending) {
        if (!authenticated || !pending) {
            showModal = false
            return@LaunchedEffect
        }
        showModal = false
        // Let Discover compose and draw before the overlay mounts.
        repeat(3) { withFrameNanos { } }
        delay(UpdateModalDiscoverSettleMs.toLong())
        if (authenticated && pending) {
            showModal = true
            delay(UpdateModalNavRevealDelayMs.toLong())
            if (authenticated && pending) {
                UpdateDataModalStore.revealBottomNavUnderUpdateModal()
            }
        }
    }

    if (!showModal) return

    UpdateDataModal(
        hazeState = hazeState,
        onDismiss = {
            showModal = false
            LocalDataSyncStore.postponeUpdatePrompt()
            UpdateDataModalStore.dismiss()
        },
    )
}

@Composable
fun UpdateDataModal(
    hazeState: HazeState,
    onDismiss: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val scope = rememberCoroutineScope()
    var phase by remember { mutableStateOf(UpdateDataPhase.Prompt) }
    var progress by remember { mutableIntStateOf(0) }
    var activeStepIndex by remember { mutableIntStateOf(0) }

    val backdropAlpha = remember { Animatable(0f) }
    val cardScale = remember { Animatable(0.96f) }
    val cardOffsetY = remember { Animatable(14f) }

    suspend fun resetEntrance() {
        backdropAlpha.snapTo(0f)
        cardScale.snapTo(0.96f)
        cardOffsetY.snapTo(14f)
    }

    val dismissSheet: () -> Unit = {
        scope.launch {
            launch { cardScale.animateTo(0.96f, tween(UpdateModalDismissMs, easing = UpdateModalEnterEasing)) }
            launch { cardOffsetY.animateTo(10f, tween(UpdateModalDismissMs, easing = UpdateModalEnterEasing)) }
            backdropAlpha.animateTo(0f, tween(UpdateModalDismissMs + 40, easing = UpdateModalEnterEasing))
            phase = UpdateDataPhase.Prompt
            progress = 0
            activeStepIndex = 0
            resetEntrance()
            onDismiss()
        }
    }

    LaunchedEffect(Unit) {
        resetEntrance()
        backdropAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(UpdateModalBackdropFadeMs, easing = UpdateModalEnterEasing),
        )
        delay(UpdateModalCardEnterStaggerMs.toLong())
        kotlinx.coroutines.coroutineScope {
            launch {
                cardScale.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(UpdateModalCardEnterMs, easing = UpdateModalEnterEasing),
                )
            }
            launch {
                cardOffsetY.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(UpdateModalCardEnterMs, easing = UpdateModalEnterEasing),
                )
            }
        }
    }

    LaunchedEffect(phase) {
        when (phase) {
            UpdateDataPhase.Syncing -> {
                val ticks = 100
                val tickMs = 42L
                for (i in 0..ticks) {
                    progress = i
                    activeStepIndex = (i * SyncSteps.size / ticks).coerceIn(0, SyncSteps.lastIndex)
                    if (i < ticks) delay(tickMs)
                }
                progress = 100
                phase = UpdateDataPhase.Complete
            }
            UpdateDataPhase.Complete -> {
                LocalDataSyncStore.markCatalogSynced()
                delay(UpdateModalCompleteVisibleMs.toLong())
                dismissSheet()
            }
            else -> Unit
        }
    }

    val overlayInteractive = backdropAlpha.value >= 0.88f && phase == UpdateDataPhase.Prompt
    val veil = updateDataOverlayVeil(palette)

    Box(modifier = Modifier.fillMaxSize().zIndex(200f)) {
        if (backdropAlpha.value > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { alpha = backdropAlpha.value }
                    .hazeEffect(state = hazeState, style = HazeMaterials.thin())
                    .background(veil)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        enabled = overlayInteractive,
                        onClick = { if (overlayInteractive) dismissSheet() },
                    ),
            )
        }

        if (backdropAlpha.value > 0.01f) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.Center,
            ) {
                val cardMaxHeight = maxHeight * UpdateModalMaxHeightFraction
                val cardScrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .widthIn(max = 400.dp)
                        .fillMaxWidth()
                        .heightIn(max = cardMaxHeight)
                        .graphicsLayer {
                            alpha = backdropAlpha.value.coerceIn(0f, 1f)
                            scaleX = cardScale.value
                            scaleY = cardScale.value
                            translationY = cardOffsetY.value
                        }
                        .clip(RoundedCornerShape(28.dp))
                        .background(palette.cardSurface)
                        .border(1.dp, palette.border, RoundedCornerShape(28.dp))
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            enabled = false,
                            onClick = {},
                        )
                        .verticalScroll(cardScrollState)
                        .padding(20.dp),
                ) {
                    when (phase) {
                        UpdateDataPhase.Prompt -> PromptContent(
                            maxCardHeight = cardMaxHeight,
                            features = DefaultUpdateFeatures,
                            onLater = dismissSheet,
                            onUpdate = { phase = UpdateDataPhase.Syncing },
                            onClose = dismissSheet,
                        )
                        UpdateDataPhase.Syncing -> SyncingContent(
                            progress = progress,
                            activeStepIndex = activeStepIndex,
                        )
                        UpdateDataPhase.Complete -> CompleteContent()
                    }
                }
            }
        }
    }
}

/** Soft slate veil on top of [HazeMaterials.thin], matching Discover glass overlays. */
private fun updateDataOverlayVeil(@Suppress("UNUSED_PARAMETER") palette: RestaurantPalette): Color =
    Color(0xFF3D4F63).copy(alpha = 0.32f)

@Composable
private fun PromptContent(
    maxCardHeight: Dp,
    features: List<UpdateFeatureItem>,
    onLater: () -> Unit,
    onUpdate: () -> Unit,
    onClose: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val releaseVersion = stringResource(I18nR.string.update_data_release_version)

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(palette.mutedSurface)
                .clickable(onClick = onClose),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = stringResource(I18nR.string.common_action_close),
                tint = palette.foreground,
                modifier = Modifier.size(16.dp),
            )
        }
    }
    Spacer(Modifier.height(4.dp))
    UpdateDataHeroIcon()
    Spacer(Modifier.height(16.dp))
    Text(
        text = stringResource(I18nR.string.update_data_title),
        color = palette.foreground,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
    )
    Spacer(Modifier.height(8.dp))
    Text(
        text = stringResource(I18nR.string.update_data_subtitle),
        color = palette.mutedForeground,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    )
    Spacer(Modifier.height(16.dp))
    WhatsNewCard(
        releaseVersion = releaseVersion,
        features = features,
        maxCardHeight = maxCardHeight,
    )
    Spacer(Modifier.height(16.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(palette.border),
    )
    Spacer(Modifier.height(16.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(UpdateModalActionRowHeight)
            .padding(horizontal = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(I18nR.string.update_data_later),
            color = palette.foreground,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onLater),
        )
        Box(
            modifier = Modifier
                .width(UpdateModalUpdateButtonWidth)
                .fillMaxHeight()
                .clip(RoundedCornerShape(UpdateModalActionRowHeight / 2))
                .background(palette.brand)
                .clickable(onClick = onUpdate),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(I18nR.string.update_data_action),
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun SyncingContent(
    progress: Int,
    activeStepIndex: Int,
) {
    val palette = LocalRestaurantPalette.current
    val activeLabel = stringResource(SyncSteps[activeStepIndex.coerceIn(SyncSteps.indices)].labelRes)

    UpdateDataHeroIcon(badge = UpdateHeroBadge.SyncSpinning)
    Spacer(Modifier.height(14.dp))
    Text(
        text = stringResource(I18nR.string.update_data_syncing_title),
        color = palette.foreground,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
    )
    Spacer(Modifier.height(8.dp))
    Text(
        text = stringResource(I18nR.string.update_data_syncing_subtitle),
        color = palette.mutedForeground,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    )
    Spacer(Modifier.height(16.dp))
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, palette.border, RoundedCornerShape(16.dp))
            .padding(14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = activeLabel,
                color = palette.mutedForeground,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = "$progress%",
                color = palette.foreground,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.height(10.dp))
        LinearProgressIndicator(
            progress = { progress / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = palette.brand,
            trackColor = palette.mutedSurface,
        )
    }
    Spacer(Modifier.height(12.dp))
    val doneCount = (progress * SyncSteps.size / 100).coerceIn(0, SyncSteps.size)
    SyncSteps.forEachIndexed { index, step ->
        val state = when {
            progress >= 100 -> SyncStepState.Done
            index < doneCount -> SyncStepState.Done
            index == doneCount.coerceAtMost(SyncSteps.lastIndex) -> SyncStepState.Active
            else -> SyncStepState.Pending
        }
        SyncStepRow(
            label = stringResource(step.labelRes),
            state = state,
        )
        if (index < SyncSteps.lastIndex) Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun RotatingSyncIcon(
    tint: Color,
    modifier: Modifier = Modifier,
    size: Dp = 18.dp,
) {
    val transition = rememberInfiniteTransition(label = "update_data_sync_spin")
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = LinearEasing),
        ),
        label = "update_data_sync_rotation",
    )
    Icon(
        imageVector = Icons.Outlined.Sync,
        contentDescription = null,
        tint = tint,
        modifier = modifier
            .size(size)
            .graphicsLayer { rotationZ = rotation },
    )
}

@Composable
private fun CompleteContent() {
    val palette = LocalRestaurantPalette.current
    val releaseVersion = stringResource(I18nR.string.update_data_release_version)
    val successGreen = Color(0xFF008A05)
    val successBg = Color(0xFFE8F5E9)

    UpdateDataHeroIcon(badge = UpdateHeroBadge.ShieldCheck)
    Spacer(Modifier.height(12.dp))
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(successBg)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(
            imageVector = Icons.Filled.Check,
            contentDescription = null,
            tint = successGreen,
            modifier = Modifier.size(14.dp),
        )
        Text(
            text = stringResource(I18nR.string.update_data_complete_version, releaseVersion),
            color = successGreen,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
    Spacer(Modifier.height(14.dp))
    Text(
        text = stringResource(I18nR.string.update_data_complete_title),
        color = palette.foreground,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
    )
    Spacer(Modifier.height(8.dp))
    Text(
        text = stringResource(I18nR.string.update_data_complete_subtitle),
        color = palette.mutedForeground,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    )
    Spacer(Modifier.height(20.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(palette.mutedSurface),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(successGreen),
        )
    }
}

@Composable
private fun UpdateDataHeroIcon(badge: UpdateHeroBadge = UpdateHeroBadge.Sync) {
    val palette = LocalRestaurantPalette.current
    val successGreen = Color(0xFF008A05)
    Box {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(palette.brand),
            contentAlignment = Alignment.Center,
        ) {
            TonightLogoMark(modifier = Modifier.size(34.dp), color = Color.White)
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 4.dp, y = 4.dp)
                .size(22.dp)
                .clip(CircleShape)
                .background(palette.cardSurface)
                .border(2.dp, palette.cardSurface, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(palette.brandSoftSurface),
                contentAlignment = Alignment.Center,
            ) {
                when (badge) {
                    UpdateHeroBadge.SyncSpinning -> RotatingSyncIcon(tint = palette.brand, size = 11.dp)
                    UpdateHeroBadge.ShieldCheck -> Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(successGreen),
                        contentAlignment = Alignment.Center,
                    ) {
                        LucideIcon(
                            paths = LucidePaths.ShieldCheck,
                            strokeColor = Color.White,
                            modifier = Modifier.size(11.dp),
                        )
                    }
                    UpdateHeroBadge.Sync -> Icon(
                        imageVector = Icons.Outlined.Sync,
                        contentDescription = null,
                        tint = palette.brand,
                        modifier = Modifier.size(11.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun WhatsNewCard(
    releaseVersion: String,
    features: List<UpdateFeatureItem>,
    maxCardHeight: Dp,
) {
    val palette = LocalRestaurantPalette.current
    val density = LocalDensity.current
    val featureListScroll = rememberScrollState()
    val collapseRangePx = with(density) { WhatsNewTitleCollapseRange.toPx().coerceAtLeast(1f) }
    val collapseProgress = (featureListScroll.value / collapseRangePx).coerceIn(0f, 1f)
    val titleText = stringResource(I18nR.string.update_data_whats_new, releaseVersion)

    val maxListByCard = (
        maxCardHeight -
            UpdateModalCardPadding -
            UpdateModalPromptHeaderEstimate -
            UpdateModalPromptFooterEstimate
        ).coerceAtLeast(120.dp)
    val featureGap = 10.dp
    val maxListByRows =
        UpdateModalFeatureRowHeight * UpdateModalMaxVisibleFeatures +
            featureGap * (UpdateModalMaxVisibleFeatures - 1)
    val featureListMaxHeight = minOf(maxListByCard, maxListByRows)
    val shouldScrollFeatures = features.size > UpdateModalMaxVisibleFeatures

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, palette.border, RoundedCornerShape(16.dp))
            .padding(start = 14.dp, top = 14.dp, end = 14.dp),
    ) {
        WhatsNewCollapsingTitle(
            title = titleText,
            collapseProgress = collapseProgress,
        )
        Column(
            modifier = Modifier
                .then(
                    if (shouldScrollFeatures) {
                        Modifier
                            .heightIn(max = featureListMaxHeight)
                            .verticalScroll(featureListScroll)
                    } else {
                        Modifier
                    },
                )
                .padding(
                    top = WhatsNewFeatureListEdgePadding,
                    bottom = WhatsNewFeatureListEdgePadding,
                ),
            verticalArrangement = Arrangement.spacedBy(featureGap),
        ) {
            features.forEach { item ->
                FeatureRow(
                    title = stringResource(item.titleRes),
                    body = stringResource(item.bodyRes),
                )
            }
        }
    }
}

@Composable
private fun WhatsNewCollapsingTitle(
    title: String,
    collapseProgress: Float,
) {
    val palette = LocalRestaurantPalette.current
    val borderAlpha = (collapseProgress * 0.45f).coerceAtLeast(0.35f)
    val titleFontSp = lerp(WhatsNewTitleFontExpandedSp, WhatsNewTitleFontCollapsedSp, collapseProgress)
    val titleLineHeightSp = lerp(WhatsNewTitleLineExpandedSp, WhatsNewTitleLineCollapsedSp, collapseProgress)
    val iconSize = lerp(20.dp, 18.dp, collapseProgress)

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.AutoAwesome,
                contentDescription = null,
                tint = palette.brand,
                modifier = Modifier.size(iconSize),
            )
            Text(
                text = title,
                color = palette.foreground,
                fontSize = titleFontSp.sp,
                lineHeight = titleLineHeightSp.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
            )
        }
        HorizontalDivider(
            modifier = Modifier.padding(top = WhatsNewTitleToDividerGap),
            color = palette.border,
        )
    }
}

@Composable
private fun FeatureRow(
    title: String,
    body: String,
) {
    val palette = LocalRestaurantPalette.current
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(palette.brandSoftSurface),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(DsR.drawable.ic_update_data_feature),
                contentDescription = null,
                tint = palette.brand,
                modifier = Modifier.size(16.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = palette.foreground, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(2.dp))
            Text(body, color = palette.mutedForeground, fontSize = 12.sp, lineHeight = 16.sp)
        }
    }
}

@Composable
private fun SyncStepRow(label: String, state: SyncStepState) {
    val palette = LocalRestaurantPalette.current
    val successGreen = Color(0xFF008A05)
    val successBg = Color(0xFFE8F5E9)
    val activeBg = palette.brandSoftSurface

    val bg: Color
    val iconBg: Color
    val iconTint: Color
    val textColor: Color
    when (state) {
        SyncStepState.Done -> {
            bg = successBg
            iconBg = successGreen
            iconTint = Color.White
            textColor = successGreen
        }
        SyncStepState.Active -> {
            bg = activeBg
            iconBg = palette.brand
            iconTint = Color.White
            textColor = palette.brand
        }
        SyncStepState.Pending -> {
            bg = palette.mutedSurface.copy(alpha = 0.5f)
            iconBg = palette.border
            iconTint = palette.mutedForeground
            textColor = palette.mutedForeground
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(bg)
            .padding(horizontal = 12.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(iconBg),
            contentAlignment = Alignment.Center,
        ) {
            when (state) {
                SyncStepState.Done -> Icon(
                    Icons.Filled.Check,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(14.dp),
                )
                SyncStepState.Active -> Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(iconTint),
                )
                SyncStepState.Pending -> Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(iconTint.copy(alpha = 0.5f)),
                )
            }
        }
        Text(
            text = label,
            color = textColor,
            fontSize = 13.sp,
            fontWeight = if (state == SyncStepState.Active) FontWeight.SemiBold else FontWeight.Medium,
            modifier = Modifier.weight(1f),
        )
        if (state == SyncStepState.Active) {
            RotatingSyncIcon(tint = palette.brand, size = 18.dp)
        }
    }
}
