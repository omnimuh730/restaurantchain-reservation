package com.mh.restaurantchainreservation.feature.profile.subpages

import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColors
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.ScrollState
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.TextStyle
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.mh.restaurantchainreservation.core.designsystem.tokens.BrandPink
import com.mh.restaurantchainreservation.core.designsystem.components.CollapsingSubpageHeaderIconButton
import com.mh.restaurantchainreservation.core.designsystem.components.DeterministicQrCode
import com.mh.restaurantchainreservation.core.designsystem.components.GlobalNotificationCenter
import com.mh.restaurantchainreservation.core.designsystem.components.RestaurantModalBottomSheet
import com.mh.restaurantchainreservation.core.designsystem.components.TabSelectionBounceBox
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.feature.profile.subpages.components.AnimatedAmountDisplay
import com.mh.restaurantchainreservation.feature.profile.subpages.components.Currency
import com.mh.restaurantchainreservation.feature.profile.subpages.components.MoneyKeypad
import com.mh.restaurantchainreservation.feature.profile.subpages.components.amountAsNumber
import com.mh.restaurantchainreservation.feature.profile.subpages.components.appendDigit
import com.mh.restaurantchainreservation.feature.profile.subpages.components.backspaceDigit
import com.mh.restaurantchainreservation.feature.profile.subpages.components.formatAmountString
import com.mh.restaurantchainreservation.feature.profile.data.MockProfileCreditCards
import com.mh.restaurantchainreservation.feature.profile.data.ProfileWalletStore
import com.mh.restaurantchainreservation.feature.profile.data.WalletCardRecord
import com.mh.restaurantchainreservation.feature.profile.data.WalletMutationResult
import com.mh.restaurantchainreservation.feature.profile.hub.AddNewCreditCardTile
import com.mh.restaurantchainreservation.feature.profile.hub.hubCardClickable
import com.mh.restaurantchainreservation.feature.profile.hub.HubCardPattern
import com.mh.restaurantchainreservation.feature.profile.hub.HubCardThemeId
import com.mh.restaurantchainreservation.feature.profile.hub.SharedHubCardFace
import com.mh.restaurantchainreservation.feature.profile.hub.SharedHubCardFaceModel
import com.mh.restaurantchainreservation.feature.profile.hub.hubCardThemeSpec
import com.mh.restaurantchainreservation.feature.profile.hub.HubStackedCarouselMotion
import com.mh.restaurantchainreservation.feature.profile.hub.hubCardThemeBackgroundBrush
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.math.min

private const val CARD_ACTION_TAB_TOP_UP = 0
private const val CARD_ACTION_TAB_WITHDRAW = 1
private const val CARD_ACTION_TAB_SEND = 2
private const val CARD_ACTION_TAB_RECEIVE = 3
private const val CARD_ACTION_TAB_SETTINGS = 4

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

private enum class CardAmountActionKind {
    TopUp,
    Withdraw,
    Send,
}

private fun WalletCardRecord.toProfileCreditCard(): ProfileCreditCard = ProfileCreditCard(
    id = id,
    nickname = nickname,
    holder = holder,
    number = number,
    expiry = expiry,
    themeId = themeId,
    pattern = pattern,
    frozen = frozen,
    externalUse = externalUse,
    krwBalance = krwBalance,
    usdBalance = usdBalance,
)

private fun ProfileCreditCard.toWalletCardRecord(): WalletCardRecord = WalletCardRecord(
    id = id,
    nickname = nickname,
    holder = holder,
    number = number,
    expiry = expiry,
    themeId = themeId,
    pattern = pattern,
    frozen = frozen,
    externalUse = externalUse,
    krwBalance = krwBalance,
    usdBalance = usdBalance,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreditCardsPage(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val palette = LocalRestaurantPalette.current
    val storeCards by ProfileWalletStore.cards.collectAsState()
    val cards = remember(storeCards) { storeCards.map { it.toProfileCreditCard() } }
    var activeIndex by rememberSaveable { mutableIntStateOf(0) }
    var cardActionsSheetOpen by rememberSaveable { mutableStateOf(false) }
    var cardActionsTab by rememberSaveable { mutableIntStateOf(CARD_ACTION_TAB_SETTINGS) }
    var showChooseCardThemeSheet by rememberSaveable { mutableStateOf(false) }
    var pendingPickTheme by remember { mutableStateOf(HubCardThemeId.Rose) }
    var pendingPickPattern by remember { mutableStateOf(hubCardThemeSpec(HubCardThemeId.Rose).pattern) }
    var pendingNewCardNumber by remember { mutableStateOf("") }
    var pendingNewCardNickname by remember { mutableStateOf("Tonight Rose") }

    val activeCard = if (activeIndex < cards.size) cards.getOrNull(activeIndex.coerceIn(0, cards.lastIndex.coerceAtLeast(0))) else null

    fun replaceActive(next: ProfileCreditCard) {
        ProfileWalletStore.updateCard(next.toWalletCardRecord())
    }

    val configuration = LocalConfiguration.current
    /** Same as [ChooseCardThemeBottomSheet] (`ChooseCardThemePage`). */
    val cardActionsSheetMaxHeight = (configuration.screenHeightDp * 0.78f).dp

    fun dismissCardActionsSheet() {
        cardActionsSheetOpen = false
    }

    fun openCardActionsSheet(tab: Int) {
        cardActionsTab = tab.coerceIn(CARD_ACTION_TAB_TOP_UP, CARD_ACTION_TAB_SETTINGS)
        cardActionsSheetOpen = true
    }

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
            modifier = Modifier.fillMaxSize(),
            contentHorizontalPadding = 0,
            headerActions = { progress ->
                CollapsingSubpageHeaderIconButton(
                    collapseProgress = progress,
                    onClick = openChooseNewCardTheme,
                    contentDescription = "Add new card",
                    imageVector = Icons.Outlined.Add,
                )
            },
        ) {
            CardCarousel(
                cards = cards,
                activeIndex = activeIndex,
                onSelect = { activeIndex = it },
                onCardClick = { page ->
                    activeIndex = page
                    openCardActionsSheet(CARD_ACTION_TAB_SETTINGS)
                },
                onAddNewCard = openChooseNewCardTheme,
                fullWidthPagerWithCenterGutters = true,
            )
            Spacer(Modifier.height(20.dp))
            Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
            activeCard?.let { card ->
                ActionGrid(
                    frozen = card.frozen,
                    onDeposit = { if (!card.frozen) openCardActionsSheet(CARD_ACTION_TAB_TOP_UP) },
                    onWithdraw = { if (!card.frozen) openCardActionsSheet(CARD_ACTION_TAB_WITHDRAW) },
                    onSend = { if (!card.frozen) openCardActionsSheet(CARD_ACTION_TAB_SEND) },
                    onReceive = { openCardActionsSheet(CARD_ACTION_TAB_RECEIVE) },
                    onSettings = { openCardActionsSheet(CARD_ACTION_TAB_SETTINGS) },
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
                ProfileWalletStore.addCard(
                    WalletCardRecord(
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
                activeIndex = cards.size
                showChooseCardThemeSheet = false
                GlobalNotificationCenter.success("Card created", "Your new Tonight card is ready.")
            },
        )

        if (cardActionsSheetOpen) {
            activeCard?.let { card ->
                val bodyScroll = rememberScrollState()
                RestaurantModalBottomSheet(onDismissRequest = ::dismissCardActionsSheet) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(cardActionsSheetMaxHeight)
                            .navigationBarsPadding(),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                        ) {
                            CardActionsTabRow(
                                selectedTab = cardActionsTab,
                                onTabSelected = { cardActionsTab = it },
                                frozen = card.frozen,
                                bodyScrollState = bodyScroll,
                            )
                            Spacer(Modifier.height(12.dp))
                        }
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .verticalScroll(bodyScroll)
                                .padding(horizontal = 20.dp)
                                .padding(top = 16.dp, bottom = 24.dp),
                        ) {
                            when (cardActionsTab) {
                            CARD_ACTION_TAB_TOP_UP, CARD_ACTION_TAB_WITHDRAW, CARD_ACTION_TAB_SEND -> {
                                if (card.frozen) {
                                    Text(
                                        text = "This card is frozen. Open the Settings tab to unfreeze, then you can top up, withdraw, or send.",
                                        color = palette.mutedForeground,
                                        fontSize = 14.sp,
                                        lineHeight = 20.sp,
                                        modifier = Modifier.padding(vertical = 8.dp),
                                    )
                                } else {
                                    key(card.id, cardActionsTab) {
                                        val actionKind = when (cardActionsTab) {
                                            CARD_ACTION_TAB_TOP_UP -> CardAmountActionKind.TopUp
                                            CARD_ACTION_TAB_WITHDRAW -> CardAmountActionKind.Withdraw
                                            else -> CardAmountActionKind.Send
                                        }
                                        CardAmountAction(
                                            card = card,
                                            actionKind = actionKind,
                                            onApply = { amount, currency, recipient ->
                                                val result = when (actionKind) {
                                                    CardAmountActionKind.TopUp ->
                                                        ProfileWalletStore.topUpCard(card.id, currency, amount)
                                                    CardAmountActionKind.Withdraw ->
                                                        ProfileWalletStore.withdrawCard(card.id, currency, amount)
                                                    CardAmountActionKind.Send ->
                                                        ProfileWalletStore.sendFromCard(
                                                            card.id,
                                                            currency,
                                                            amount,
                                                            recipient,
                                                        )
                                                }
                                                when (result) {
                                                    is WalletMutationResult.Success -> {
                                                        dismissCardActionsSheet()
                                                        GlobalNotificationCenter.success("Card updated", result.message)
                                                    }
                                                    is WalletMutationResult.Error ->
                                                        GlobalNotificationCenter.info("Unable to complete", result.message)
                                                }
                                            },
                                        )
                                    }
                                }
                            }
                            CARD_ACTION_TAB_RECEIVE -> {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    DeterministicQrCode(
                                        code = "tonight-card:${card.number}",
                                        modifier = Modifier.size(190.dp),
                                    )
                                    Spacer(Modifier.height(12.dp))
                                    Text(
                                        text = "Receive on this card",
                                        color = palette.foreground,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                    )
                                    Text(
                                        text = maskCard(card.number),
                                        color = palette.mutedForeground,
                                        fontSize = 13.sp,
                                    )
                                }
                            }
                            CARD_ACTION_TAB_SETTINGS -> {
                                CardSettingsPanel(
                                    card = card,
                                    canRemove = cards.size > 1,
                                    onToggleFrozen = {
                                        replaceActive(card.copy(frozen = !card.frozen))
                                        GlobalNotificationCenter.info(
                                            "Card settings",
                                            if (card.frozen) "Card unfrozen." else "Card frozen.",
                                        )
                                    },
                                    onToggleExternal = {
                                        replaceActive(card.copy(externalUse = !card.externalUse))
                                        GlobalNotificationCenter.info(
                                            "Card settings",
                                            if (card.externalUse) "External use disabled." else "External use enabled.",
                                        )
                                    },
                                    onRemove = {
                                        when (val result = ProfileWalletStore.removeCard(card.id)) {
                                            is WalletMutationResult.Success -> {
                                                activeIndex = min(activeIndex, (cards.size - 2).coerceAtLeast(0))
                                                dismissCardActionsSheet()
                                                GlobalNotificationCenter.warning(
                                                    "Card closed",
                                                    "${card.nickname} was removed.",
                                                )
                                            }
                                            is WalletMutationResult.Error ->
                                                GlobalNotificationCenter.info("Unable to remove", result.message)
                                        }
                                    },
                                )
                            }
                        }
                        }
                    }
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
    val carouselDensity = LocalDensity.current.density
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
                    (if (floored > maxWidth - 8.dp) maxWidth - 8.dp else floored).coerceAtLeast(1.dp)
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
                    modifier = with(HubStackedCarouselMotion) {
                        Modifier.hubStackedCarouselPage(
                            pageOffsetPages = d,
                            density = carouselDensity,
                        )
                    },
                ) {
                    if (page >= cards.size) {
                        AddNewCreditCardTile(
                            modifier = Modifier
                                .fillMaxWidth()
                                .hubCardClickable(
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
                                .hubCardClickable(
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
                drawCircle(color = RestaurantColors.Base.white, radius = fillR, center = c)
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
private fun CardActionsTabRow(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    frozen: Boolean,
    bodyScrollState: ScrollState,
) {
    val palette = LocalRestaurantPalette.current
    val density = LocalDensity.current
    val thresholdPx = remember(density) { with(density) { 48.dp.toPx() } }
    val collapseRaw = (bodyScrollState.value / thresholdPx).coerceIn(0f, 1f)
    val collapse by animateFloatAsState(
        targetValue = collapseRaw,
        animationSpec = tween(durationMillis = 160),
        label = "cardActionTabIconCollapse",
    )
    val tabPairs = remember {
        listOf(
            Icons.Outlined.ArrowDownward to "Top up",
            Icons.Outlined.ArrowUpward to "Withdraw",
            Icons.Outlined.Send to "Send",
            Icons.Outlined.QrCode to "Receive",
            Icons.Outlined.Settings to "Settings",
        )
    }
    val stripBg = palette.cardSurface
    val strokePx = with(density) { 1.dp.toPx() }
    val rowHeight = lerp(66f, 48f, collapse).dp
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(stripBg)
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
            modifier = Modifier
                .fillMaxWidth()
                .height(rowHeight),
            verticalAlignment = Alignment.Top,
        ) {
            tabPairs.forEachIndexed { index, (icon, label) ->
                val selected = index == selectedTab
                val muted = frozen && index <= CARD_ACTION_TAB_SEND
                CardActionTabCell(
                    modifier = Modifier.weight(1f),
                    icon = icon,
                    label = label,
                    selected = selected,
                    muted = muted,
                    collapse = collapse,
                    onClick = { onTabSelected(index) },
                )
            }
        }
    }
}

@Composable
private fun CardActionTabCell(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    selected: Boolean,
    muted: Boolean,
    collapse: Float,
    onClick: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val iconSlot = (24f * (1f - collapse)).coerceAtLeast(0f).dp
    val gapAfterIcon = (4f * (1f - collapse)).coerceAtLeast(0f).dp
    val iconAlpha = 1f - collapse
    val targetLabelColor = when {
        muted -> palette.mutedForeground.copy(alpha = 0.42f)
        selected -> palette.brand
        else -> RestaurantColors.Base.black
    }
    val labelColor by animateColorAsState(
        targetValue = targetLabelColor,
        animationSpec = spring(stiffness = 520f, dampingRatio = 0.85f),
        label = "card_action_tab_color",
    )
    val labelWeight = if (selected) FontWeight.Bold else FontWeight.Medium
    val labelToIndicatorGap = lerp(6f, 3f, collapse).dp
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clickable(onClick = onClick),
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(top = lerp(8f, 4f, collapse).dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TabSelectionBounceBox(
                isActive = selected,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .height(iconSlot)
                            .fillMaxWidth()
                            .graphicsLayer {
                                alpha = iconAlpha
                                translationY = -2f * collapse
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        if (iconAlpha > 0.02f) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = labelColor,
                                modifier = Modifier.size(22.dp),
                            )
                        }
                    }
                    Spacer(Modifier.height(gapAfterIcon))
                    Text(
                        text = label,
                        color = labelColor,
                        fontSize = 12.sp,
                        fontWeight = labelWeight,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Spacer(Modifier.height(labelToIndicatorGap))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                contentAlignment = Alignment.Center,
            ) {
                if (selected) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.52f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(999.dp))
                            .background(palette.brand),
                    )
                }
            }
        }
    }
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
        CreditCardGridAction(Icons.Outlined.ArrowDownward, "Top up", frozen, onDeposit, Modifier.weight(1f))
        CreditCardGridAction(Icons.Outlined.ArrowUpward, "Withdraw", frozen, onWithdraw, Modifier.weight(1f))
        CreditCardGridAction(Icons.Outlined.Send, "Send", frozen, onSend, Modifier.weight(1f))
        CreditCardGridAction(Icons.Outlined.QrCode, "Receive", false, onReceive, Modifier.weight(1f))
        CreditCardGridAction(Icons.Outlined.Settings, "Settings", false, onSettings, Modifier.weight(1f))
    }
}

@Composable
private fun CreditCardGridAction(icon: ImageVector, label: String, disabled: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
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
    val ledger by ProfileWalletStore.ledger.collectAsState()
    val txs = remember(card.id, card.krwBalance, card.usdBalance, ledger) {
        ProfileWalletStore.ledgerForCard(card.id)
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
        if (txs.isEmpty()) {
            Text(
                "No transactions yet.",
                color = palette.mutedForeground,
                fontSize = 13.sp,
                modifier = Modifier.padding(vertical = 8.dp),
            )
        } else {
            txs.forEach { tx ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 9.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(34.dp).clip(CircleShape).background(palette.mutedSurface), contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.CreditCard, null, tint = palette.foreground, modifier = Modifier.size(17.dp))
                    }
                    Text(tx.label, color = palette.foreground, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(start = 10.dp).weight(1f))
                    Text(tx.amountDisplay, color = if (tx.positive) palette.success else palette.foreground, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun CardListRow(card: ProfileCreditCard, selected: Boolean, onClick: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .hubCardClickable(onClick = onClick)
            .padding(14.dp),
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
            Icon(Icons.Outlined.CreditCard, null, tint = RestaurantColors.Base.white, modifier = Modifier.size(22.dp))
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
    actionKind: CardAmountActionKind,
    onApply: (Double, Currency, String) -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    var currency by rememberSaveable { mutableStateOf(Currency.KRW) }
    var amount by remember { mutableStateOf("") }
    var recipient by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(currency) {
        amount = ""
    }

    val numeric = amountAsNumber(amount)
    val needsRecipient = actionKind == CardAmountActionKind.Send
    val canConfirm = numeric > 0.0 && (!needsRecipient || recipient.trim().isNotEmpty())
    val available = if (currency == Currency.KRW) card.krwBalance else card.usdBalance
    val actionTitle = when (actionKind) {
        CardAmountActionKind.TopUp -> "Top up"
        CardAmountActionKind.Withdraw -> "Withdraw"
        CardAmountActionKind.Send -> "Send"
    }

    Column(Modifier.fillMaxWidth()) {
        SmallCardHeader(card)
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Available ${if (currency == Currency.KRW) "domestic" else "foreign"}: " +
                if (currency == Currency.KRW) formatKrw(available) else formatUsd(available),
            color = palette.mutedForeground,
            fontSize = 13.sp,
        )
        if (needsRecipient) {
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(palette.mutedSurface)
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("To", color = palette.mutedForeground, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.width(10.dp))
                BasicTextField(
                    value = recipient,
                    onValueChange = { recipient = it },
                    singleLine = true,
                    textStyle = TextStyle(
                        color = palette.foreground,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                    modifier = Modifier.weight(1f),
                    decorationBox = { inner ->
                        if (recipient.isEmpty()) {
                            Text("@username", color = palette.mutedForeground, fontSize = 14.sp)
                        }
                        inner()
                    },
                )
            }
        }
        Spacer(Modifier.height(16.dp))
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
            }
            CurrencyChip("USD", currency == Currency.USD, Modifier.weight(1f)) {
                currency = Currency.USD
            }
        }
        Spacer(Modifier.height(18.dp))
        key(currency) {
            MoneyKeypad(
                currency = currency,
                onDigit = { digit -> amount = appendDigit(amount, digit, currency) },
                onBackspace = {
                    val next = backspaceDigit(amount)
                    amount = if (next == "0") "" else next
                },
                onClear = { amount = "" },
            )
        }
        Spacer(Modifier.height(18.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(if (canConfirm) palette.brand else palette.mutedSurface)
                .clickable(enabled = canConfirm) {
                    onApply(amountAsNumber(amount), currency, recipient.trim())
                },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                actionTitle,
                color = if (canConfirm) RestaurantColors.Base.white else palette.mutedForeground,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
            )
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
            Icon(Icons.Outlined.CreditCard, null, tint = RestaurantColors.Base.white, modifier = Modifier.size(23.dp))
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
                    .background(BrandPink.Primary.copy(alpha = 0.10f))
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
