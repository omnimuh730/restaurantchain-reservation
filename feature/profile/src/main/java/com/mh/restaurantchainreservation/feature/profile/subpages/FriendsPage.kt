package com.mh.restaurantchainreservation.feature.profile.subpages

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AlternateEmail
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.i18n.R as I18nR

private data class Contact(
    val id: String,
    val name: String,
    val username: String? = null,
    val phone: String? = null,
    val initials: String,
    val color: Color,
)

private data class FriendRequest(
    val contact: Contact,
    val requestedAt: String,
    val note: String,
)

private data class BlockedContact(
    val contact: Contact,
    val blockedAt: String,
    val reason: String,
)

private val PaletteColors = listOf(
    Color(0xFFE11D48),
    Color(0xFF2563EB),
    Color(0xFF059669),
    Color(0xFFD97706),
    Color(0xFF7C3AED),
    Color(0xFF0891B2),
    Color(0xFFDC2626),
    Color(0xFF0D9488),
)

@Composable
fun FriendsPage(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val friendRequests = remember {
        mutableStateListOf(
            FriendRequest(
                contact = Contact(
                    id = "req-1",
                    name = "Mina Park",
                    username = "minapark",
                    initials = "MP",
                    color = Color(0xFFDB2777),
                ),
                requestedAt = "2h ago",
                note = "Wants to plan dinners and split reservations with you.",
            ),
            FriendRequest(
                contact = Contact(
                    id = "req-2",
                    name = "Noah Williams",
                    username = "noahw",
                    initials = "NW",
                    color = Color(0xFF0891B2),
                ),
                requestedAt = "Yesterday",
                note = "Sent a friend request from your recent dining circle.",
            ),
        )
    }

    val contacts = remember {
        mutableStateListOf(
            Contact(id = "1", name = "Sarah Kim", username = "sarahkim", initials = "SK", color = Color(0xFFE11D48)),
            Contact(id = "2", name = "Marcus Johnson", username = "marcusj", initials = "MJ", color = Color(0xFF2563EB)),
            Contact(id = "3", name = "Emma Chen", username = "emmachen", initials = "EC", color = Color(0xFF059669)),
            Contact(id = "4", name = "David Park", phone = "+1 (555) 567-8901", initials = "DP", color = Color(0xFFD97706)),
            Contact(id = "5", name = "Olivia Tran", username = "oliviat", initials = "OT", color = Color(0xFF7C3AED)),
        )
    }

    val blockedContacts = remember { mutableStateListOf<BlockedContact>() }

    var showAddForm by rememberSaveable { mutableStateOf(false) }
    var newName by rememberSaveable { mutableStateOf("") }
    var newIdentifier by rememberSaveable { mutableStateOf("") }
    var pendingRemoveId by remember { mutableStateOf<String?>(null) }

    SubpageScaffold(
        title = stringResource(I18nR.string.friends_title),
        onBack = onBack,
        modifier = modifier,
    ) {
        FriendRequestsSection(
            requests = friendRequests,
            onApprove = { req ->
                contacts.removeAll { it.id == req.contact.id }
                contacts.add(0, req.contact)
                friendRequests.removeAll { it.contact.id == req.contact.id }
            },
            onReject = { req -> friendRequests.removeAll { it.contact.id == req.contact.id } },
            onBlock = { req ->
                friendRequests.removeAll { it.contact.id == req.contact.id }
                contacts.removeAll { it.id == req.contact.id }
                blockedContacts.removeAll { it.contact.id == req.contact.id }
                blockedContacts.add(
                    0,
                    BlockedContact(
                        contact = req.contact,
                        blockedAt = "Just now",
                        reason = "Blocked from pending friend requests",
                    ),
                )
            },
        )

        Spacer(Modifier.height(8.dp))

        AddFriendPanel(
            showForm = showAddForm,
            onToggle = { showAddForm = it },
            name = newName,
            onNameChange = { newName = it },
            identifier = newIdentifier,
            onIdentifierChange = { newIdentifier = it },
            onAdd = {
                if (newName.isNotBlank()) {
                    val initials = computeInitials(newName)
                    val isPhone = newIdentifier.startsWith("+") ||
                        newIdentifier.firstOrNull()?.isDigit() == true
                    contacts.add(
                        0,
                        Contact(
                            id = "c-${System.currentTimeMillis()}",
                            name = newName.trim(),
                            username = if (!isPhone && newIdentifier.isNotBlank()) newIdentifier.trim() else null,
                            phone = if (isPhone && newIdentifier.isNotBlank()) newIdentifier.trim() else null,
                            initials = initials,
                            color = PaletteColors.random(),
                        ),
                    )
                    newName = ""
                    newIdentifier = ""
                    showAddForm = false
                }
            },
        )

        Spacer(Modifier.height(8.dp))

        ContactsSection(
            contacts = contacts,
            onRequestRemove = { pendingRemoveId = it.id },
            onBlock = { contact ->
                contacts.removeAll { it.id == contact.id }
                blockedContacts.removeAll { it.contact.id == contact.id }
                blockedContacts.add(
                    0,
                    BlockedContact(
                        contact = contact,
                        blockedAt = "Just now",
                        reason = "Blocked from your contacts list",
                    ),
                )
            },
        )

        Spacer(Modifier.height(20.dp))

        BlockedContactsSection(
            blocked = blockedContacts,
            onUnblock = { id -> blockedContacts.removeAll { it.contact.id == id } },
        )

        Spacer(Modifier.height(40.dp))
    }

    if (pendingRemoveId != null) {
        RemoveContactDialog(
            onDismiss = { pendingRemoveId = null },
            onRemove = {
                val id = pendingRemoveId
                if (id != null) {
                    contacts.removeAll { it.id == id }
                }
                pendingRemoveId = null
            },
            onBlock = {
                val id = pendingRemoveId
                if (id != null) {
                    val contact = contacts.firstOrNull { it.id == id }
                    if (contact != null) {
                        contacts.removeAll { it.id == id }
                        blockedContacts.removeAll { it.contact.id == id }
                        blockedContacts.add(
                            0,
                            BlockedContact(
                                contact = contact,
                                blockedAt = "Just now",
                                reason = "Blocked from your contacts list",
                            ),
                        )
                    }
                }
                pendingRemoveId = null
            },
        )
    }
}

private fun computeInitials(name: String): String =
    name.trim()
        .split(' ', '\t', '\n')
        .filter { it.isNotEmpty() }
        .map { it.first().uppercaseChar() }
        .joinToString("")
        .take(2)
        .ifEmpty { "?" }

@Composable
private fun FriendRequestsSection(
    requests: SnapshotStateList<FriendRequest>,
    onApprove: (FriendRequest) -> Unit,
    onReject: (FriendRequest) -> Unit,
    onBlock: (FriendRequest) -> Unit,
) {
    if (requests.isEmpty()) return
    val palette = LocalRestaurantPalette.current
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Friend requests", color = palette.foreground, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(palette.brand.copy(alpha = 0.10f))
                    .padding(horizontal = 8.dp, vertical = 3.dp),
            ) {
                Text(
                    text = "${requests.size} pending",
                    color = palette.brand,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, palette.border.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                .background(palette.cardSurface),
        ) {
            requests.forEachIndexed { idx, req ->
                FriendRequestRow(
                    request = req,
                    showDivider = idx != requests.size - 1,
                    onApprove = { onApprove(req) },
                    onReject = { onReject(req) },
                    onBlock = { onBlock(req) },
                )
            }
        }
    }
}

@Composable
private fun FriendRequestRow(
    request: FriendRequest,
    showDivider: Boolean,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onBlock: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            ColoredAvatar(initials = request.contact.initials, color = request.contact.color, size = 44)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = request.contact.name,
                    color = palette.foreground,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(2.dp))
                val handle = request.contact.username?.let { "@$it" } ?: request.contact.phone.orEmpty()
                Text(
                    text = "$handle · ${request.requestedAt}",
                    color = palette.mutedForeground,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = request.note,
                    color = palette.mutedForeground,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
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
                    .height(40.dp)
                    .clip(CircleShape)
                    .border(1.dp, palette.border, CircleShape)
                    .background(palette.cardSurface)
                    .clickable(onClick = onReject),
                contentAlignment = Alignment.Center,
            ) {
                Text("Reject", color = palette.foreground, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .clip(CircleShape)
                    .background(palette.brand)
                    .clickable(onClick = onApprove),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(Modifier.width(6.dp))
                Text("Approve", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp)
                .clip(CircleShape)
                .background(palette.mutedSurface)
                .clickable(onClick = onBlock),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Outlined.Block,
                contentDescription = null,
                tint = palette.mutedForeground,
                modifier = Modifier.size(14.dp),
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = "Block this person",
                color = palette.mutedForeground,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        if (showDivider) {
            Spacer(Modifier.height(14.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(palette.border.copy(alpha = 0.5f)),
            )
        }
    }
}

@Composable
private fun AddFriendPanel(
    showForm: Boolean,
    onToggle: (Boolean) -> Unit,
    name: String,
    onNameChange: (String) -> Unit,
    identifier: String,
    onIdentifierChange: (String) -> Unit,
    onAdd: () -> Unit,
) {
    Crossfade(targetState = showForm, animationSpec = tween(220), label = "add-friend") { expanded ->
        if (expanded) {
            AddFriendForm(
                name = name,
                onNameChange = onNameChange,
                identifier = identifier,
                onIdentifierChange = onIdentifierChange,
                onAdd = onAdd,
                onClose = { onToggle(false) },
            )
        } else {
            AddFriendCta(onClick = { onToggle(true) })
        }
    }
}

@Composable
private fun AddFriendCta(onClick: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(20.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.dp, palette.border.copy(alpha = 0.6f), shape)
            .background(palette.cardSurface)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(palette.brand.copy(alpha = 0.10f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.PersonAdd,
                contentDescription = null,
                tint = palette.brand,
                modifier = Modifier.size(20.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text("Add a Friend", color = palette.foreground, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(2.dp))
            Text("Build your dining circle", color = palette.mutedForeground, fontSize = 13.sp)
        }
    }
}

@Composable
private fun AddFriendForm(
    name: String,
    onNameChange: (String) -> Unit,
    identifier: String,
    onIdentifierChange: (String) -> Unit,
    onAdd: () -> Unit,
    onClose: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(20.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.dp, palette.border.copy(alpha = 0.6f), shape)
            .background(palette.cardSurface)
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Add New Friend", color = palette.foreground, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(palette.mutedSurface)
                    .clickable(onClick = onClose),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = "Close",
                    tint = palette.mutedForeground,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(palette.border.copy(alpha = 0.5f)),
        )
        Spacer(Modifier.height(14.dp))

        FriendInputField(
            value = name,
            onValueChange = onNameChange,
            placeholder = "Full Name",
            leadingIcon = null,
        )
        Spacer(Modifier.height(10.dp))
        val isPhone = identifier.startsWith("+") || identifier.firstOrNull()?.isDigit() == true
        FriendInputField(
            value = identifier,
            onValueChange = onIdentifierChange,
            placeholder = "Username or Phone number",
            leadingIcon = if (isPhone) Icons.Outlined.Phone else Icons.Outlined.AlternateEmail,
        )
        Spacer(Modifier.height(14.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (name.isBlank()) palette.brand.copy(alpha = 0.4f) else palette.brand)
                .clickable(enabled = name.isNotBlank(), onClick = onAdd),
            contentAlignment = Alignment.Center,
        ) {
            Text("Add to Contacts", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun FriendInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector?,
) {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(shape)
            .border(1.dp, palette.border, shape)
            .background(palette.cardSurface)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = palette.mutedForeground,
                modifier = Modifier.size(16.dp),
            )
        }
        Box(modifier = Modifier.weight(1f)) {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
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
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun ContactsSection(
    contacts: SnapshotStateList<Contact>,
    onRequestRemove: (Contact) -> Unit,
    onBlock: (Contact) -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "${contacts.size} Contacts",
            color = palette.foreground,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 4.dp),
        )
        Spacer(Modifier.height(10.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, palette.border.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                .background(palette.cardSurface),
        ) {
            if (contacts.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("No friends yet", color = palette.foreground, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Add friends to easily invite them to reservations.",
                        color = palette.mutedForeground,
                        fontSize = 13.sp,
                    )
                }
            } else {
                contacts.forEachIndexed { idx, contact ->
                    ContactRow(
                        contact = contact,
                        showDivider = idx != contacts.size - 1,
                        onRemove = { onRequestRemove(contact) },
                        onBlock = { onBlock(contact) },
                    )
                }
            }
        }
    }
}

@Composable
private fun ContactRow(
    contact: Contact,
    showDivider: Boolean,
    onRemove: () -> Unit,
    onBlock: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ColoredAvatar(initials = contact.initials, color = contact.color, size = 40)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = contact.name,
                    color = palette.foreground,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(2.dp))
                val sub = contact.username?.let { "@$it" } ?: contact.phone.orEmpty()
                Text(
                    text = sub,
                    color = palette.mutedForeground,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            ContactIconButton(
                icon = Icons.Outlined.Close,
                onClick = onRemove,
                ariaLabel = "Remove ${contact.name}",
            )
            ContactIconButton(
                icon = Icons.Outlined.Block,
                onClick = onBlock,
                ariaLabel = "Block ${contact.name}",
            )
        }
        if (showDivider) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp)
                    .height(1.dp)
                    .background(palette.border.copy(alpha = 0.5f)),
            )
        }
    }
}

@Composable
private fun ContactIconButton(icon: ImageVector, onClick: () -> Unit, ariaLabel: String) {
    val palette = LocalRestaurantPalette.current
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = ariaLabel,
            tint = palette.mutedForeground,
            modifier = Modifier.size(16.dp),
        )
    }
}

@Composable
private fun BlockedContactsSection(
    blocked: SnapshotStateList<BlockedContact>,
    onUnblock: (String) -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Blocked list", color = palette.foreground, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(palette.mutedSurface)
                    .padding(horizontal = 8.dp, vertical = 3.dp),
            ) {
                Text(
                    text = "${blocked.size} blocked",
                    color = palette.mutedForeground,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, palette.border.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                .background(palette.cardSurface),
        ) {
            if (blocked.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("No blocked contacts", color = palette.foreground, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Block spammers from friend requests or your contacts list.",
                        color = palette.mutedForeground,
                        fontSize = 12.sp,
                    )
                }
            } else {
                blocked.forEachIndexed { idx, item ->
                    BlockedRow(item = item, showDivider = idx != blocked.size - 1, onUnblock = { onUnblock(item.contact.id) })
                }
            }
        }
    }
}

@Composable
private fun BlockedRow(
    item: BlockedContact,
    showDivider: Boolean,
    onUnblock: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
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
                Icon(
                    imageVector = Icons.Outlined.Block,
                    contentDescription = null,
                    tint = palette.mutedForeground,
                    modifier = Modifier.size(16.dp),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.contact.name,
                    color = palette.foreground,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "${item.reason} · ${item.blockedAt}",
                    color = palette.mutedForeground,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Row(
                modifier = Modifier
                    .height(34.dp)
                    .clip(CircleShape)
                    .border(1.dp, palette.border, CircleShape)
                    .clickable(onClick = onUnblock)
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Refresh,
                    contentDescription = null,
                    tint = palette.foreground,
                    modifier = Modifier.size(13.dp),
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "Unblock",
                    color = palette.foreground,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        if (showDivider) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp)
                    .height(1.dp)
                    .background(palette.border.copy(alpha = 0.5f)),
            )
        }
    }
}

@Composable
private fun ColoredAvatar(initials: String, color: Color, size: Int) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initials,
            color = Color.White,
            fontSize = (size * 0.32f).sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
        )
    }
}

@Composable
private fun RemoveContactDialog(
    onDismiss: () -> Unit,
    onRemove: () -> Unit,
    onBlock: () -> Unit,
) {
    val palette = LocalRestaurantPalette.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Remove Contact", color = palette.foreground, fontWeight = FontWeight.Bold)
        },
        text = {
            Text(
                "Are you sure you want to remove this contact from your friends list? They won't be notified.",
                color = palette.mutedForeground,
                fontSize = 14.sp,
                lineHeight = 20.sp,
            )
        },
        confirmButton = {
            TextButton(onClick = onRemove) {
                Text("Remove", color = palette.destructive, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            Row {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(I18nR.string.common_cancel), color = palette.mutedForeground, fontWeight = FontWeight.SemiBold)
                }
                Spacer(Modifier.width(4.dp))
                TextButton(onClick = onBlock) {
                    Text("Block", color = palette.foreground, fontWeight = FontWeight.SemiBold)
                }
            }
        },
        containerColor = palette.cardSurface,
    )
}
