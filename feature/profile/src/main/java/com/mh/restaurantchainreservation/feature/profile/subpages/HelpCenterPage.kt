package com.mh.restaurantchainreservation.feature.profile.subpages

import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColors
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.Accessibility
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Login
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import kotlinx.coroutines.launch

private enum class TipTone { Info, Warn, Success }

private data class HelpVideo(val title: String, val length: String)

private data class HelpSection(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val summary: String,
    val keywords: String,
    val image: String,
    val readMins: Int,
    val related: List<String>,
    val video: HelpVideo? = null,
    val render: @Composable (jumpTo: (String) -> Unit) -> Unit,
)

private data class HelpFaq(val question: String, val answer: String)

@Composable
fun HelpCenterPage(
    onBack: () -> Unit,
    onContactSupport: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    var query by rememberSaveable { mutableStateOf("") }
    var expandedFaq by rememberSaveable { mutableIntStateOf(0) }
    var activeTopicId by rememberSaveable { mutableStateOf<String?>(null) }

    val sections = remember { buildHelpSections() }
    val faqs = remember { buildHelpFaqs() }

    val activeTopic = activeTopicId?.let { id -> sections.firstOrNull { it.id == id } }

    val scrollState = rememberScrollState()
    val showTop by remember(scrollState) {
        derivedStateOf { scrollState.value > 1200 }
    }
    val scope = androidx.compose.runtime.rememberCoroutineScope()

    LaunchedEffect(activeTopicId) {
        scrollState.scrollTo(0)
    }

    val filtered = remember(query, sections) {
        val term = query.trim().lowercase()
        val base = if (term.isEmpty()) {
            sections
        } else {
            sections.filter {
                it.title.lowercase().contains(term) ||
                    it.summary.lowercase().contains(term) ||
                    it.keywords.lowercase().contains(term)
            }
        }
        val pinned = base.filter { it.id == "help-usage" }
        val rest = base.filter { it.id != "help-usage" }
        pinned + rest
    }

    val jumpTo: (String) -> Unit = { id -> activeTopicId = id }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(palette.pageBackground)
            .statusBarsPadding(),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            HelpCenterHeader(
                title = activeTopic?.title ?: "Help center",
                subtitle = activeTopic?.summary ?: "Answers for bookings, payments, and profile",
                onBack = {
                    if (activeTopicId != null) activeTopicId = null else onBack()
                },
            )

            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                ) {
                    AnimatedContent(
                        targetState = activeTopic,
                        transitionSpec = {
                            (fadeIn(tween(220)) + slideInVertically(tween(220)) { it / 8 })
                                .togetherWith(fadeOut(tween(160)) + slideOutVertically(tween(160)) { -it / 8 })
                        },
                        label = "help-page-content",
                    ) { topic ->
                        if (topic == null) {
                            Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                                HelpHeroCard(query = query, onQueryChange = { query = it }, sections = sections, onJump = jumpTo)
                                QuickStartGrid(onJump = jumpTo)
                                AllTopicsList(filtered = filtered, query = query, onJump = jumpTo)
                                FaqList(
                                    faqs = faqs,
                                    expandedIndex = expandedFaq,
                                    onToggle = { idx -> expandedFaq = if (expandedFaq == idx) -1 else idx },
                                )
                                StillNeedAHandFooter(onContactSupport = onContactSupport)
                                Text(
                                    text = "Guide version 1.2 \u00B7 Updated April 2026",
                                    color = palette.mutedForeground,
                                    fontSize = 11.sp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp, bottom = 24.dp),
                                    textAlign = TextAlign.Center,
                                )
                            }
                        } else {
                            HelpTopicView(
                                section = topic,
                                sections = sections,
                                onJump = jumpTo,
                                onContactSupport = onContactSupport,
                                onGoIndex = { activeTopicId = null },
                            )
                            Spacer(Modifier.height(24.dp))
                        }
                    }
                }

                if (showTop) {
                    val palette2 = LocalRestaurantPalette.current
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 20.dp, bottom = 24.dp)
                            .size(44.dp)
                            .shadow(8.dp, CircleShape, clip = false)
                            .clip(CircleShape)
                            .background(palette2.brand)
                            .clickable { scope.launch { scrollState.animateScrollTo(0) } },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowUpward,
                            contentDescription = "Back to top",
                            tint = RestaurantColors.Base.white,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HelpCenterHeader(title: String, subtitle: String, onBack: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(palette.cardSurface.copy(alpha = 0.95f)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
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
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = palette.foreground,
                    modifier = Modifier.size(18.dp),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = palette.foreground,
                    fontSize = 18.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = subtitle,
                    color = palette.mutedForeground,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(palette.border),
        )
    }
}

@Composable
private fun HelpHeroCard(
    query: String,
    onQueryChange: (String) -> Unit,
    sections: List<HelpSection>,
    onJump: (String) -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val cardShape = RoundedCornerShape(28.dp)

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
                .clip(cardShape)
                .border(1.dp, palette.border, cardShape)
                .background(palette.cardSurface),
        ) {
            AsyncImage(
                model = "https://images.unsplash.com/photo-1559339352-11d035aa65de?w=1200&h=720&fit=crop",
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                RestaurantColors.Overlay.borderSubtle,
                                RestaurantColors.Shadow.cardAmbient,
                                RestaurantColors.Base.blackAlpha(0.70f),
                            ),
                        ),
                    ),
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom,
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(percent = 50))
                        .background(palette.brand.copy(alpha = 0.18f))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text(
                        text = "Help center",
                        color = RestaurantColors.Base.white,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "What do you need tonight?",
                    color = RestaurantColors.Base.white,
                    fontSize = 26.sp,
                    lineHeight = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Fast answers for bookings, QR Pay, saved places, and account settings.",
                    color = RestaurantColors.Overlay.textOnImageMuted,
                    fontSize = 13.sp,
                    lineHeight = 17.sp,
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, palette.border, RoundedCornerShape(24.dp))
                .background(palette.cardSurface)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            HelpSearchInput(value = query, onValueChange = onQueryChange)
            val quickIds = listOf("book", "qrpay", "saved", "troubleshoot")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                quickIds.forEach { id ->
                    val s = sections.firstOrNull { it.id == id } ?: return@forEach
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(percent = 50))
                            .background(palette.brand.copy(alpha = 0.10f))
                            .border(1.dp, palette.brand.copy(alpha = 0.20f), RoundedCornerShape(percent = 50))
                            .clickable { onJump(id) }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(
                            imageVector = s.icon,
                            contentDescription = null,
                            tint = palette.brand,
                            modifier = Modifier.size(13.dp),
                        )
                        Text(
                            text = s.title,
                            color = palette.brand,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HelpSearchInput(value: String, onValueChange: (String) -> Unit) {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(14.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(palette.cardSurface)
            .border(1.dp, palette.border, shape)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(Icons.Outlined.Search, null, tint = palette.mutedForeground, modifier = Modifier.size(16.dp))
        Box(modifier = Modifier.weight(1f)) {
            if (value.isEmpty()) {
                Text("Search help\u2026 e.g. booking, heart, QR", color = palette.mutedForeground, fontSize = 13.sp)
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                cursorBrush = SolidColor(palette.brand),
                textStyle = LocalTextStyle.current.merge(TextStyle(color = palette.foreground, fontSize = 13.sp)),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

private data class QuickStartItem(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val meta: String,
)

@Composable
private fun QuickStartGrid(onJump: (String) -> Unit) {
    val palette = LocalRestaurantPalette.current
    val items = listOf(
        QuickStartItem("getting-started", "First steps", Icons.Outlined.AutoAwesome, "2 min"),
        QuickStartItem("book", "Book a table", Icons.Outlined.CalendarMonth, "5 min"),
        QuickStartItem("policy", "Deposits", Icons.Outlined.Shield, "Rules"),
        QuickStartItem("qrpay", "QR Pay", Icons.Outlined.QrCode, "Scan"),
    )
    Column {
        Text(
            text = "START HERE",
            color = palette.mutedForeground,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            items.subList(0, 2).forEach { item ->
                QuickStartCard(item = item, modifier = Modifier.weight(1f), onClick = { onJump(item.id) })
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            items.subList(2, 4).forEach { item ->
                QuickStartCard(item = item, modifier = Modifier.weight(1f), onClick = { onJump(item.id) })
            }
        }
    }
}

@Composable
private fun QuickStartCard(
    item: QuickStartItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(20.dp)
    Row(
        modifier = modifier
            .heightIn(min = 84.dp)
            .clip(shape)
            .border(1.dp, palette.border, shape)
            .background(palette.cardSurface)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(palette.brand.copy(alpha = 0.10f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(item.icon, null, tint = palette.brand, modifier = Modifier.size(18.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                item.label,
                color = palette.foreground,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(2.dp))
            Text(item.meta, color = palette.mutedForeground, fontSize = 11.sp)
        }
    }
}

@Composable
private fun AllTopicsList(filtered: List<HelpSection>, query: String, onJump: (String) -> Unit) {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(20.dp)
    Column {
        Text(
            text = "ALL TOPICS",
            color = palette.mutedForeground,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .border(1.dp, palette.border, shape)
                .background(palette.cardSurface),
        ) {
            if (filtered.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "No topics match \"$query\". Try another word.",
                        color = palette.mutedForeground,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                    )
                }
            } else {
                filtered.forEachIndexed { i, section ->
                    if (i > 0) {
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(palette.border))
                    }
                    TopicRow(section = section, onClick = { onJump(section.id) })
                }
            }
        }
    }
}

@Composable
private fun TopicRow(section: HelpSection, onClick: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
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
            Icon(section.icon, null, tint = palette.foreground, modifier = Modifier.size(18.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = section.title,
                    color = palette.foreground,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(percent = 50))
                        .background(palette.brand.copy(alpha = 0.08f))
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                ) {
                    Icon(Icons.Outlined.Schedule, null, tint = palette.brand, modifier = Modifier.size(11.dp))
                    Text("${section.readMins}m", color = palette.brand, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            Text(
                text = section.summary,
                color = palette.mutedForeground,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
        Icon(Icons.Filled.ChevronRight, null, tint = palette.mutedForeground, modifier = Modifier.size(16.dp))
    }
}

@Composable
private fun FaqList(
    faqs: List<HelpFaq>,
    expandedIndex: Int,
    onToggle: (Int) -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(24.dp)
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(start = 4.dp, bottom = 10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(palette.brand.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.HelpOutline, null, tint = palette.brand, modifier = Modifier.size(18.dp))
            }
            Column {
                Text("Frequently asked", color = palette.foreground, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                Text("Short answers before you chat with us.", color = palette.mutedForeground, fontSize = 12.sp)
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .border(1.dp, palette.border, shape)
                .background(palette.cardSurface),
        ) {
            faqs.forEachIndexed { idx, faq ->
                if (idx > 0) {
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(palette.border))
                }
                FaqRow(
                    question = faq.question,
                    answer = faq.answer,
                    expanded = expandedIndex == idx,
                    onToggle = { onToggle(idx) },
                )
            }
        }
    }
}

@Composable
private fun FaqRow(question: String, answer: String, expanded: Boolean, onToggle: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            Text(
                text = question,
                color = palette.foreground,
                fontSize = 14.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
            )
            Icon(
                imageVector = Icons.Outlined.ExpandMore,
                contentDescription = null,
                tint = palette.mutedForeground,
                modifier = Modifier
                    .size(18.dp)
                    .rotate(if (expanded) 180f else 0f),
            )
        }
        AnimatedVisibility(visible = expanded) {
            Text(
                text = answer,
                color = palette.mutedForeground,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            )
        }
    }
}

@Composable
private fun StillNeedAHandFooter(onContactSupport: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(28.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.dp, palette.brand.copy(alpha = 0.20f), shape)
            .background(palette.brand.copy(alpha = 0.08f))
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .shadow(8.dp, CircleShape, clip = false)
                    .clip(CircleShape)
                    .background(palette.brand),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.ChatBubbleOutline, null, tint = RestaurantColors.Base.white, modifier = Modifier.size(20.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Still need a hand?", color = palette.foreground, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                Text(
                    text = "Support can help with bookings, billing, and account questions.",
                    color = palette.mutedForeground,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 44.dp)
                    .clip(RoundedCornerShape(percent = 50))
                    .background(palette.brand)
                    .clickable(onClick = onContactSupport)
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Outlined.ChatBubbleOutline, null, tint = RestaurantColors.Base.white, modifier = Modifier.size(15.dp))
                    Text("Chat", color = RestaurantColors.Base.white, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 44.dp)
                    .clip(RoundedCornerShape(percent = 50))
                    .border(1.dp, palette.brand, RoundedCornerShape(percent = 50))
                    .clickable(onClick = onContactSupport)
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Outlined.Email, null, tint = palette.brand, modifier = Modifier.size(15.dp))
                    Text("Email", color = palette.brand, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Submit a request",
            color = palette.brand,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable(onClick = onContactSupport)
                .padding(vertical = 4.dp),
        )
    }
}

@Composable
private fun HelpTopicView(
    section: HelpSection,
    sections: List<HelpSection>,
    onJump: (String) -> Unit,
    onContactSupport: () -> Unit,
    onGoIndex: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        // Hero image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(3f)
                .clip(RoundedCornerShape(28.dp)),
        ) {
            AsyncImage(
                model = section.image,
                contentDescription = section.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(RestaurantColors.Base.blackAlpha(0.15f), RestaurantColors.Base.blackAlpha(0.65f)),
                        ),
                    ),
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(percent = 50))
                            .background(RestaurantColors.Overlay.imageCaption)
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(section.icon, null, tint = palette.brand, modifier = Modifier.size(11.dp))
                            Text(
                                section.title,
                                color = palette.brand,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(percent = 50))
                            .background(RestaurantColors.Overlay.scrimHeavy)
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Outlined.Schedule, null, tint = RestaurantColors.Base.white, modifier = Modifier.size(11.dp))
                            Text("${section.readMins} min read", color = RestaurantColors.Base.white, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    section.video?.let { v ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(percent = 50))
                                .background(RestaurantColors.Overlay.scrimHeavy)
                                .padding(horizontal = 8.dp, vertical = 3.dp),
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Outlined.PlayArrow, null, tint = RestaurantColors.Base.white, modifier = Modifier.size(11.dp))
                                Text("Video \u00B7 ${v.length}", color = RestaurantColors.Base.white, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = section.title,
                    color = RestaurantColors.Base.white,
                    fontSize = 22.sp,
                    lineHeight = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
                Text(
                    text = section.summary,
                    color = RestaurantColors.Overlay.textOnImageMuted,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(RestaurantColors.Base.blackAlpha(0.4f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.VolumeUp, null, tint = RestaurantColors.Base.white, modifier = Modifier.size(18.dp))
            }
        }

        // Body card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, palette.border, RoundedCornerShape(24.dp))
                .background(palette.cardSurface)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            section.render(onJump)

            section.video?.let { video ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, palette.border, RoundedCornerShape(16.dp))
                        .background(palette.mutedSurface.copy(alpha = 0.5f))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(palette.brand.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Outlined.PlayArrow, null, tint = palette.brand, modifier = Modifier.size(18.dp))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(video.title, color = palette.foreground, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Text(
                            "Video walkthrough \u00B7 ${video.length}",
                            color = palette.mutedForeground,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 2.dp),
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(percent = 50))
                            .border(1.dp, palette.border, RoundedCornerShape(percent = 50))
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                    ) {
                        Text("Watch", color = palette.foreground, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            if (section.related.isNotEmpty()) {
                Column {
                    Text(
                        text = "RELATED",
                        color = palette.mutedForeground,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        section.related.forEach { relId ->
                            val rel = sections.firstOrNull { it.id == relId } ?: return@forEach
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(percent = 50))
                                    .background(palette.brand.copy(alpha = 0.10f))
                                    .clickable { onJump(relId) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                            ) {
                                Icon(rel.icon, null, tint = palette.brand, modifier = Modifier.size(12.dp))
                                Text(rel.title, color = palette.brand, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(palette.border))
            HelpfulnessRow()
        }

        // Footer buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 44.dp)
                    .clip(RoundedCornerShape(percent = 50))
                    .border(1.dp, palette.border, RoundedCornerShape(percent = 50))
                    .clickable(onClick = onGoIndex)
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = palette.foreground, modifier = Modifier.size(14.dp))
                    Text("All topics", color = palette.foreground, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 44.dp)
                    .clip(RoundedCornerShape(percent = 50))
                    .background(palette.brand)
                    .clickable(onClick = onContactSupport)
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Outlined.ChatBubbleOutline, null, tint = RestaurantColors.Base.white, modifier = Modifier.size(14.dp))
                    Text("Contact support", color = RestaurantColors.Base.white, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}

@Composable
private fun HelpfulnessRow() {
    val palette = LocalRestaurantPalette.current
    var feedback by remember { mutableStateOf<Boolean?>(null) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text("Was this helpful?", color = palette.mutedForeground, fontSize = 13.sp)
        when (val v = feedback) {
            true -> Text(
                "Thanks for the feedback!",
                color = palette.success,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
            )
            false -> Text(
                "Thanks \u2014 we'll make this clearer.",
                color = palette.mutedForeground,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
            )
            null -> {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(percent = 50))
                            .border(1.dp, palette.border, RoundedCornerShape(percent = 50))
                            .clickable { feedback = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Outlined.ThumbUp, null, tint = palette.foreground, modifier = Modifier.size(13.dp))
                            Text("Yes", color = palette.foreground, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(percent = 50))
                            .border(1.dp, palette.border, RoundedCornerShape(percent = 50))
                            .clickable { feedback = false }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Outlined.ThumbDown, null, tint = palette.foreground, modifier = Modifier.size(13.dp))
                            Text("No", color = palette.foreground, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

// === Help content primitives ===

@Composable
private fun HelpParagraph(text: String) {
    val palette = LocalRestaurantPalette.current
    Text(text = text, color = palette.foreground, fontSize = 14.sp, lineHeight = 21.sp)
}

@Composable
private fun HelpStep(n: Int, content: @Composable () -> Unit) {
    val palette = LocalRestaurantPalette.current
    Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(palette.brand.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(n.toString(), color = palette.brand, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        Box(modifier = Modifier.weight(1f).padding(top = 4.dp)) {
            content()
        }
    }
}

@Composable
private fun HelpInlineLink(label: String, target: String, onJump: (String) -> Unit) {
    val palette = LocalRestaurantPalette.current
    Text(
        text = label,
        color = palette.brand,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        textDecoration = TextDecoration.Underline,
        modifier = Modifier
            .clickable { onJump(target) }
            .padding(vertical = 1.dp),
    )
}

@Composable
private fun HelpTip(
    tone: TipTone = TipTone.Info,
    icon: ImageVector = Icons.Outlined.Lightbulb,
    text: String,
) {
    val palette = LocalRestaurantPalette.current
    val (bg, fg) = when (tone) {
        TipTone.Info -> palette.brand.copy(alpha = 0.10f) to palette.brand
        TipTone.Warn -> palette.warning.copy(alpha = 0.12f) to palette.warning
        TipTone.Success -> palette.success.copy(alpha = 0.12f) to palette.success
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(bg)
            .padding(14.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(icon, null, tint = fg, modifier = Modifier.size(18.dp).padding(top = 1.dp))
        Text(text = text, color = palette.foreground, fontSize = 13.sp, lineHeight = 18.sp, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun HelpSubheading(text: String) {
    val palette = LocalRestaurantPalette.current
    Text(text = text, color = palette.foreground, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
}

@Composable
private fun HelpInfoCard(
    icon: ImageVector,
    iconColor: Color,
    bg: Color,
    title: String,
    body: String,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, palette.border, RoundedCornerShape(20.dp))
            .background(bg)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(icon, null, tint = iconColor, modifier = Modifier.size(16.dp))
            Text(title, color = palette.foreground, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        Text(body, color = palette.mutedForeground, fontSize = 12.sp, lineHeight = 16.sp)
    }
}

// === Section content ===

private fun buildHelpSections(): List<HelpSection> = listOf(
    HelpSection(
        id = "getting-started",
        title = "Getting started",
        icon = Icons.Outlined.AutoAwesome,
        summary = "A friendly first look at CatchTable.",
        keywords = "start begin intro first time tutorial how",
        image = "https://images.unsplash.com/photo-1723744910051-da35a92321af?w=1200&h=600&fit=crop",
        readMins = 2,
        related = listOf("discover", "signin", "book"),
        video = HelpVideo("30-second tour", "0:32"),
        render = { jumpTo ->
            HelpParagraph(
                "Welcome! CatchTable helps you find great restaurants, book a table, pay with your phone, and save the places you love. You can look around without signing in. You only need an account when you want to save, book, or pay.",
            )
            Spacer(Modifier.height(12.dp))
            HelpStep(1) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Open the app. The first thing you see is the ", color = LocalRestaurantPalette.current.foreground, fontSize = 14.sp)
                    HelpInlineLink("Discover", "discover", jumpTo)
                    Text(" page.", color = LocalRestaurantPalette.current.foreground, fontSize = 14.sp)
                }
            }
            Spacer(Modifier.height(8.dp))
            HelpStep(2) { HelpParagraph("At the bottom of the screen are four big buttons: Discover, Wishlist, Dining, and Profile. In the middle is a round QR button.") }
            Spacer(Modifier.height(8.dp))
            HelpStep(3) { HelpParagraph("Tap any card or picture to see more about a restaurant or a food.") }
            Spacer(Modifier.height(8.dp))
            HelpStep(4) { HelpParagraph("When you're ready, tap the heart to save a place, or tap Book to reserve a table.") }
            Spacer(Modifier.height(12.dp))
            HelpTip(text = "New here? Try Discover first to browse, then learn how to book a table.")
        },
    ),
    HelpSection(
        id = "signin",
        title = "Sign in & accounts",
        icon = Icons.Outlined.Login,
        summary = "Why you sometimes need to sign in.",
        keywords = "login register sign up account profile password",
        image = "https://images.unsplash.com/photo-1633265486064-086b219458ec?w=1200&h=600&fit=crop",
        readMins = 2,
        related = listOf("saved", "book", "profile"),
        render = { _ ->
            HelpParagraph("Browsing Discover is always free — no sign-in required. You'll be asked to sign in only when you use features that need your personal data.")
            Spacer(Modifier.height(12.dp))
            val palette = LocalRestaurantPalette.current
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                HelpInfoCard(
                    icon = Icons.Outlined.CheckCircle,
                    iconColor = palette.success,
                    bg = palette.success.copy(alpha = 0.08f),
                    title = "No sign-in needed",
                    body = "\u2022 Browsing restaurants\n\u2022 Searching food or places\n\u2022 Reading menus & reviews",
                    modifier = Modifier.weight(1f),
                )
                HelpInfoCard(
                    icon = Icons.Outlined.Login,
                    iconColor = palette.brand,
                    bg = palette.brand.copy(alpha = 0.06f),
                    title = "Sign-in required",
                    body = "\u2022 Saving to your Wishlist\n\u2022 Booking a table\n\u2022 QR Pay\n\u2022 Dining & Profile tabs",
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(12.dp))
            HelpTip(
                tone = TipTone.Info,
                icon = Icons.Outlined.Shield,
                text = "When a feature needs you to sign in, a small pop-up appears. Tap Sign in to continue, or Not now to go back.",
            )
        },
    ),
    HelpSection(
        id = "discover",
        title = "Discover page",
        icon = Icons.Outlined.Home,
        summary = "Find restaurants, foods, and promotions.",
        keywords = "discover home search banners categories monthly best",
        image = "https://images.unsplash.com/photo-1552566626-52f8b828add9?w=1200&h=600&fit=crop",
        readMins = 3,
        related = listOf("saved", "book", "signin"),
        video = HelpVideo("Browsing Discover", "1:05"),
        render = { jumpTo ->
            HelpParagraph("The Discover page is the front door of the app. Scroll up and down to see featured places, cities, food types, monthly best picks, and more.")
            Spacer(Modifier.height(12.dp))
            HelpStep(1) { HelpParagraph("Search bar at the top — type any restaurant, food, or city name and tap the result.") }
            Spacer(Modifier.height(8.dp))
            HelpStep(2) { HelpParagraph("Big picture banners — drag left or right to see more. Tap the picture to open that collection. Tap View All in the corner to see every banner in a big gallery.") }
            Spacer(Modifier.height(8.dp))
            HelpStep(3) { HelpParagraph("Round category icons — like \"Korean\", \"Italian\", \"Dessert\". Tap any one to see restaurants in that category.") }
            Spacer(Modifier.height(8.dp))
            HelpStep(4) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Nearby Me & Local Favourite — these use your location, so they need ", color = LocalRestaurantPalette.current.foreground, fontSize = 14.sp)
                    HelpInlineLink("sign in", "signin", jumpTo)
                    Text(".", color = LocalRestaurantPalette.current.foreground, fontSize = 14.sp)
                }
            }
            Spacer(Modifier.height(8.dp))
            HelpStep(5) { HelpParagraph("Tap any restaurant card to see the menu, photos, reviews, and a Book button.") }
            Spacer(Modifier.height(12.dp))
            HelpTip(
                icon = Icons.Outlined.Favorite,
                text = "See a place you like? Tap the little heart on the picture to save it for later.",
            )
        },
    ),
    HelpSection(
        id = "wishlist",
        title = "Wishlist",
        icon = Icons.Outlined.FavoriteBorder,
        summary = "Keep saved restaurants and foods together.",
        keywords = "wishlist saved favorites heart bookmark",
        image = "https://images.unsplash.com/photo-1569336415962-a4bd9f69cd83?w=1200&h=600&fit=crop",
        readMins = 2,
        related = listOf("discover", "signin"),
        render = { _ ->
            HelpParagraph("The Wishlist tab keeps your saved restaurants and foods in one place.")
            Spacer(Modifier.height(12.dp))
            HelpStep(1) { HelpParagraph("Open the Wishlist tab from the bottom navigation.") }
            Spacer(Modifier.height(8.dp))
            HelpStep(2) { HelpParagraph("Tap a collection to see saved places.") }
            Spacer(Modifier.height(8.dp))
            HelpStep(3) { HelpParagraph("Tap any saved restaurant to open it again.") }
            Spacer(Modifier.height(8.dp))
            HelpStep(4) { HelpParagraph("Tap the heart again when you want to remove something.") }
            Spacer(Modifier.height(12.dp))
            HelpTip(icon = Icons.Outlined.Favorite, text = "Wishlist is private to your account, so it is available after sign-in.")
        },
    ),
    HelpSection(
        id = "saved",
        title = "Saving to your Wishlist",
        icon = Icons.Outlined.Bookmark,
        summary = "Keep your favourites in one place.",
        keywords = "save favorite favourite heart bookmark list wishlist",
        image = "https://images.unsplash.com/photo-1414235077428-338989a2e8c0?w=1200&h=600&fit=crop",
        readMins = 2,
        related = listOf("signin", "discover"),
        render = { jumpTo ->
            HelpParagraph("Tap the little heart on any restaurant or food picture to add it to your Wishlist. A red heart means it is saved. Tap again to remove.")
            Spacer(Modifier.height(12.dp))
            HelpStep(1) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Tap the heart icon on a card. If you aren't signed in, a pop-up will ask you to ", color = LocalRestaurantPalette.current.foreground, fontSize = 14.sp)
                    HelpInlineLink("sign in first", "signin", jumpTo)
                    Text(".", color = LocalRestaurantPalette.current.foreground, fontSize = 14.sp)
                }
            }
            Spacer(Modifier.height(8.dp))
            HelpStep(2) { HelpParagraph("Open your list from the Wishlist button in the bottom navigation.") }
            Spacer(Modifier.height(8.dp))
            HelpStep(3) { HelpParagraph("Open a collection to browse saved restaurants and foods.") }
            Spacer(Modifier.height(8.dp))
            HelpStep(4) { HelpParagraph("Tap any saved item to open it again, or tap its heart to remove it from the list.") }
            Spacer(Modifier.height(12.dp))
            HelpTip(icon = Icons.Outlined.Favorite, text = "Saving is free and private — only you can see your Wishlist.")
        },
    ),
    HelpSection(
        id = "book",
        title = "Booking a table",
        icon = Icons.Outlined.NotificationsNone,
        summary = "Reserve your seat, pay a small deposit, and lock in your time.",
        keywords = "book reserve table reservation date time guest party deposit pro",
        image = "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=1200&h=600&fit=crop",
        readMins = 5,
        related = listOf("policy", "dining", "signin", "enjoy", "reviews"),
        video = HelpVideo("Booking in 45 seconds", "0:45"),
        render = { jumpTo ->
            HelpParagraph("Booking means telling the restaurant \"please hold a table for me\". This needs you to sign in, and reservations are a Pro-only feature — see Profile → Upgrade to Pro.")
            Spacer(Modifier.height(12.dp))
            HelpStep(1) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Open any restaurant from ", color = LocalRestaurantPalette.current.foreground, fontSize = 14.sp)
                    HelpInlineLink("Discover", "discover", jumpTo)
                    Text(".", color = LocalRestaurantPalette.current.foreground, fontSize = 14.sp)
                }
            }
            Spacer(Modifier.height(8.dp))
            HelpStep(2) { HelpParagraph("Tap the big Book a Table button at the bottom.") }
            Spacer(Modifier.height(8.dp))
            HelpStep(3) { HelpParagraph("Choose how many people (guests) will come.") }
            Spacer(Modifier.height(8.dp))
            HelpStep(4) { HelpParagraph("Pick a date. Use the \"Custom\" chip at the end of the row to pick any day on the calendar.") }
            Spacer(Modifier.height(8.dp))
            HelpStep(5) { HelpParagraph("Pick a time from the time chips.") }
            Spacer(Modifier.height(8.dp))
            HelpStep(6) { HelpParagraph("Enter your name, phone, and any notes (like \"window seat\" or \"allergic to peanuts\"). Choose the occasion if you like.") }
            Spacer(Modifier.height(8.dp))
            HelpStep(7) { HelpParagraph("Pay the deposit to hold the table. The amount is shown before you confirm and is refunded in full after you arrive and pay your bill.") }
            Spacer(Modifier.height(8.dp))
            HelpStep(8) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Review everything, then tap Confirm. The booking appears in ", color = LocalRestaurantPalette.current.foreground, fontSize = 14.sp)
                    HelpInlineLink("Dining", "dining", jumpTo)
                    Text(".", color = LocalRestaurantPalette.current.foreground, fontSize = 14.sp)
                }
            }
            Spacer(Modifier.height(12.dp))
            HelpTip(tone = TipTone.Warn, icon = Icons.Outlined.Warning, text = "Read the Reservation policy before you confirm — it covers the deposit, the 2-hour cancellation window, and what happens if you don't show up.")
            HelpTip(tone = TipTone.Success, icon = Icons.Outlined.CheckCircle, text = "A confirmed booking will also send you a reminder in the Notifications list.")
        },
    ),
    HelpSection(
        id = "policy",
        title = "Reservation policy (deposit, cancel, no-show)",
        icon = Icons.Outlined.Shield,
        summary = "How the deposit, refunds, cancellation window, and no-shows work.",
        keywords = "policy deposit refund cancel cancellation no-show noshow rules terms pro fee",
        image = "https://images.unsplash.com/photo-1554224155-8d04cb21cd6c?w=1200&h=600&fit=crop",
        readMins = 3,
        related = listOf("book", "dining", "qrpay", "profile"),
        render = { _ ->
            HelpParagraph("To keep tables fair for everyone, every reservation has a small deposit and a simple set of rules. Here's exactly how it works:")
            Spacer(Modifier.height(12.dp))
            val palette = LocalRestaurantPalette.current
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                HelpInfoCard(
                    icon = Icons.Outlined.Receipt,
                    iconColor = palette.brand,
                    bg = palette.brand.copy(alpha = 0.06f),
                    title = "Deposit to reserve",
                    body = "You pay a small deposit when you confirm. This holds your table and is shown clearly before you pay.",
                    modifier = Modifier.weight(1f),
                )
                HelpInfoCard(
                    icon = Icons.Outlined.CheckCircle,
                    iconColor = palette.success,
                    bg = palette.success.copy(alpha = 0.08f),
                    title = "Cancel early = 100% refund",
                    body = "Cancel more than 2 hours before your seating time and you get your full deposit back, automatically.",
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                HelpInfoCard(
                    icon = Icons.Outlined.Schedule,
                    iconColor = palette.warning,
                    bg = palette.warning.copy(alpha = 0.10f),
                    title = "Within 2 hours = locked",
                    body = "Once it's inside the 2-hour window, the booking can no longer be cancelled — the kitchen is already preparing.",
                    modifier = Modifier.weight(1f),
                )
                HelpInfoCard(
                    icon = Icons.Outlined.Warning,
                    iconColor = palette.destructive,
                    bg = palette.destructive.copy(alpha = 0.08f),
                    title = "No-show = deposit kept",
                    body = "If you don't cancel and don't arrive, the restaurant keeps the deposit to cover the empty seat.",
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(16.dp))
            HelpSubheading("How we verify that you arrived")
            Spacer(Modifier.height(12.dp))
            HelpStep(1) { HelpParagraph("When you reach the restaurant, open the Dining tab and find your booking card.") }
            Spacer(Modifier.height(8.dp))
            HelpStep(2) { HelpParagraph("Tap Scan QR and point your phone at the restaurant's arrival QR — or tap the small QR icon to show your QR and let a staff member scan it.") }
            Spacer(Modifier.height(8.dp))
            HelpStep(3) { HelpParagraph("Either way, your booking is marked as arrived. Your deposit is now safe and will come back when you pay the bill via QR Pay.") }
            Spacer(Modifier.height(12.dp))
            HelpTip(tone = TipTone.Info, icon = Icons.Outlined.AutoAwesome, text = "Booking (and leaving a review later) is a Pro-only feature. Free members can browse, save, and search.")
            HelpTip(tone = TipTone.Warn, icon = Icons.Outlined.Warning, text = "Running late? Call the restaurant from the booking detail page. Most places will hold your table for 15 minutes.")
        },
    ),
    HelpSection(
        id = "reviews",
        title = "Reviews (after you dine)",
        icon = Icons.Outlined.ChatBubbleOutline,
        summary = "When you can review, how to do it, and what each score means.",
        keywords = "review rating stars feedback pro paid completed taste ambience service value money",
        image = "https://images.unsplash.com/photo-1521017432531-fbd92d768814?w=1200&h=600&fit=crop",
        readMins = 3,
        related = listOf("policy", "dining", "qrpay", "profile"),
        render = { _ ->
            HelpParagraph("Reviews on CatchTable come from real diners only. The restaurant profile page does not have a \"Write review\" button — the option appears in your own booking once the meal is paid and completed.")
            Spacer(Modifier.height(16.dp))
            HelpSubheading("When can I leave a review?")
            Spacer(Modifier.height(8.dp))
            HelpParagraph("All three must be true:")
            Spacer(Modifier.height(8.dp))
            HelpStep(1) { HelpParagraph("Your booking is marked arrived (see arrival verification).") }
            Spacer(Modifier.height(8.dp))
            HelpStep(2) { HelpParagraph("You paid through QR Pay, so the visit shows Completed.") }
            Spacer(Modifier.height(8.dp))
            HelpStep(3) { HelpParagraph("You're on a Pro plan — reviewing, like booking, is Pro-only.") }
            Spacer(Modifier.height(16.dp))
            HelpSubheading("How to leave a review")
            Spacer(Modifier.height(8.dp))
            HelpStep(1) { HelpParagraph("Go to Dining → Visited and open the completed booking.") }
            Spacer(Modifier.height(8.dp))
            HelpStep(2) { HelpParagraph("Tap Write review. Give an overall star rating (required).") }
            Spacer(Modifier.height(8.dp))
            HelpStep(3) { HelpParagraph("Optionally rate any of the four sub-categories. You can skip any you don't want to rate.") }
            Spacer(Modifier.height(8.dp))
            HelpStep(4) { HelpParagraph("Add a short note (what stood out, what you'd order again) and submit.") }
            Spacer(Modifier.height(16.dp))
            HelpSubheading("What each score means")
            Spacer(Modifier.height(8.dp))
            ScoreItem("\uD83C\uDF74 Taste", "How the food itself tasted — flavor, freshness, seasoning, execution.")
            ScoreItem("\u2728 Ambience", "The room: lighting, noise level, decor, comfort, vibe.")
            ScoreItem("\uD83E\uDD1D Service", "Staff attentiveness, timing between courses, friendliness.")
            ScoreItem("\uD83D\uDCB0 Value", "How the overall experience measured up to what you paid.")
            Spacer(Modifier.height(12.dp))
            HelpTip(tone = TipTone.Info, icon = Icons.Outlined.AutoAwesome, text = "Not every reviewer rates every category — that's by design. Leave blank the ones you can't fairly judge.")
        },
    ),
    HelpSection(
        id = "dining",
        title = "Dining tab (your bookings)",
        icon = Icons.Outlined.Restaurant,
        summary = "Upcoming, currently dining, and past visits.",
        keywords = "dining reservation upcoming visited cancel edit",
        image = "https://images.unsplash.com/photo-1414235077428-338989a2e8c0?w=1200&h=600&fit=crop",
        readMins = 3,
        related = listOf("book", "policy", "enjoy", "qrpay", "reviews"),
        render = { _ ->
            HelpParagraph("The Dining tab is a simple list of all your bookings. Each card has the restaurant name, the date, and the status.")
            Spacer(Modifier.height(12.dp))
            HelpStep(1) { HelpParagraph("Upcoming bookings say \"Left 2h 15m\" until your seating time.") }
            Spacer(Modifier.height(8.dp))
            HelpStep(2) { HelpParagraph("If you're at the restaurant right now, the card shows Now Dining with a red dot.") }
            Spacer(Modifier.height(8.dp))
            HelpStep(3) { HelpParagraph("When you arrive, tap Scan QR to scan the restaurant's code — or show your own QR for staff to scan.") }
            Spacer(Modifier.height(8.dp))
            HelpStep(4) { HelpParagraph("Past bookings show Visited and a View Receipt button. From here you can also Write a review.") }
            Spacer(Modifier.height(8.dp))
            HelpStep(5) { HelpParagraph("Tap a card to open its details — Get directions, Invite friends, View menu, or Cancel (if more than 2 hours remain).") }
            Spacer(Modifier.height(12.dp))
            HelpTip(tone = TipTone.Warn, icon = Icons.Outlined.Warning, text = "Cancellations only refund the deposit more than 2 hours before your seating. Inside 2 hours the booking is locked.")
        },
    ),
    HelpSection(
        id = "enjoy",
        title = "While you're at the restaurant",
        icon = Icons.Outlined.Restaurant,
        summary = "Menu, servers, and Scan & Pay.",
        keywords = "enjoy meal menu waiter server call pay bill qr",
        image = "https://images.unsplash.com/photo-1753351055117-f24d8baa682e?w=1200&h=600&fit=crop",
        readMins = 2,
        related = listOf("dining", "qrpay"),
        render = { _ ->
            HelpParagraph("When your reservation starts, the Dining card turns into an Enjoy Your Meal page. It has three helpers:")
            Spacer(Modifier.height(12.dp))
            HelpStep(1) { HelpParagraph("View menu — see today's dishes and prices. The menu is view only; the waiter will take your order.") }
            Spacer(Modifier.height(8.dp))
            HelpStep(2) { HelpParagraph("Call server — a polite way to ask for help without raising your hand.") }
            Spacer(Modifier.height(8.dp))
            HelpStep(3) { HelpParagraph("Scan & pay — see QR Pay below.") }
            Spacer(Modifier.height(12.dp))
            HelpTip(text = "Tap the restaurant name at the top of the page to see the full restaurant profile, hours, and reviews.")
        },
    ),
    HelpSection(
        id = "qrpay",
        title = "QR Pay (Scan & Pay)",
        icon = Icons.Outlined.QrCode,
        summary = "Pay the bill by scanning a QR code.",
        keywords = "qr scan pay bill check tip split receipt",
        image = "https://images.unsplash.com/photo-1556742111-a301076d9d18?w=1200&h=600&fit=crop",
        readMins = 4,
        related = listOf("enjoy", "dining", "troubleshoot"),
        video = HelpVideo("Scanning a bill QR", "0:58"),
        render = { _ ->
            HelpParagraph("At the end of your meal, you can pay without waiting for a card machine. Look for the small QR code on the bill or table.")
            Spacer(Modifier.height(12.dp))
            HelpStep(1) { HelpParagraph("Tap the big round QR button in the centre of the bottom bar, or tap Scan & Pay from the Enjoy Your Meal page.") }
            Spacer(Modifier.height(8.dp))
            HelpStep(2) { HelpParagraph("Point your camera at the QR code on the bill.") }
            Spacer(Modifier.height(8.dp))
            HelpStep(3) { HelpParagraph("Check the items and the total carefully.") }
            Spacer(Modifier.height(8.dp))
            HelpStep(4) { HelpParagraph("Choose a tip if you want. You can split the bill with friends.") }
            Spacer(Modifier.height(8.dp))
            HelpStep(5) { HelpParagraph("Tap Pay. The restaurant will see the payment right away.") }
            Spacer(Modifier.height(12.dp))
            HelpTip(tone = TipTone.Success, icon = Icons.Outlined.Receipt, text = "A receipt is saved to Dining → Visited for your records.")
            HelpTip(tone = TipTone.Warn, icon = Icons.Outlined.Shield, text = "Only scan QR codes at the table, printed on the bill, or shown by a staff member. If anything looks odd, ask a server.")
        },
    ),
    HelpSection(
        id = "notifications",
        title = "Notifications",
        icon = Icons.Outlined.CalendarMonth,
        summary = "Reminders and updates from restaurants.",
        keywords = "notifications bell reminder updates inbox",
        image = "https://images.unsplash.com/photo-1604872376944-0b0d4e1c8a25?w=1200&h=600&fit=crop",
        readMins = 1,
        related = listOf("book", "dining"),
        render = { _ ->
            HelpParagraph("The little bell icon shows messages about your bookings, offers, and important updates. A red number means you have something new.")
            Spacer(Modifier.height(12.dp))
            HelpStep(1) { HelpParagraph("Tap the bell at the top-right of the screen.") }
            Spacer(Modifier.height(8.dp))
            HelpStep(2) { HelpParagraph("Unread messages have a coloured dot. Tap one to read and mark it done.") }
            Spacer(Modifier.height(8.dp))
            HelpStep(3) { HelpParagraph("Use Mark all as read to clear the list quickly.") }
        },
    ),
    HelpSection(
        id = "profile",
        title = "Profile & settings",
        icon = Icons.Outlined.Person,
        summary = "Your name, theme, rewards, and app settings.",
        keywords = "profile settings theme rewards tier balance friends",
        image = "https://images.unsplash.com/photo-1488161628813-04466f872be2?w=1200&h=600&fit=crop",
        readMins = 2,
        related = listOf("signin", "help-usage"),
        render = { _ ->
            HelpParagraph("The Profile tab is your personal space. From here you can edit your name, see your rewards balance, change the look of the app, and open Settings.")
            Spacer(Modifier.height(12.dp))
            HelpStep(1) { HelpParagraph("Tap your name at the top to edit your profile.") }
            Spacer(Modifier.height(8.dp))
            HelpStep(2) { HelpParagraph("Use the Appearance buttons to change colors or switch to dark mode.") }
            Spacer(Modifier.height(8.dp))
            HelpStep(3) { HelpParagraph("Open Settings for account, privacy, language, and sound options.") }
            Spacer(Modifier.height(8.dp))
            HelpStep(4) { HelpParagraph("Tap Help & Guide any time to come back here.") }
            Spacer(Modifier.height(12.dp))
            HelpTip(tone = TipTone.Info, icon = Icons.Outlined.AutoAwesome, text = "Earn points every time you dine or refer a friend — points unlock higher reward tiers.")
        },
    ),
    HelpSection(
        id = "accessibility",
        title = "Accessibility & comfort",
        icon = Icons.Outlined.Accessibility,
        summary = "Larger text, read-aloud, and colour themes.",
        keywords = "accessibility large text read aloud voice contrast theme dark",
        image = "https://images.unsplash.com/photo-1551836022-d5d88e9218df?w=1200&h=600&fit=crop",
        readMins = 2,
        related = listOf("profile", "help-usage"),
        render = { _ ->
            HelpParagraph("Make the app comfortable for you. Whether you need bigger letters, spoken text, or a dark screen, we have options.")
            Spacer(Modifier.height(12.dp))
            HelpStep(1) { HelpParagraph("Larger text — use the A+ button at the top of this guide to make everything bigger.") }
            Spacer(Modifier.height(8.dp))
            HelpStep(2) { HelpParagraph("Read aloud — tap the speaker icon at the top of any section and the app will read it to you.") }
            Spacer(Modifier.height(8.dp))
            HelpStep(3) { HelpParagraph("Dark mode / themes — change the colour in Profile → Appearance.") }
            Spacer(Modifier.height(8.dp))
            HelpStep(4) { HelpParagraph("Language — go to Profile → Settings → Language.") }
            Spacer(Modifier.height(12.dp))
            HelpTip(tone = TipTone.Success, icon = Icons.Outlined.VolumeUp, text = "Read-aloud needs a moment to start on some phones. If nothing happens, check that your phone's volume is on.")
        },
    ),
    HelpSection(
        id = "help-usage",
        title = "How to use this guide",
        icon = Icons.Outlined.MenuBook,
        summary = "Search, links, and quick navigation.",
        keywords = "how use guide help search hyperlink navigation",
        image = "https://images.unsplash.com/photo-1456513080510-7bf3a84b82f8?w=1200&h=600&fit=crop",
        readMins = 1,
        related = listOf("getting-started", "accessibility"),
        render = { jumpTo ->
            HelpParagraph("Think of this page as a small book with clickable pages.")
            Spacer(Modifier.height(12.dp))
            HelpStep(1) { HelpParagraph("Use the Search box at the top to jump to any topic.") }
            Spacer(Modifier.height(8.dp))
            HelpStep(2) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Every underlined word is a link. Tap it to go to that topic. Try ", color = LocalRestaurantPalette.current.foreground, fontSize = 14.sp)
                    HelpInlineLink("QR Pay", "qrpay", jumpTo)
                    Text("!", color = LocalRestaurantPalette.current.foreground, fontSize = 14.sp)
                }
            }
            Spacer(Modifier.height(8.dp))
            HelpStep(3) { HelpParagraph("Scroll down — each topic has steps and pictures.") }
            Spacer(Modifier.height(8.dp))
            HelpStep(4) { HelpParagraph("Tap \"Was this helpful?\" at the end of a topic to tell us what to improve.") }
            Spacer(Modifier.height(8.dp))
            HelpStep(5) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Still stuck? Tap the floating chat bubble to ", color = LocalRestaurantPalette.current.foreground, fontSize = 14.sp)
                    HelpInlineLink("talk to us", "contact", jumpTo)
                    Text(" right now.", color = LocalRestaurantPalette.current.foreground, fontSize = 14.sp)
                }
            }
        },
    ),
    HelpSection(
        id = "troubleshoot",
        title = "When something goes wrong",
        icon = Icons.Outlined.Warning,
        summary = "Simple fixes for common problems.",
        keywords = "problem error bug fix help support slow crash",
        image = "https://images.unsplash.com/photo-1504384308090-c894fdcc538d?w=1200&h=600&fit=crop",
        readMins = 3,
        related = listOf("contact", "qrpay", "signin"),
        render = { jumpTo ->
            HelpStep(1) { HelpParagraph("I can't see anything / the page is blank. Pull down to refresh, or close the app and open it again.") }
            Spacer(Modifier.height(8.dp))
            HelpStep(2) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("My heart isn't turning red. You must ", color = LocalRestaurantPalette.current.foreground, fontSize = 14.sp)
                    HelpInlineLink("sign in", "signin", jumpTo)
                    Text(" first to save items.", color = LocalRestaurantPalette.current.foreground, fontSize = 14.sp)
                }
            }
            Spacer(Modifier.height(8.dp))
            HelpStep(3) { HelpParagraph("Booking won't go through. Check your phone number has the right country code.") }
            Spacer(Modifier.height(8.dp))
            HelpStep(4) { HelpParagraph("QR code won't scan. Make sure there is enough light and hold the camera steady.") }
            Spacer(Modifier.height(8.dp))
            HelpStep(5) { HelpParagraph("I forgot my password. On the login screen, tap Forgot password.") }
            Spacer(Modifier.height(12.dp))
            HelpTip(tone = TipTone.Info, icon = Icons.Outlined.ChatBubbleOutline, text = "Still stuck? Jump to Contact support and we'll help — or tap the chat bubble at the bottom.")
        },
    ),
    HelpSection(
        id = "contact",
        title = "Contact support",
        icon = Icons.Outlined.ChatBubbleOutline,
        summary = "Chat, email, or call us.",
        keywords = "contact help support email phone chat live agent",
        image = "https://images.unsplash.com/photo-1587560699334-cc4ff634909a?w=1200&h=600&fit=crop",
        readMins = 1,
        related = listOf("troubleshoot", "help-usage"),
        render = { _ ->
            HelpParagraph("Our team replies within 24 hours. Pick whichever is easiest for you.")
            Spacer(Modifier.height(12.dp))
            val palette = LocalRestaurantPalette.current
            ContactCard(
                icon = Icons.Outlined.ChatBubbleOutline,
                iconColor = palette.brand,
                bg = palette.brand.copy(alpha = 0.10f),
                outline = true,
                title = "Live chat",
                subtitle = "Fastest \u00B7 now",
            )
            Spacer(Modifier.height(8.dp))
            ContactCard(
                icon = Icons.Outlined.Email,
                iconColor = palette.info,
                bg = palette.info.copy(alpha = 0.10f),
                outline = false,
                title = "Email",
                subtitle = "help@catchtable.app",
            )
            Spacer(Modifier.height(8.dp))
            ContactCard(
                icon = Icons.Outlined.Phone,
                iconColor = palette.success,
                bg = palette.success.copy(alpha = 0.10f),
                outline = false,
                title = "Phone",
                subtitle = "1-800-CATCH-TB",
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = "Mon\u2013Sun \u00B7 08:00\u201322:00 local time",
                color = LocalRestaurantPalette.current.mutedForeground,
                fontSize = 12.sp,
            )
        },
    ),
)

@Composable
private fun ScoreItem(title: String, body: String) {
    val palette = LocalRestaurantPalette.current
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(title, color = palette.foreground, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        Text(body, color = palette.mutedForeground, fontSize = 12.sp, lineHeight = 16.sp, modifier = Modifier.padding(top = 2.dp))
    }
}

@Composable
private fun ContactCard(
    icon: ImageVector,
    iconColor: Color,
    bg: Color,
    outline: Boolean,
    title: String,
    subtitle: String,
) {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(16.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.dp, if (outline) palette.brand else palette.border, shape)
            .background(if (outline) palette.brand.copy(alpha = 0.05f) else palette.cardSurface)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(bg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = iconColor, modifier = Modifier.size(18.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = palette.foreground, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = palette.mutedForeground, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
        }
    }
}

private fun buildHelpFaqs(): List<HelpFaq> = listOf(
    HelpFaq(
        question = "Is CatchTable free to use?",
        answer = "Yes. Browsing and searching are free. Some optional features (like Pro) have a small monthly fee.",
    ),
    HelpFaq(
        question = "Can I use the app without signing in?",
        answer = "You can browse Discover freely. To save, book, or pay you'll need to sign in.",
    ),
    HelpFaq(
        question = "Do I need to pay a deposit to book?",
        answer = "Yes — a small deposit holds your table and is fully refunded after you arrive and pay.",
    ),
    HelpFaq(
        question = "Can I cancel a booking and get my deposit back?",
        answer = "Cancel more than 2 hours before your seating for a 100% refund. Inside the 2-hour window the booking is locked, and a no-show means the restaurant keeps the deposit.",
    ),
    HelpFaq(
        question = "How does the restaurant know I actually showed up?",
        answer = "Open the Dining tab, tap Scan QR at the restaurant — or show your QR for staff to scan.",
    ),
    HelpFaq(
        question = "Why can't I write a review yet?",
        answer = "Reviews unlock automatically once your booking is marked arrived AND paid via QR Pay. Booking and reviewing are Pro-only.",
    ),
    HelpFaq(
        question = "Are my card details safe with QR Pay?",
        answer = "Yes. Your card is never shown to the restaurant — they only see that the payment succeeded.",
    ),
    HelpFaq(
        question = "Can children use this app?",
        answer = "With a parent's help, yes. Parents should set up the account and be present when paying.",
    ),
    HelpFaq(
        question = "How do I change the language?",
        answer = "Open Profile → Settings → Language.",
    ),
    HelpFaq(
        question = "Does the app work without internet?",
        answer = "Browsing needs the internet. If your connection drops, try moving closer to Wi-Fi or switching off Airplane mode.",
    ),
)

