package com.mh.restaurantchainreservation.feature.profile.subpages

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.zIndex
import com.mh.restaurantchainreservation.core.designsystem.components.DeterministicQrCode
import com.mh.restaurantchainreservation.core.designsystem.components.GlobalNotificationCenter
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.feature.profile.subpages.components.AnimatedAmountDisplay
import com.mh.restaurantchainreservation.feature.profile.subpages.components.Currency
import com.mh.restaurantchainreservation.feature.profile.subpages.components.MoneyKeypad
import com.mh.restaurantchainreservation.feature.profile.subpages.components.amountAsNumber
import com.mh.restaurantchainreservation.feature.profile.subpages.components.appendDigit
import com.mh.restaurantchainreservation.feature.profile.subpages.components.backspaceDigit
import com.mh.restaurantchainreservation.feature.profile.subpages.components.formatAmountString
import com.mh.restaurantchainreservation.feature.profile.hub.AddNewCreditCardTile
import com.mh.restaurantchainreservation.feature.profile.hub.HubCardPattern
import com.mh.restaurantchainreservation.feature.profile.hub.HubCardThemeId
import com.mh.restaurantchainreservation.feature.profile.hub.SharedHubCardFace
import com.mh.restaurantchainreservation.feature.profile.hub.SharedHubCardFaceModel
import com.mh.restaurantchainreservation.feature.profile.hub.hubCardThemeSpec
import com.mh.restaurantchainreservation.feature.profile.hub.hubCardThemeBackgroundBrush
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.math.min

private enum class CardMode { Browse, Deposit, Withdraw, Send, Settings, ChooseNewCardTheme }

private data class ProfileCreditCard(
    val id: String,
    val nickname: String,
    val holder: String,
    val number: String,
    val expiry: String,
    val themeId: HubCardThemeId,
    val pattern: HubCardPattern,
    val frozen: Boolean = false,
    val externalUse: Boolean = true,
    val krwBalance: Double,
    val usdBalance: Double,
)

private data class CardTx(
    val label: String,
    val amount: String,
    val positive: Boolean,
)

@Composable
fun CreditCardsPage(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val palette = LocalRestaurantPalette.current
    val cards = remember {
        mutableStateListOf(
            ProfileCreditCard(
                id = "card-main",
                nickname = "Tonight Black",
                holder = "Alex Chen",
                number = "4890123456784242",
                expiry = "08/29",
                themeId = HubCardThemeId.Rose,
                pattern = hubCardThemeSpec(HubCardThemeId.Rose).pattern,
                krwBalance = 820000.0,
                usdBalance = 216.45,
            ),
            ProfileCreditCard(
                id = "card-travel",
                nickname = "Travel Card",
                holder = "Alex Chen",
                number = "5339123411119021",
                expiry = "11/30",
                themeId = HubCardThemeId.Ocean,
                pattern = HubCardPattern.Rays,
                krwBalance = 120000.0,
                usdBalance = 84.0,
            ),
        )
    }
    var activeIndex by rememberSaveable { mutableIntStateOf(0) }
    var mode by rememberSaveable { mutableStateOf(CardMode.Browse) }
    var qrCard by remember { mutableStateOf<ProfileCreditCard?>(null) }
    var pendingPickTheme by remember { mutableStateOf(HubCardThemeId.Rose) }
    var pendingPickPattern by remember { mutableStateOf(hubCardThemeSpec(HubCardThemeId.Rose).pattern) }
    var pendingNewCardNumber by remember { mutableStateOf("") }
    var pendingNewCardNickname by remember { mutableStateOf("Tonight Rose") }

    val activeCard = if (activeIndex < cards.size) cards.getOrNull(activeIndex.coerceIn(0, cards.lastIndex.coerceAtLeast(0))) else null

    fun replaceActive(next: ProfileCreditCard) {
        val index = cards.indexOfFirst { it.id == next.id }
        if (index >= 0) cards[index] = next
    }

    AnimatedContent(
        targetState = mode,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "credit-card-mode",
        modifier = modifier,
    ) { targetMode ->
        when (targetMode) {
            CardMode.Browse -> {
                val openChooseNewCardTheme = {
                    val index = cards.size + 1
                    pendingPickTheme = HubCardThemeId.Rose
                    pendingPickPattern = hubCardThemeSpec(HubCardThemeId.Rose).pattern
                    pendingNewCardNumber = "4890${(100000000000 + index * 43129).toString().takeLast(12)}"
                    pendingNewCardNickname = "Tonight ${HubCardThemeId.Rose.name}"
                    mode = CardMode.ChooseNewCardTheme
                }
                SubpageScaffold(
                    title = "Credit cards",
                    onBack = onBack,
                ) {
                    HeaderAddButton(onClick = openChooseNewCardTheme)
                    Spacer(Modifier.height(18.dp))
                    CardCarousel(
                        cards = cards,
                        activeIndex = activeIndex,
                        onSelect = { activeIndex = it },
                        onAddNewCard = openChooseNewCardTheme,
                    )
                    Spacer(Modifier.height(18.dp))
                    activeCard?.let { card ->
                        ActionGrid(
                            frozen = card.frozen,
                            onDeposit = { if (!card.frozen) mode = CardMode.Deposit },
                            onWithdraw = { if (!card.frozen) mode = CardMode.Withdraw },
                            onSend = { if (!card.frozen) mode = CardMode.Send },
                            onReceive = { qrCard = card },
                            onSettings = { mode = CardMode.Settings },
                        )
                        Spacer(Modifier.height(22.dp))
                        TransactionsCard(card = card)
                        Spacer(Modifier.height(22.dp))
                    }
                    Text("Your cards", color = palette.foreground, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.height(10.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .border(1.dp, palette.border, RoundedCornerShape(20.dp))
                            .background(palette.cardSurface),
                    ) {
                        cards.forEachIndexed { index, item ->
                            CardListRow(
                                card = item,
                                selected = index == activeIndex && activeIndex < cards.size,
                                onClick = { activeIndex = index },
                            )
                        }
                    }
                    Spacer(Modifier.height(40.dp))
                }
            }
            CardMode.ChooseNewCardTheme -> {
                val index = cards.size + 1
                ChooseCardThemePage(
                    previewNickname = pendingNewCardNickname,
                    holder = "Alex Chen",
                    lastFour = pendingNewCardNumber.takeLast(4),
                    fullCardNumber = pendingNewCardNumber,
                    selectedThemeId = pendingPickTheme,
                    selectedPattern = pendingPickPattern,
                    onThemeSelected = {
                        pendingPickTheme = it
                        pendingNewCardNickname = "Tonight ${it.name}"
                    },
                    onPatternSelected = { pendingPickPattern = it },
                    onBack = { mode = CardMode.Browse },
                    onConfirm = {
                        cards.add(
                            ProfileCreditCard(
                                id = "card-$index",
                                nickname = pendingNewCardNickname,
                                holder = "Alex Chen",
                                number = pendingNewCardNumber,
                                expiry = "12/${29 + index}",
                                themeId = pendingPickTheme,
                                pattern = pendingPickPattern,
                                krwBalance = 0.0,
                                usdBalance = 0.0,
                            ),
                        )
                        activeIndex = cards.lastIndex
                        mode = CardMode.Browse
                        GlobalNotificationCenter.success("Card created", "Your new Tonight card is ready.")
                    },
                )
            }
            CardMode.Deposit, CardMode.Withdraw, CardMode.Send -> {
                activeCard?.let { card ->
                    CardAmountAction(
                        mode = targetMode,
                        card = card,
                        onBack = { mode = CardMode.Browse },
                        onApply = { amount, currency ->
                            val signed = if (targetMode == CardMode.Withdraw || targetMode == CardMode.Send) -amount else amount
                            replaceActive(
                                when (currency) {
                                    Currency.KRW -> card.copy(krwBalance = (card.krwBalance + signed).coerceAtLeast(0.0))
                                    Currency.USD -> card.copy(usdBalance = (card.usdBalance + signed).coerceAtLeast(0.0))
                                },
                            )
                            mode = CardMode.Browse
                            GlobalNotificationCenter.success("Card updated", "Your card balance was updated.")
                        },
                    )
                }
            }
            CardMode.Settings -> {
                activeCard?.let { card ->
                    CardSettingsPanel(
                        card = card,
                        canRemove = cards.size > 1,
                        onBack = { mode = CardMode.Browse },
                        onToggleFrozen = {
                            replaceActive(card.copy(frozen = !card.frozen))
                            GlobalNotificationCenter.info("Card settings", if (card.frozen) "Card unfrozen." else "Card frozen.")
                        },
                        onToggleExternal = {
                            replaceActive(card.copy(externalUse = !card.externalUse))
                            GlobalNotificationCenter.info("Card settings", if (card.externalUse) "External use disabled." else "External use enabled.")
                        },
                        onRemove = {
                            val removeAt = cards.indexOfFirst { it.id == card.id }
                            if (removeAt >= 0 && cards.size > 1) {
                                cards.removeAt(removeAt)
                                activeIndex = min(activeIndex, cards.lastIndex)
                                mode = CardMode.Browse
                                GlobalNotificationCenter.warning("Card closed", "${card.nickname} was removed.")
                            }
                        },
                    )
                }
            }
        }
    }

    qrCard?.let { card ->
        AlertDialog(
            onDismissRequest = { qrCard = null },
            title = { Text("Receive on this card") },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    DeterministicQrCode(
                        code = "tonight-card:${card.number}",
                        modifier = Modifier.size(190.dp),
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(card.nickname, color = palette.foreground, fontWeight = FontWeight.Bold)
                    Text(maskCard(card.number), color = palette.mutedForeground, fontSize = 13.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = { qrCard = null }) {
                    Text("Done", color = palette.brand, fontWeight = FontWeight.Bold)
                }
            },
        )
    }
}

@Composable
private fun HeaderAddButton(onClick: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, palette.border, RoundedCornerShape(18.dp))
            .background(palette.cardSurface)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(Modifier.size(42.dp).clip(CircleShape).background(palette.brand.copy(alpha = 0.10f)), contentAlignment = Alignment.Center) {
            Icon(Icons.Outlined.Add, null, tint = palette.brand, modifier = Modifier.size(22.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text("Open a new card", color = palette.foreground, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
            Text("Every card stays multi-currency.", color = palette.mutedForeground, fontSize = 12.sp)
        }
    }
}

private fun ProfileCreditCard.toFaceModel(revealPan: Boolean): SharedHubCardFaceModel {
    val lastFour = number.takeLast(4).ifEmpty { "0000" }
    return SharedHubCardFaceModel(
        productLabel = nickname,
        holder = holder,
        lastFour = lastFour,
        krwBalance = krwBalance.toLong(),
        usdBalance = usdBalance,
        themeId = themeId,
        pattern = pattern,
        showBalance = krwBalance > 0.0 || usdBalance > 0.0,
        showDualBalance = krwBalance > 0.0 && usdBalance > 0.0,
        frozen = frozen,
        showFullPan = revealPan,
        fullCardNumber = number,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CardCarousel(
    cards: List<ProfileCreditCard>,
    activeIndex: Int,
    onSelect: (Int) -> Unit,
    onAddNewCard: () -> Unit,
) {
    val cardWidth = 318.dp
    val pagerState = rememberPagerState(
        initialPage = activeIndex.coerceIn(0, cards.size),
        pageCount = { cards.size + 1 },
    )
    val stackedFling = PagerDefaults.flingBehavior(
        state = pagerState,
        snapAnimationSpec = spring(dampingRatio = 0.82f, stiffness = 320f),
    )

    LaunchedEffect(activeIndex, cards.size) {
        val target = activeIndex.coerceIn(0, cards.size)
        if (pagerState.currentPage != target) {
            pagerState.animateScrollToPage(target)
        }
    }

    LaunchedEffect(pagerState, cards.size) {
        snapshotFlow { pagerState.settledPage }
            .distinctUntilChanged()
            .collect { page -> onSelect(page) }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val sidePad = remember(maxWidth, cardWidth) {
            ((maxWidth - cardWidth) / 2).coerceAtLeast(4.dp)
        }
        val overlap = cardWidth * 0.26f

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = sidePad),
            pageSize = PageSize.Fixed(cardWidth),
            pageSpacing = -overlap,
            verticalAlignment = Alignment.CenterVertically,
            beyondViewportPageCount = 3,
            flingBehavior = stackedFling,
        ) { page ->
            val d = pagerState.getOffsetDistanceInPages(page).coerceIn(-2.5f, 2.5f)
            val absD = kotlin.math.abs(d)
            val focusT = 1f - absD.coerceIn(0f, 1f)
            Box(
                modifier = Modifier
                    .zIndex(4000f - absD * 1100f)
                    .graphicsLayer {
                        transformOrigin = TransformOrigin(0.5f, 0.52f)
                        cameraDistance = 14f * density
                        rotationZ = (-d * 7.8f).coerceIn(-12f, 12f)
                        val scale = lerp(0.84f, 1f, focusT)
                        scaleX = scale
                        scaleY = scale
                        translationX = d * 22f * density
                        translationY = lerp(20f * density, 0f, focusT)
                        alpha = lerp(0.74f, 1f, focusT).coerceIn(0.58f, 1f)
                    },
            ) {
                if (page >= cards.size) {
                    AddNewCreditCardTile(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onAddNewCard),
                    )
                } else {
                    CardFace(
                        card = cards[page],
                        reveal = page == activeIndex,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(page) },
                    )
                }
            }
        }
    }
}

@Composable
private fun CardFace(card: ProfileCreditCard, reveal: Boolean, modifier: Modifier = Modifier) {
    SharedHubCardFace(
        model = card.toFaceModel(revealPan = reveal),
        modifier = modifier,
    )
}

@Composable
private fun ActionGrid(
    frozen: Boolean,
    onDeposit: () -> Unit,
    onWithdraw: () -> Unit,
    onSend: () -> Unit,
    onReceive: () -> Unit,
    onSettings: () -> Unit,
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        CardAction(Icons.Outlined.ArrowDownward, "Top up", frozen, onDeposit, Modifier.weight(1f))
        CardAction(Icons.Outlined.ArrowUpward, "Withdraw", frozen, onWithdraw, Modifier.weight(1f))
        CardAction(Icons.Outlined.Send, "Send", frozen, onSend, Modifier.weight(1f))
        CardAction(Icons.Outlined.QrCode, "Receive", false, onReceive, Modifier.weight(1f))
        CardAction(Icons.Outlined.Settings, "Settings", false, onSettings, Modifier.weight(1f))
    }
}

@Composable
private fun CardAction(icon: ImageVector, label: String, disabled: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = modifier
            .height(82.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(if (disabled) palette.mutedSurface.copy(alpha = 0.60f) else palette.mutedSurface)
            .clickable(enabled = !disabled, onClick = onClick)
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(icon, null, tint = if (disabled) palette.mutedForeground.copy(alpha = 0.5f) else palette.foreground, modifier = Modifier.size(20.dp))
        Spacer(Modifier.height(6.dp))
        Text(label, color = if (disabled) palette.mutedForeground.copy(alpha = 0.5f) else palette.foreground, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
    }
}

@Composable
private fun TransactionsCard(card: ProfileCreditCard) {
    val palette = LocalRestaurantPalette.current
    val txs = remember(card.id, card.krwBalance, card.usdBalance) {
        listOf(
            CardTx("Coffee & brunch", "-$24.80", false),
            CardTx("Card top up", "+${formatKrw(50000.0)}", true),
            CardTx("Sent to Travel Card", "-$12.00", false),
            CardTx("Dining reward", "+$4.25", true),
        )
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .border(1.dp, palette.border, RoundedCornerShape(22.dp))
            .background(palette.cardSurface)
            .padding(14.dp),
    ) {
        Text("Recent transactions", color = palette.foreground, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(8.dp))
        txs.forEach { tx ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 9.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(34.dp).clip(CircleShape).background(palette.mutedSurface), contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.CreditCard, null, tint = palette.foreground, modifier = Modifier.size(17.dp))
                }
                Text(tx.label, color = palette.foreground, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(start = 10.dp).weight(1f))
                Text(tx.amount, color = if (tx.positive) palette.success else palette.foreground, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun CardListRow(card: ProfileCreditCard, selected: Boolean, onClick: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(14.dp))
                .drawBehind {
                    drawRect(
                        brush = hubCardThemeBackgroundBrush(
                            themeId = card.themeId,
                            widthPx = size.width,
                            heightPx = size.height,
                            brandColor = palette.brand,
                        ),
                    )
                },
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.CreditCard, null, tint = Color.White, modifier = Modifier.size(22.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(card.nickname, color = palette.foreground, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
            Text(maskCard(card.number), color = palette.mutedForeground, fontSize = 12.sp)
        }
        if (selected) {
            Text("Selected", color = palette.brand, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun CardAmountAction(
    mode: CardMode,
    card: ProfileCreditCard,
    onBack: () -> Unit,
    onApply: (Double, Currency) -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    var currency by rememberSaveable { mutableStateOf(Currency.KRW) }
    var amount by rememberSaveable(currency) { mutableStateOf("") }
    val numeric = amountAsNumber(amount)
    val title = when (mode) {
        CardMode.Deposit -> "Top up card"
        CardMode.Withdraw -> "Withdraw from card"
        else -> "Send from card"
    }
    val canConfirm = numeric > 0.0

    SubpageScaffold(title = title, onBack = onBack) {
        SmallCardHeader(card)
        Spacer(Modifier.height(24.dp))
        AnimatedAmountDisplay(
            amount = formatAmountString(amount.ifEmpty { "0" }, currency),
            symbol = if (currency == Currency.KRW) "W" else "$",
            symbolColor = palette.brand,
            valueColor = palette.foreground,
            modifier = Modifier.fillMaxWidth(),
            fontSize = 48,
        )
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            CurrencyChip("KRW", currency == Currency.KRW, Modifier.weight(1f)) {
                currency = Currency.KRW
                amount = ""
            }
            CurrencyChip("USD", currency == Currency.USD, Modifier.weight(1f)) {
                currency = Currency.USD
                amount = ""
            }
        }
        Spacer(Modifier.height(18.dp))
        MoneyKeypad(
            currency = currency,
            onDigit = { amount = appendDigit(amount, it, currency) },
            onBackspace = { amount = backspaceDigit(amount) },
        )
        Spacer(Modifier.height(18.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(if (canConfirm) palette.brand else palette.mutedSurface)
                .clickable(enabled = canConfirm) { onApply(numeric, currency) },
            contentAlignment = Alignment.Center,
        ) {
            Text("Confirm", color = if (canConfirm) Color.White else palette.mutedForeground, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun CurrencyChip(label: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = modifier
            .height(42.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(if (selected) palette.foreground else palette.mutedSurface)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, color = if (selected) palette.cardSurface else palette.foreground, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SmallCardHeader(card: ProfileCreditCard) {
    val palette = LocalRestaurantPalette.current
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(15.dp))
                .drawBehind {
                    drawRect(
                        brush = hubCardThemeBackgroundBrush(
                            themeId = card.themeId,
                            widthPx = size.width,
                            heightPx = size.height,
                            brandColor = palette.brand,
                        ),
                    )
                },
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.CreditCard, null, tint = Color.White, modifier = Modifier.size(23.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(card.nickname, color = palette.foreground, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
            Text(maskCard(card.number), color = palette.mutedForeground, fontSize = 12.sp)
        }
    }
}

@Composable
private fun CardSettingsPanel(
    card: ProfileCreditCard,
    canRemove: Boolean,
    onBack: () -> Unit,
    onToggleFrozen: () -> Unit,
    onToggleExternal: () -> Unit,
    onRemove: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    var reveal by rememberSaveable { mutableStateOf(false) }
    SubpageScaffold(title = "Card settings", onBack = onBack) {
        CardFace(card = card, reveal = reveal, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(18.dp))
        SettingsRow(Icons.Outlined.Visibility, "Reveal card details", if (reveal) formatCardNumber(card.number) else "Hidden") {
            reveal = !reveal
        }
        SettingsRow(Icons.Outlined.Lock, if (card.frozen) "Unfreeze card" else "Freeze card", if (card.frozen) "Payments are paused" else "Pause external use") {
            onToggleFrozen()
        }
        SettingsRow(Icons.Outlined.CreditCard, "External card use", if (card.externalUse) "Enabled" else "Disabled") {
            onToggleExternal()
        }
        Spacer(Modifier.height(18.dp))
        AnimatedVisibility(visible = canRemove, enter = fadeIn(), exit = fadeOut()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFFF385C).copy(alpha = 0.10f))
                    .clickable(onClick = onRemove),
                contentAlignment = Alignment.Center,
            ) {
                Text("Close card", color = palette.brand, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@Composable
private fun SettingsRow(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).clickable(onClick = onClick).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(Modifier.size(38.dp).clip(CircleShape).background(palette.mutedSurface), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = palette.foreground, modifier = Modifier.size(18.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = palette.foreground, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = palette.mutedForeground, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

private fun formatKrw(value: Double): String = "W" + "%,.0f".format(value)

private fun formatUsd(value: Double): String = "$" + "%,.2f".format(value)

private fun maskCard(number: String): String = "**** **** **** " + number.takeLast(4)

private fun formatCardNumber(number: String): String = number.chunked(4).joinToString(" ")
