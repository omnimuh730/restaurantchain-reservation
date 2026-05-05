package com.mh.restaurantchainreservation.feature.profile.subpages

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
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material.icons.outlined.Search
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette
import com.mh.restaurantchainreservation.core.i18n.R as I18nR

private data class SavedPlace(val name: String, val address: String)

private val SavedPlacesData = listOf(
    SavedPlace("Home", "Yongsan-gu, Seoul"),
    SavedPlace("Work", "Gangnam Finance Center"),
    SavedPlace("Hannam Studio", "Itaewon-ro 27"),
)
private val RecentPlacesData = listOf(
    SavedPlace("Hongdae Plaza", "Mapo-gu, Seoul"),
    SavedPlace("Itaewon Galleria", "Yongsan-gu, Seoul"),
)

@Composable
fun LocationPage(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val palette = LocalRestaurantPalette.current
    var query by rememberSaveable { mutableStateOf("") }

    SubpageScaffold(
        title = stringResource(I18nR.string.location_title),
        onBack = onBack,
        modifier = modifier,
    ) {
        SearchInput(value = query, onValueChange = { query = it })
        Spacer(Modifier.height(12.dp))
        UseCurrentRow(onClick = onBack)

        Spacer(Modifier.height(20.dp))
        SectionHeader(stringResource(I18nR.string.location_saved_places))
        Spacer(Modifier.height(8.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SavedPlacesData.forEach { PlaceRow(it, onClick = onBack) }
        }

        Spacer(Modifier.height(20.dp))
        SectionHeader(stringResource(I18nR.string.location_recent))
        Spacer(Modifier.height(8.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (RecentPlacesData.isEmpty()) {
                Text(
                    text = stringResource(I18nR.string.location_no_recent),
                    color = palette.mutedForeground,
                    fontSize = 13.sp,
                )
            } else {
                RecentPlacesData.forEach { PlaceRow(it, onClick = onBack) }
            }
        }
        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun SearchInput(value: String, onValueChange: (String) -> Unit) {
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
        Icon(
            imageVector = Icons.Outlined.Search,
            contentDescription = null,
            tint = palette.mutedForeground,
            modifier = Modifier.size(18.dp),
        )
        Box(modifier = Modifier.weight(1f)) {
            if (value.isEmpty()) {
                Text(
                    text = stringResource(I18nR.string.location_search_hint),
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
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun UseCurrentRow(onClick: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(14.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.dp, palette.brand.copy(alpha = 0.4f), shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(palette.brand.copy(alpha = 0.10f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.MyLocation,
                contentDescription = null,
                tint = palette.brand,
                modifier = Modifier.size(18.dp),
            )
        }
        Text(
            text = stringResource(I18nR.string.location_use_current),
            color = palette.brand,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun SectionHeader(text: String) {
    val palette = LocalRestaurantPalette.current
    Text(
        text = text,
        color = palette.foreground,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
private fun PlaceRow(place: SavedPlace, onClick: () -> Unit) {
    val palette = LocalRestaurantPalette.current
    val shape = RoundedCornerShape(16.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.dp, palette.border.copy(alpha = 0.6f), shape)
            .background(palette.cardSurface)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(palette.mutedSurface),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.LocationOn,
                contentDescription = null,
                tint = palette.foreground,
                modifier = Modifier.size(18.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = place.name,
                color = palette.foreground,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = place.address,
                color = palette.mutedForeground,
                fontSize = 12.sp,
            )
        }
    }
}
