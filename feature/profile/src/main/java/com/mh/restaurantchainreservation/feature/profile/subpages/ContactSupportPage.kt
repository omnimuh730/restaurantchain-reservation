package com.mh.restaurantchainreservation.feature.profile.subpages

import com.mh.restaurantchainreservation.core.designsystem.tokens.RestaurantColors
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.components.icons.TonightLogoMark
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.i18n.R as I18nR
import kotlin.math.roundToInt
import kotlin.random.Random
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class ChatSender { Agent, User }

private data class ChatOption(val label: String, val payload: String)

private data class ChatMessage(
    val id: String,
    val sender: ChatSender,
    val text: String? = null,
    val options: List<ChatOption> = emptyList(),
    val timestamp: Long,
)

private enum class SessionStatus { Open, Resolved }

private data class ChatSession(
    val id: String,
    val date: String,
    val status: SessionStatus,
    val topic: String,
    val ticketId: String,
    val lastMessage: String,
    val messages: List<ChatMessage>,
)

private val HumanQuickReplies = listOf(
    ChatOption("I want to change my reservation", "change"),
    ChatOption("I have a question about QR Pay", "qrpay"),
    ChatOption("I need to report an issue", "report"),
    ChatOption("I'd rather type out my issue", "type"),
)

private fun agentReply(payload: String, now: Long): List<ChatMessage> {
    fun base(text: String, options: List<ChatOption> = emptyList()): ChatMessage = ChatMessage(
        id = "${now}-${Random.nextInt()}",
        sender = ChatSender.Agent,
        text = text,
        options = options,
        timestamp = now,
    )
    return when (payload) {
        "change" -> listOf(
            base(
                text = "Happy to help with that! Which of these matches what you need?",
                options = listOf(
                    ChatOption("Update date or guest count", "change_details"),
                    ChatOption("Add or change my name", "change_name"),
                    ChatOption("Different listing", "switch"),
                    ChatOption("Different host", "host"),
                    ChatOption("Other", "type"),
                ),
            ),
        )
        "qrpay" -> listOf(
            base(
                text = "Sorry to hear about the QR Pay trouble. What happened?",
                options = listOf(
                    ChatOption("Scan didn't register", "scan_failed"),
                    ChatOption("Payment failed", "payment_failed"),
                    ChatOption("How does QR Pay work?", "qrpay_info"),
                ),
            ),
        )
        else -> listOf(base("Could you share a bit more detail so we can help?"))
    }
}

@Composable
fun ContactSupportPage(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val palette = LocalRestaurantPalette.current

    val sessions = remember {
        mutableStateListOf(
            ChatSession(
                id = "chat-1",
                date = "Oct 24",
                status = SessionStatus.Resolved,
                topic = "Reservation Modification",
                ticketId = "#REQ-8921",
                lastMessage = "Got it - your booking is moved to next Thursday at 7:30 PM.",
                messages = listOf(
                    ChatMessage("m1", ChatSender.Agent, "Hi Sam, let's get you sorted out.", timestamp = System.currentTimeMillis() - 100_000),
                    ChatMessage("m2", ChatSender.User, "I want to change my reservation", timestamp = System.currentTimeMillis() - 60_000),
                    ChatMessage("m3", ChatSender.Agent, "Got it - your booking is moved to next Thursday at 7:30 PM.", timestamp = System.currentTimeMillis() - 30_000),
                ),
            ),
            ChatSession(
                id = "chat-2",
                date = "Oct 22",
                status = SessionStatus.Resolved,
                topic = "QR Pay Issue",
                ticketId = "#REQ-8744",
                lastMessage = "Refund processed - it should appear in 3-5 business days.",
                messages = emptyList(),
            ),
        )
    }
    var activeSessionId by remember { mutableStateOf<String?>(null) }
    var sessionToDelete by remember { mutableStateOf<String?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        SupportInboxScreen(
            sessions = sessions,
            onBack = onBack,
            onOpen = { id -> activeSessionId = id },
            onDeleteRequest = { id -> sessionToDelete = id },
            onStartNew = {
                val ticketId = "#REQ-${(1000..9999).random()}"
                val newSession = ChatSession(
                    id = "chat-${System.currentTimeMillis()}",
                    date = "Today",
                    status = SessionStatus.Open,
                    topic = "New Request",
                    ticketId = ticketId,
                    lastMessage = "Started a new conversation...",
                    messages = emptyList(),
                )
                sessions.add(0, newSession)
                activeSessionId = newSession.id
            },
        )

        val activeSession = activeSessionId?.let { id -> sessions.firstOrNull { it.id == id } }
        if (activeSession != null) {
            SupportChatOverlay(
                session = activeSession,
                onClose = { activeSessionId = null },
                onUpdateSession = { id, msgs ->
                    val idx = sessions.indexOfFirst { it.id == id }
                    if (idx >= 0) {
                        val cur = sessions[idx]
                        val last = msgs.lastOrNull()
                        val lastText = last?.text ?: "Sent an option"
                        val updatedTopic = if (cur.topic == "New Request" && last?.sender == ChatSender.User && last.text != null) {
                            last.text.take(30)
                        } else {
                            cur.topic
                        }
                        sessions[idx] = cur.copy(
                            messages = msgs,
                            lastMessage = lastText,
                            status = SessionStatus.Open,
                            topic = updatedTopic,
                        )
                    }
                },
            )
        }

        if (sessionToDelete != null) {
            DeleteConversationDialog(
                onCancel = { sessionToDelete = null },
                onConfirm = {
                    val id = sessionToDelete
                    if (id != null) sessions.removeAll { it.id == id }
                    sessionToDelete = null
                    if (activeSessionId == id) activeSessionId = null
                },
            )
        }
    }
}

@Composable
private fun SupportInboxScreen(
    sessions: SnapshotStateList<ChatSession>,
    onBack: () -> Unit,
    onOpen: (String) -> Unit,
    onDeleteRequest: (String) -> Unit,
    onStartNew: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    SubpageScaffold(
        title = "Support Inbox",
        onBack = onBack,
    ) {
        Text(
            text = "Your conversations",
            color = palette.foreground,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.2f).sp,
        )
        Spacer(Modifier.height(14.dp))

        if (sessions.isEmpty()) {
            EmptyInboxCard()
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                sessions.forEach { session ->
                    SwipeableSessionRow(
                        session = session,
                        onOpen = { onOpen(session.id) },
                        onDeleteRequest = { onDeleteRequest(session.id) },
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(palette.foreground)
                .clickable(onClick = onStartNew),
            contentAlignment = Alignment.Center,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = null,
                    tint = palette.cardSurface,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Start a new conversation",
                    color = palette.cardSurface,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun EmptyInboxCard() {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, palette.border, RoundedCornerShape(20.dp))
            .background(palette.mutedSurface)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Outlined.ChatBubbleOutline,
            contentDescription = null,
            tint = palette.mutedForeground,
            modifier = Modifier.size(28.dp),
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "No recent support chats",
            color = palette.foreground,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "When you contact support, your history will appear here.",
            color = palette.mutedForeground,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun SwipeableSessionRow(
    session: ChatSession,
    onOpen: () -> Unit,
    onDeleteRequest: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val density = LocalDensity.current
    val actionWidthPx = with(density) { 88.dp.toPx() }
    val openThresholdPx = with(density) { 40.dp.toPx() }
    val swipeOffset = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    var isOpen by remember { mutableStateOf(false) }

    val progress = (-swipeOffset.value / actionWidthPx).coerceIn(0f, 1f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(RestaurantColors.Semantic.errorBright.copy(alpha = progress))
                .padding(end = 0.dp),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .width(88.dp)
                    .fillMaxHeight()
                    .clickable(enabled = isOpen) { onDeleteRequest() },
                contentAlignment = Alignment.Center,
            ) {
                val scale = 0.7f + 0.3f * progress
                Icon(
                    imageVector = Icons.Outlined.DeleteOutline,
                    contentDescription = "Delete conversation",
                    tint = RestaurantColors.Base.white,
                    modifier = Modifier
                        .size((24 * scale).dp),
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(swipeOffset.value.roundToInt(), 0) }
                .clip(RoundedCornerShape(20.dp))
                .background(palette.cardSurface)
                .border(1.dp, palette.border, RoundedCornerShape(20.dp))
                .pointerInput(session.id) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { _, dragAmount ->
                            scope.launch {
                                val target = (swipeOffset.value + dragAmount).coerceIn(-actionWidthPx - 20, 0f)
                                swipeOffset.snapTo(target)
                            }
                        },
                        onDragEnd = {
                            scope.launch {
                                if (swipeOffset.value < -openThresholdPx) {
                                    swipeOffset.animateTo(-actionWidthPx, spring(stiffness = 400f, dampingRatio = 0.85f))
                                    isOpen = true
                                } else {
                                    swipeOffset.animateTo(0f, spring(stiffness = 400f, dampingRatio = 0.85f))
                                    isOpen = false
                                }
                            }
                        },
                        onDragCancel = {
                            scope.launch {
                                swipeOffset.animateTo(0f, spring(stiffness = 400f, dampingRatio = 0.85f))
                                isOpen = false
                            }
                        },
                    )
                }
                .clickable {
                    if (isOpen) {
                        scope.launch {
                            swipeOffset.animateTo(0f, spring(stiffness = 400f, dampingRatio = 0.85f))
                            isOpen = false
                        }
                    } else {
                        onOpen()
                    }
                }
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            if (session.status == SessionStatus.Open) {
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .fillMaxHeight()
                        .background(palette.foreground, RoundedCornerShape(2.dp)),
                )
            }
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(palette.mutedSurface)
                    .border(1.dp, palette.border, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                TonightLogoMark(
                    color = palette.brand,
                    modifier = Modifier.size(28.dp),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                ) {
                    Text(
                        text = session.topic,
                        color = palette.foreground,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = session.date,
                        color = palette.mutedForeground,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusChip(status = session.status)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = session.ticketId,
                        color = palette.mutedForeground,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = session.lastMessage,
                    color = palette.mutedForeground,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun StatusChip(status: SessionStatus) {
    val palette = LocalRestaurantPalette.current
    val (bg, fg, label) = when (status) {
        SessionStatus.Open -> Triple(RestaurantColors.Semantic.successOpenBg, RestaurantColors.Semantic.successDark, "OPEN")
        SessionStatus.Resolved -> Triple(palette.mutedSurface, palette.mutedForeground, "RESOLVED")
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .padding(horizontal = 6.dp, vertical = 2.dp),
    ) {
        Text(
            text = label,
            color = fg,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
        )
    }
}

@Composable
private fun DeleteConversationDialog(
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RestaurantColors.Base.blackAlpha(0.4f))
            .clickable(onClick = onCancel),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(palette.cardSurface)
                .clickable(enabled = false) {}
                .padding(24.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(RestaurantColors.Semantic.errorSurface),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DeleteOutline,
                        contentDescription = null,
                        tint = RestaurantColors.Semantic.errorBright,
                        modifier = Modifier.size(22.dp),
                    )
                }
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(palette.mutedSurface)
                        .clickable(onClick = onCancel),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = stringResource(I18nR.string.common_action_close),
                        tint = palette.mutedForeground,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
            Spacer(Modifier.height(14.dp))
            Text(
                "Delete conversation?",
                color = palette.foreground,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "This action cannot be undone. Are you sure you want to permanently remove this chat history?",
                color = palette.mutedForeground,
                fontSize = 14.sp,
                lineHeight = 20.sp,
            )
            Spacer(Modifier.height(20.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(palette.mutedSurface)
                        .clickable(onClick = onCancel),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        stringResource(I18nR.string.common_cancel),
                        color = palette.foreground,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(RestaurantColors.Semantic.errorBright)
                        .clickable(onClick = onConfirm),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        stringResource(I18nR.string.common_delete),
                        color = RestaurantColors.Base.white,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
private fun SupportChatOverlay(
    session: ChatSession,
    onClose: () -> Unit,
    onUpdateSession: (String, List<ChatMessage>) -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val messages = remember(session.id) {
        mutableStateListOf<ChatMessage>().apply {
            if (session.messages.isNotEmpty()) {
                addAll(session.messages)
            } else {
                val now = System.currentTimeMillis()
                add(
                    ChatMessage(
                        id = "${now}-1",
                        sender = ChatSender.Agent,
                        text = "Hi there, let's get you sorted out. We'll ask a few questions and then connect you with a human teammate.",
                        timestamp = now,
                    ),
                )
                add(
                    ChatMessage(
                        id = "${now}-2",
                        sender = ChatSender.Agent,
                        text = "How can we help with your account today? Select an option below or type out your issue.",
                        options = HumanQuickReplies,
                        timestamp = now + 1,
                    ),
                )
            }
        }
    }
    var input by remember(session.id) { mutableStateOf("") }
    var typing by remember(session.id) { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(messages.size, typing) {
        val target = (messages.size - 1).coerceAtLeast(0)
        listState.animateScrollToItem(target)
    }

    LaunchedEffect(session.id) {
        if (session.messages.isEmpty()) {
            onUpdateSession(session.id, messages.toList())
        }
    }

    fun pushUserMessage(text: String): List<ChatMessage> {
        val now = System.currentTimeMillis()
        messages.add(
            ChatMessage(
                id = "${now}-u",
                sender = ChatSender.User,
                text = text,
                timestamp = now,
            ),
        )
        val snapshot = messages.toList()
        onUpdateSession(session.id, snapshot)
        return snapshot
    }

    fun pushAgent(replies: List<ChatMessage>) {
        messages.addAll(replies)
        onUpdateSession(session.id, messages.toList())
    }

    fun send(payload: String, displayText: String?) {
        if (displayText != null) pushUserMessage(displayText)
        input = ""
        if (payload == "type") return
        scope.launch {
            typing = true
            delay(1200L + Random.nextLong(0, 800L))
            typing = false
            pushAgent(agentReply(payload, System.currentTimeMillis()))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.pageBackground)
            .statusBarsPadding(),
    ) {
        ChatHeader(onClose = onClose)
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(palette.cardSurface)
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item("today") {
                Spacer(Modifier.height(12.dp))
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Today",
                        color = palette.mutedForeground,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Spacer(Modifier.height(8.dp))
            }
            items(items = messages, key = { it.id }) { msg ->
                val nextIdx = messages.indexOfFirst { it.id == msg.id } + 1
                val isAgent = msg.sender == ChatSender.Agent
                val isLastInGroup = isAgent && (nextIdx >= messages.size || messages[nextIdx].sender != ChatSender.Agent)
                ChatBubble(
                    msg = msg,
                    showAvatar = isLastInGroup,
                    onOptionClick = { opt -> send(opt.payload, opt.label) },
                )
            }
            if (typing) {
                item("typing") {
                    TypingIndicator()
                }
            }
            item("bottom-pad") { Spacer(Modifier.height(12.dp)) }
        }
        ChatInputBar(
            value = input,
            onValueChange = { input = it },
            onSubmit = {
                val trimmed = input.trim()
                if (trimmed.isNotBlank()) send(trimmed, trimmed)
            },
        )
    }
}

@Composable
private fun ChatHeader(onClose: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(palette.cardSurface)
            .padding(horizontal = 14.dp, vertical = 12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onClose),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = stringResource(I18nR.string.common_action_back),
                    tint = palette.foreground,
                    modifier = Modifier.size(20.dp),
                )
            }
            Spacer(Modifier.weight(1f))
            Spacer(Modifier.size(36.dp))
        }
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                "Support Team",
                color = palette.foreground,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Phone,
                    contentDescription = null,
                    tint = palette.mutedForeground,
                    modifier = Modifier.size(11.dp),
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    "+1 (800) 123-4567",
                    color = palette.mutedForeground,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(palette.border),
    )
}

@Composable
private fun ChatBubble(
    msg: ChatMessage,
    showAvatar: Boolean,
    onOptionClick: (ChatOption) -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val isUser = msg.sender == ChatSender.User
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
    ) {
        if (!isUser) {
            Box(modifier = Modifier.size(36.dp), contentAlignment = Alignment.BottomCenter) {
                if (showAvatar) {
                    TonightLogoMark(
                        color = palette.brand,
                        modifier = Modifier.size(36.dp),
                    )
                }
            }
            Spacer(Modifier.width(8.dp))
        }
        Column(
            modifier = Modifier.fillMaxWidth(0.85f),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start,
        ) {
            if (msg.text != null) {
                val bubbleColor = if (isUser) palette.foreground else palette.mutedSurface
                val textColor = if (isUser) palette.cardSurface else palette.foreground
                val shape = if (isUser) {
                    RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 6.dp)
                } else {
                    RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 6.dp, bottomEnd = 20.dp)
                }
                Box(
                    modifier = Modifier
                        .clip(shape)
                        .background(bubbleColor)
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                ) {
                    Text(
                        text = msg.text,
                        color = textColor,
                        fontSize = 15.sp,
                        lineHeight = 20.sp,
                    )
                }
            }
            if (msg.options.isNotEmpty()) {
                Spacer(Modifier.height(6.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, palette.border, RoundedCornerShape(16.dp))
                        .background(palette.cardSurface),
                ) {
                    msg.options.forEachIndexed { i, opt ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOptionClick(opt) }
                                .padding(horizontal = 14.dp, vertical = 14.dp),
                        ) {
                            Text(
                                text = opt.label,
                                color = palette.foreground,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                        if (i < msg.options.size - 1) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(palette.border),
                            )
                        }
                    }
                }
            }
        }
        if (isUser) {
            Spacer(Modifier.width(8.dp))
            Box(modifier = Modifier.size(36.dp))
        }
    }
}

@Composable
private fun TypingIndicator() {
    val palette = LocalRestaurantPalette.current
    Row(verticalAlignment = Alignment.Bottom) {
        Box(modifier = Modifier.size(36.dp), contentAlignment = Alignment.BottomCenter) {
            TonightLogoMark(color = palette.brand, modifier = Modifier.size(36.dp))
        }
        Spacer(Modifier.width(8.dp))
        Column {
            val transition = rememberInfiniteTransition(label = "typing")
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 6.dp, bottomEnd = 20.dp))
                    .background(palette.mutedSurface)
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                repeat(3) { i ->
                    val phase by transition.animateFloat(
                        initialValue = 0.3f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(durationMillis = 800, delayMillis = i * 150),
                            repeatMode = RepeatMode.Reverse,
                        ),
                        label = "dot$i",
                    )
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(palette.mutedForeground.copy(alpha = phase)),
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Support Team is typing...",
                color = palette.mutedForeground,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun ChatInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
            .navigationBarsPadding()
            .background(palette.cardSurface),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(palette.border),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .border(1.dp, palette.border, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = null,
                    tint = palette.foreground,
                    modifier = Modifier.size(20.dp),
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .clip(CircleShape)
                    .border(1.dp, palette.border, CircleShape)
                    .background(palette.cardSurface)
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = "Type a message",
                        color = palette.mutedForeground,
                        fontSize = 14.sp,
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    cursorBrush = SolidColor(palette.brand),
                    textStyle = LocalTextStyle.current.merge(
                        TextStyle(color = palette.foreground, fontSize = 14.sp),
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            val active = value.isNotBlank()
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(if (active) palette.foreground else palette.mutedSurface)
                    .clickable(enabled = active, onClick = onSubmit),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Send,
                    contentDescription = "Send message",
                    tint = if (active) palette.cardSurface else palette.mutedForeground,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}
