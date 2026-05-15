package com.mh.restaurantchainreservation.feature.profile.subpages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.mh.restaurantchainreservation.core.designsystem.components.CollapsingSubpageHeaderIconButton
import com.mh.restaurantchainreservation.core.designsystem.components.DeterministicQrCode
import com.mh.restaurantchainreservation.core.designsystem.components.GlobalNotificationCenter
import com.mh.restaurantchainreservation.core.designsystem.components.PageHeader
import com.mh.restaurantchainreservation.core.designsystem.components.RestaurantModalBottomSheet
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.feature.profile.subpages.components.AnimatedAmountDisplay
import com.mh.restaurantchainreservation.feature.profile.subpages.components.Currency
import com.mh.restaurantchainreservation.feature.profile.subpages.components.MoneyKeypad
import com.mh.restaurantchainreservation.feature.profile.subpages.components.amountAsNumber
import com.mh.restaurantchainreservation.feature.profile.subpages.components.appendDigit
import com.mh.restaurantchainreservation.feature.profile.subpages.components.backspaceDigit
import com.mh.restaurantchainreservation.feature.profile.subpages.components.formatAmountString
import com.mh.restaurantchainreservation.feature.profile.data.MockProfileCreditCards
import com.mh.restaurantchainreservation.feature.profile.hub.AddNewCreditCardTile
import com.mh.restaurantchainreservation.feature.profile.hub.HubCardPattern
import com.mh.restaurantchainreservation.feature.profile.hub.HubCardThemeId
import com.mh.restaurantchainreservation.feature.profile.hub.SharedHubCardFace
import com.mh.restaurantchainreservation.feature.profile.hub.SharedHubCardFaceModel
import com.mh.restaurantchainreservation.feature.profile.hub.hubCardThemeSpec
import com.mh.restaurantchainreservation.feature.profile.hub.HubStackedCarouselMotion
import com.mh.restaurantchainreservation.feature.profile.hub.hubCardThemeBackgroundBrush
import com.mh.restaurantchainreservation.core.i18n.R as I18nR
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.math.min

private enum class CardMode { Browse, Deposit, Withdraw, Send, Settings }

/** Horizontal inset when the focused card is centered; swipe brings neighbors through this gutter. */
private val CreditCardPagerGutter = 20.dp

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreditCardsPage(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val palette = LocalRestaurantPalette.current
    val cards = remember {
        mutableStateListOf<ProfileCreditCard>().apply {
            MockProfileCreditCards.cards.forEach { def ->
                add(
                    ProfileCreditCard(
                        id = def.id,
                        nickname = def.nickname,
                        holder = MockProfileCreditCards.HOLDER,
                        number = def.number,
                        expiry = def.expiry,
                        themeId = def.themeId,
                        pattern = def.pattern,
                        krwBalance = def.krwBalance,
                        usdBalance = def.usdBalance,
                    ),
                )
            }
        }
    }
    var activeIndex by rememberSaveable { mutableIntStateOf(0) }
    var mode by rememberSaveable { mutableStateOf(CardMode.Browse) }
    var showChooseCardThemeSheet by rememberSaveable { mutableStateOf(false) }
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

    val sheetBackLabel = stringResource(I18nR.string.common_action_back)
    val configuration = LocalConfiguration.current
    val creditCardSheetMaxHeight = (configuration.screenHeightDp * 0.78f).dp

    Box(modifier = modifier.fillMaxSize()) {
        val openChooseNewCardTheme = {
            val index = cards.size + 1
            pendingPickTheme = HubCardThemeId.Rose
            pendingPickPattern = hubCardThemeSpec(HubCardThemeId.Rose).pattern
            pendingNewCardNumber = "4890${(100000000000 + index * 43129).toString().takeLast(12)}"
            pendingNewCardNickname = "Tonight ${HubCardThemeId.Rose.name}"
            showChooseCardThemeSheet = true
        }
        SubpageScaffold(
            title = "Credit cards",
            onBack = onBack,
            contentHorizontalPadding = 0,
            titleFontExpandedSp = 33f,
            titleFontCollapsedSp = 19f,
            titleLineHeightExpandedSp = 39f,
            titleLineHeightCollapsedSp = 23f,
            headerActions = { collapseProgress ->
                CollapsingSubpageHeaderIconButton(
                    collapseProgress = collapseProgress,
                    onClick = openChooseNewCardTheme,
                    contentDescription = "Add new card",
                    imageVector = Icons.Outlined.Add,
                )
            },
        ) {
            Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "Multi-currency · Tonight Card",
                    modifier = Modifier.fillMaxWidth(),
                    color = palette.mutedForeground,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Normal,
                )
            }
            Spacer(Modifier.height(20.dp))
            CardCarousel(
                cards = cards,
                activeIndex = activeIndex,
                onSelect = { activeIndex = it },
                onCardClick = { page ->
                    activeIndex = page
                    mode = CardMode.Settings
                },
                onAddNewCard = openChooseNewCardTheme,
                fullWidthPagerWithCenterGutters = true,
            )
            Spacer(Modifier.height(20.dp))
            Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
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

        ChooseCardThemeBottomSheet(
            visible = showChooseCardThemeSheet,
            onDismiss = { showChooseCardThemeSheet = false },
            previewNickname = pendingNewCardNickname,
            holder = MockProfileCreditCards.HOLDER,
            lastFour = pendingNewCardNumber.takeLast(4),
            fullCardNumber = pendingNewCardNumber,
            selectedThemeId = pendingPickTheme,
            selectedPattern = pendingPickPattern,
            onThemeSelected = {
                pendingPickTheme = it
                pendingNewCardNickname = "Tonight ${it.name}"
            },
            onPatternSelected = { pendingPickPattern = it },
            onConfirm = { funding ->
                val index = cards.size + 1
                cards.add(
                    ProfileCreditCard(
                        id = "card-$index",
                        nickname = pendingNewCardNickname,
                        holder = MockProfileCreditCards.HOLDER,
                        number = pendingNewCardNumber,
                        expiry = "12/${29 + index}",
                        themeId = pendingPickTheme,
                        pattern = pendingPickPattern,
                        krwBalance = funding.initialKrw,
                        usdBalance = funding.initialUsd,
                    ),
                )
                activeIndex = cards.lastIndex
                showChooseCardThemeSheet = false
                GlobalNotificationCenter.success("Card created", "Your new Tonight card is ready.")
            },
        )

        if (mode == CardMode.Deposit || mode == CardMode.Withdraw || mode == CardMode.Send) {
            activeCard?.let { card ->
                RestaurantModalBottomSheet(onDismissRequest = { mode = CardMode.Browse }) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = creditCardSheetMaxHeight)
                            .verticalScroll(rememberScrollState())
                            .navigationBarsPadding()
                            .padding(horizontal = 20.dp)
                            .padding(bottom = 24.dp),
                    ) {
                        val sheetTitle = when (mode) {
                            CardMode.Deposit -> "Top up card"
                            CardMode.Withdraw -> "Withdraw from card"
                            else -> "Send from card"
                        }
                        PageHeader(
                            title = sheetTitle,
                            onBack = { mode = CardMode.Browse },
                            backContentDescription = sheetBackLabel,
                        )
                        CardAmountAction(
                            card = card,
                            onApply = { amount, currency ->
                                val signed = if (mode == CardMode.Withdraw || mode == CardMode.Send) -amount else amount
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
            }
        }

        if (mode == CardMode.Settings) {
            activeCard?.let { card ->
                RestaurantModalBottomSheet(onDismissRequest = { mode = CardMode.Browse }) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = creditCardSheetMaxHeight)
                            .verticalScroll(rememberScrollState())
                            .navigationBarsPadding()
                            .padding(horizontal = 20.dp)
                            .padding(bottom = 24.dp),
                    ) {
                        PageHeader(
                            title = "Card settings",
                            onBack = { mode = CardMode.Browse },
                            backContentDescription = sheetBackLabel,
                        )
                        CardSettingsPanel(
                            card = card,
                            canRemove = cards.size > 1,
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
            RestaurantModalBottomSheet(onDismissRequest = { qrCard = null }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = creditCardSheetMaxHeight)
                        .verticalScroll(rememberScrollState())
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    PageHeader(
                        title = "Receive on this card",
                        onBack = { qrCard = null },
                        backContentDescription = sheetBackLabel,
                    )
                    DeterministicQrCode(
                        code = "tonight-card:${card.number}",
                        modifier = Modifier.size(190.dp),
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(card.nickname, color = palette.foreground, fontWeight = FontWeight.Bold)
                    Text(maskCard(card.number), color = palette.mutedForeground, fontSize = 13.sp)
                }
            }
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
    onCardClick: (Int) -> Unit,
    onAddNewCard: () -> Unit,
    /**
     * When true, the pager uses the full screen width; each page is narrower than the screen with
     * [CreditCardPagerGutter] on each side so the centered card has side space, while [HorizontalPager]
     * `contentPadding` lets adjacent cards scroll into those gutters.
     */
    fullWidthPagerWithCenterGutters: Boolean = false,
) {
    val palette = LocalRestaurantPalette.current
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

    val pageCount = cards.size + 1

    Column(modifier = Modifier.fillMaxWidth()) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
        ) {
            val cardWidth = remember(maxWidth, fullWidthPagerWithCenterGutters) {
                if (fullWidthPagerWithCenterGutters) {
                    (maxWidth - CreditCardPagerGutter * 2).coerceAtLeast(1.dp)
                } else {
                    val target = maxWidth * 0.92f
                    val capped = if (target > 364.dp) 364.dp else target
                    val floored = if (capped < 272.dp) 272.dp else capped
                    if (floored > maxWidth - 8.dp) maxWidth - 8.dp else floored
                }
            }
            val sidePad = remember(maxWidth, cardWidth, fullWidthPagerWithCenterGutters) {
                if (fullWidthPagerWithCenterGutters) {
                    CreditCardPagerGutter
                } else {
                    ((maxWidth - cardWidth) / 2).coerceAtLeast(4.dp)
                }
            }
            val overlap = cardWidth * 0.22f

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
                Box(
                    modifier = Modifier
                        .zIndex(HubStackedCarouselMotion.zIndexForPage(d))
                        .graphicsLayer {
                            transformOrigin = TransformOrigin(0.5f, 0.52f)
                            cameraDistance = 14f * density
                            rotationZ = HubStackedCarouselMotion.rotationZForPage(d)
                            val scale = HubStackedCarouselMotion.scaleForPage(d)
                            scaleX = scale
                            scaleY = scale
                            translationX = HubStackedCarouselMotion.translationX(d, density)
                            translationY = HubStackedCarouselMotion.translationY(d, density)
                            alpha = HubStackedCarouselMotion.alphaForPage(d)
                        },
                ) {
                    if (page >= cards.size) {
                        AddNewCreditCardTile(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    role = Role.Button,
                                    onClickLabel = "Add new card",
                                    onClick = onAddNewCard,
                                ),
                        )
                    } else {
                        CardFace(
                            card = cards[page],
                            reveal = false,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    role = Role.Button,
                                    onClickLabel = "Card settings",
                                    onClick = { onCardClick(page) },
                                ),
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(pageCount) { index ->
                if (index > 0) Spacer(Modifier.width(6.dp))
                val selected = index == pagerState.currentPage
                if (selected) {
                    Box(
                        modifier = Modifier
                            .width(22.dp)
                            .height(6.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(palette.brand),
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(palette.mutedForeground.copy(alpha = 0.28f)),
                    )
                }
            }
            Spacer(Modifier.width(10.dp))
            CarouselDashedAddButton(onClick = onAddNewCard)
        }
    }
}

@Composable
private fun CarouselDashedAddButton(onClick: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = Modifier
            .size(36.dp)
            .clickable(
                role = Role.Button,
                onClickLabel = "Add new card",
                onClick = onClick,
            )
            .drawBehind {
                val c = Offset(size.width / 2f, size.height / 2f)
                val fillR = size.minDimension / 2f - 1.dp.toPx()
                drawCircle(color = Color.White, radius = fillR, center = c)
                val strokeW = 2.dp.toPx()
                val r = (fillR - strokeW / 2f).coerceAtLeast(4f)
                drawCircle(
                    color = palette.brand,
                    radius = r,
                    center = c,
                    style = Stroke(
                        width = strokeW,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(9f, 7f), 0f),
                    ),
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Outlined.Add,
            contentDescription = null,
            tint = palette.brand,
            modifier = Modifier.size(20.dp),
        )
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
    card: ProfileCreditCard,
    onApply: (Double, Currency) -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    var currency by rememberSaveable { mutableStateOf(Currency.KRW) }
    var amount by rememberSaveable(currency) { mutableStateOf("") }
    val numeric = amountAsNumber(amount)
    val canConfirm = numeric > 0.0

    Column(Modifier.fillMaxWidth()) {
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
    onToggleFrozen: () -> Unit,
    onToggleExternal: () -> Unit,
    onRemove: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    var reveal by rememberSaveable { mutableStateOf(false) }
    Column(Modifier.fillMaxWidth()) {
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
