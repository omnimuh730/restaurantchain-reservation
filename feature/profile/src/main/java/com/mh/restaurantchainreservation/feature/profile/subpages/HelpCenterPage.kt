package com.mh.restaurantchainreservation.feature.profile.subpages

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.AppShortcut
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.EventNote
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.i18n.R as I18nR

@Composable
fun HelpCenterPage(
    onBack: () -> Unit,
    onContactSupport: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    var query by rememberSaveable { mutableStateOf("") }
    var openIndex by rememberSaveable { mutableStateOf(-1) }

    SubpageScaffold(
        title = stringResource(I18nR.string.help_title),
        onBack = onBack,
        modifier = modifier,
    ) {
        SearchBar(value = query, onValueChange = { query = it })

        Spacer(Modifier.height(24.dp))
        Text(stringResource(I18nR.string.help_popular_topics), color = palette.foreground, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        TopicsGrid()

        Spacer(Modifier.height(24.dp))
        Text(stringResource(I18nR.string.help_faqs), color = palette.foreground, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            val faqs = listOf(
                stringResource(I18nR.string.help_faq_1_q) to stringResource(I18nR.string.help_faq_1_a),
                stringResource(I18nR.string.help_faq_2_q) to stringResource(I18nR.string.help_faq_1_a),
                stringResource(I18nR.string.help_faq_3_q) to stringResource(I18nR.string.help_faq_1_a),
                stringResource(I18nR.string.help_faq_4_q) to stringResource(I18nR.string.help_faq_1_a),
            )
            faqs.forEachIndexed { index, (q, a) ->
                FaqRow(
                    question = q,
                    answer = a,
                    expanded = openIndex == index,
                    onToggle = { openIndex = if (openIndex == index) -1 else index },
                )
            }
        }

        Spacer(Modifier.height(28.dp))
        ContactCallout(onClick = onContactSupport)
        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun SearchBar(value: String, onValueChange: (String) -> Unit) {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(14.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(palette.mutedSurface)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(Icons.Outlined.Search, null, tint = palette.mutedForeground, modifier = Modifier.size(18.dp))
        Box(modifier = Modifier.weight(1f)) {
            if (value.isEmpty()) {
                Text(stringResource(I18nR.string.help_search_hint), color = palette.mutedForeground, fontSize = 14.sp)
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                cursorBrush = SolidColor(palette.brand),
                textStyle = LocalTextStyle.current.merge(TextStyle(color = palette.foreground, fontSize = 14.sp)),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun TopicsGrid() {
    val items = listOf(
        Triple(Icons.Outlined.EventNote, stringResource(I18nR.string.help_topic_booking), null),
        Triple(Icons.Outlined.AccountCircle, stringResource(I18nR.string.help_topic_account), null),
        Triple(Icons.Outlined.CreditCard, stringResource(I18nR.string.help_topic_payment), null),
        Triple(Icons.Outlined.Shield, stringResource(I18nR.string.help_topic_security), null),
        Triple(Icons.Outlined.AppShortcut, stringResource(I18nR.string.help_topic_app), null),
    )
    val rows = items.chunked(2)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                row.forEach { (icon, label, _) ->
                    TopicCard(icon = icon, label = label, modifier = Modifier.weight(1f))
                }
                if (row.size == 1) Box(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun TopicCard(icon: ImageVector, label: String, modifier: Modifier = Modifier) {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = modifier
            .clip(shape)
            .border(1.dp, palette.border.copy(alpha = 0.6f), shape)
            .background(palette.cardSurface)
            .clickable { /* topic page hookup later */ }
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier.size(36.dp).clip(CircleShape).background(palette.brand.copy(alpha = 0.10f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = palette.brand, modifier = Modifier.size(18.dp))
        }
        Text(label, color = palette.foreground, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun FaqRow(question: String, answer: String, expanded: Boolean, onToggle: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(14.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.dp, palette.border.copy(alpha = 0.6f), shape)
            .background(palette.cardSurface)
            .clickable(onClick = onToggle)
            .padding(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(question, color = palette.foreground, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            Icon(
                imageVector = if (expanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                contentDescription = null,
                tint = palette.mutedForeground,
                modifier = Modifier.size(18.dp),
            )
        }
        AnimatedVisibility(visible = expanded) {
            Column {
                Spacer(Modifier.height(8.dp))
                Text(answer, color = palette.mutedForeground, fontSize = 13.sp, lineHeight = 18.sp)
            }
        }
    }
}

@Composable
private fun ContactCallout(onClick: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(palette.brand.copy(alpha = 0.08f))
            .clickable(onClick = onClick)
            .padding(16.dp),
    ) {
        Text(
            text = stringResource(I18nR.string.help_contact_cta),
            color = palette.brand,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
