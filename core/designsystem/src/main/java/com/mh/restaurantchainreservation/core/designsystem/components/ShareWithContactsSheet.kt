package com.mh.restaurantchainreservation.core.designsystem.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColors
import com.mh.restaurantchainreservation.core.model.ShareContact
import com.mh.restaurantchainreservation.core.model.ShareContacts
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class ShareContactTab { Available, Selected }

private val ShareContactTabBarHeight = 50.dp
private val ShareContactTabTopPadding = 4.dp
private val ShareContactTabActiveIndicatorHeight = 3.dp
private val ShareContactTabLabelToIndicatorGap = 10.dp

private data class ShareContactTabSpec(
    val tab: ShareContactTab,
    val icon: ImageVector,
    val label: String,
)

private val ShareContactTabs = listOf(
    ShareContactTabSpec(ShareContactTab.Available, Icons.Outlined.PersonAdd, "Contacts"),
    ShareContactTabSpec(ShareContactTab.Selected, Icons.Filled.Check, "Selected"),
)

/**
 * Contact-picker bottom sheet for in-app sharing (no system link chooser).
 * Uses [RestaurantModalBottomSheet] for the same rising dialog treatment as
 * order receipt, wishlist, and credit-card modals.
 */
@Composable
fun ShareWithContactsSheet(
    subtitle: String,
    onDismiss: () -> Unit,
    onShare: (Set<String>) -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Share with friends",
    contacts: List<ShareContact> = ShareContacts.all,
) {
    val palette = LocalRestaurantPalette.current
    var query by remember { mutableStateOf("") }
    var tab by remember { mutableStateOf(ShareContactTab.Available) }
    var selected by remember { mutableStateOf(emptySet<String>()) }
    var sent by remember { mutableStateOf(false) }
    var sentCount by remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()
    var isProgrammaticTabChange by remember { mutableStateOf(false) }
    val pagerState = rememberPagerState(
        initialPage = ShareContactTabs.indexOfFirst { it.tab == tab }.coerceAtLeast(0),
        pageCount = { ShareContactTabs.size },
    )

    LaunchedEffect(tab) {
        val page = ShareContactTabs.indexOfFirst { it.tab == tab }.coerceAtLeast(0)
        if (pagerState.currentPage != page) {
            isProgrammaticTabChange = true
            pagerState.animateScrollToPage(page)
            isProgrammaticTabChange = false
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow { pagerState.settledPage }.collect { page ->
            if (isProgrammaticTabChange) return@collect
            val nextTab = ShareContactTabs[page].tab
            if (tab != nextTab) {
                tab = nextTab
            }
        }
    }

    LaunchedEffect(sent) {
        if (sent) {
            delay(1500L)
            onDismiss()
        }
    }

    val (available, selectedList) = remember(query, selected, contacts) {
        val needle = query.trim().lowercase()
        val matches = contacts.filter { needle.isEmpty() || it.name.lowercase().contains(needle) }
        matches.partition { it.id !in selected }
    }

    RestaurantModalBottomSheet(onDismissRequest = onDismiss, modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
        ) {
            Text(
                text = title,
                color = palette.foreground,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            if (subtitle.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    color = palette.mutedForeground,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            if (sent) {
                Spacer(Modifier.height(20.dp))
                ShareSuccessState(count = sentCount)
                return@Column
            }

            Spacer(Modifier.height(10.dp))
            ShareSearchField(value = query, onChange = { query = it })
            Spacer(Modifier.height(12.dp))
            ShareContactTabBar(
                selected = tab,
                availableCount = available.size,
                selectedCount = selectedList.size,
                onSelect = { newTab ->
                    tab = newTab
                    coroutineScope.launch {
                        val page = ShareContactTabs.indexOfFirst { it.tab == newTab }.coerceAtLeast(0)
                        if (pagerState.currentPage != page) {
                            isProgrammaticTabChange = true
                            pagerState.animateScrollToPage(page)
                            isProgrammaticTabChange = false
                        }
                    }
                },
            )
            Spacer(Modifier.height(12.dp))

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                beyondViewportPageCount = 1,
            ) { page ->
                val pageTab = ShareContactTabs[page].tab
                val visibleList = if (pageTab == ShareContactTab.Available) available else selectedList
                ShareContactTabPage(
                    tab = pageTab,
                    query = query,
                    selectedCount = selected.size,
                    contacts = visibleList,
                    selectedIds = selected,
                    onToggleContact = { contactId ->
                        selected = if (contactId in selected) {
                            selected - contactId
                        } else {
                            selected + contactId
                        }
                    },
                )
            }

            Spacer(Modifier.height(16.dp))
            val ctaLabel = when {
                selected.isEmpty() -> "Select contacts"
                else -> "Share with ${selected.size} friend${if (selected.size == 1) "" else "s"}"
            }
            SharePrimaryButton(
                label = ctaLabel,
                enabled = selected.isNotEmpty(),
                onClick = {
                    sentCount = selected.size
                    onShare(selected)
                    sent = true
                },
            )
        }
    }
}

@Composable
private fun ShareSearchField(value: String, onChange: (String) -> Unit) {
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
                Text("Search contacts", color = palette.mutedForeground, fontSize = 14.sp)
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
                Icon(Icons.Filled.Close, null, tint = palette.mutedForeground, modifier = Modifier.size(12.dp))
            }
        }
    }
}

@Composable
private fun ShareContactTabBar(
    selected: ShareContactTab,
    availableCount: Int,
    selectedCount: Int,
    onSelect: (ShareContactTab) -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val density = LocalDensity.current
    val strokePx = with(density) { 1.dp.toPx() }
    val counts = mapOf(
        ShareContactTab.Available to availableCount,
        ShareContactTab.Selected to selectedCount,
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = ShareContactTabTopPadding)
            .graphicsLayer { clip = false },
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(ShareContactTabBarHeight)
                .drawBehind {
                    val y = size.height - strokePx * 0.5f
                    drawLine(
                        color = palette.border,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = strokePx,
                    )
                },
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom,
            ) {
                ShareContactTabs.forEach { spec ->
                    ShareContactTabItem(
                        modifier = Modifier.weight(1f),
                        icon = spec.icon,
                        label = spec.label,
                        count = counts[spec.tab] ?: 0,
                        active = spec.tab == selected,
                        onClick = { onSelect(spec.tab) },
                    )
                }
            }
        }
    }
}

@Composable
private fun ShareContactTabItem(
    icon: ImageVector,
    label: String,
    count: Int,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    val interactionSource = remember { MutableInteractionSource() }
    val contentColor by animateColorAsState(
        targetValue = if (active) palette.brand else palette.mutedForeground,
        animationSpec = spring(stiffness = 520f, dampingRatio = 0.85f),
        label = "share_tab_content",
    )
    val indicatorScale by animateFloatAsState(
        targetValue = if (active) 1f else 0f,
        animationSpec = spring(stiffness = 500f, dampingRatio = 0.8f),
        label = "share_tab_indicator_scale",
    )

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Tab,
                onClick = onClick,
            ),
    ) {
        Column(
            modifier = Modifier.align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                TabSelectionBounceBox(isActive = active) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = contentColor,
                            modifier = Modifier.size(16.dp),
                        )
                        Text(
                            text = label,
                            color = contentColor,
                            fontSize = 14.sp,
                            fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                            maxLines = 1,
                        )
                        ShareContactTabCountBadge(
                            count = count,
                            active = active,
                        )
                    }
                }
            }
            Spacer(Modifier.height(ShareContactTabLabelToIndicatorGap))
            Box(
                modifier = Modifier
                    .width(70.dp)
                    .height(ShareContactTabActiveIndicatorHeight)
                    .graphicsLayer {
                        scaleX = indicatorScale
                        alpha = indicatorScale
                    }
                    .clip(RoundedCornerShape(999.dp))
                    .background(palette.brand),
            )
        }
    }
}

@Composable
private fun ShareContactTabCountBadge(
    count: Int,
    active: Boolean,
) {
    val palette = LocalRestaurantPalette.current
    val background = if (active) palette.brand.copy(alpha = 0.12f) else palette.mutedSurface
    val textColor = if (active) palette.brand else palette.mutedForeground

    Box(
        modifier = Modifier
            .defaultMinSize(minWidth = 20.dp)
            .height(20.dp)
            .clip(CircleShape)
            .background(background)
            .padding(horizontal = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = count.toString(),
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
        )
    }
}

@Composable
private fun ShareContactTabPage(
    tab: ShareContactTab,
    query: String,
    selectedCount: Int,
    contacts: List<ShareContact>,
    selectedIds: Set<String>,
    onToggleContact: (String) -> Unit,
) {
    val palette = LocalRestaurantPalette.current

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = if (tab == ShareContactTab.Selected) "Selected contacts" else "Available contacts",
                color = palette.foreground,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                text = "$selectedCount selected",
                color = palette.mutedForeground,
                fontSize = 12.sp,
            )
        }
        Spacer(Modifier.height(8.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 0.dp, max = 320.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (contacts.isEmpty()) {
                ShareEmptyContactsState(query = query, tab = tab)
            } else {
                contacts.forEach { contact ->
                    ShareContactRow(
                        contact = contact,
                        isSelected = contact.id in selectedIds,
                        onToggle = { onToggleContact(contact.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun ShareContactRow(
    contact: ShareContact,
    isSelected: Boolean,
    onToggle: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(20.dp)
    val checkScale by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = spring(stiffness = 420f, dampingRatio = 0.55f),
        label = "share_check",
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (isSelected) palette.brand.copy(alpha = 0.08f) else palette.cardSurface)
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
        ShareAvatarBubble(name = contact.name, initials = contact.initials)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = contact.name,
                color = palette.foreground,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                text = when {
                    isSelected && contact.handle.isNotBlank() -> "Ready to share"
                    contact.handle.isNotBlank() -> contact.handle
                    isSelected -> "Ready to share"
                    else -> "Tap to select"
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
                    tint = RestaurantColors.Base.white,
                    modifier = Modifier.size(16.dp).scale(checkScale),
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
private fun ShareAvatarBubble(name: String, initials: String) {
    val hue = (name.hashCode() and 0x7FFFFFFF) % 360
    val color = Color.hsv(hue = hue.toFloat(), saturation = 0.55f, value = 0.85f)
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initials,
            color = RestaurantColors.Base.white,
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
        )
    }
}

@Composable
private fun ShareEmptyContactsState(query: String, tab: ShareContactTab) {
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
                query.isNotBlank() -> "No contacts match your search"
                tab == ShareContactTab.Selected -> "No contacts selected yet"
                else -> "All contacts are already selected"
            },
            color = palette.foreground,
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
        )
    }
}

@Composable
private fun ShareSuccessState(count: Int) {
    val palette = LocalRestaurantPalette.current
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(stiffness = 380f, dampingRatio = 0.5f),
        label = "share_success",
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
            text = "Shared with $count friend${if (count == 1) "" else "s"}",
            color = palette.foreground,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "They'll see it in their Wishlist shared section.",
            color = palette.mutedForeground,
            fontSize = 13.sp,
        )
    }
}

@Composable
private fun SharePrimaryButton(label: String, enabled: Boolean, onClick: () -> Unit) {
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
            tint = if (enabled) RestaurantColors.Base.white else palette.mutedForeground,
            modifier = Modifier.size(16.dp),
        )
        Spacer(Modifier.size(8.dp))
        Text(
            text = label,
            color = if (enabled) RestaurantColors.Base.white else palette.mutedForeground,
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold,
        )
    }
}
