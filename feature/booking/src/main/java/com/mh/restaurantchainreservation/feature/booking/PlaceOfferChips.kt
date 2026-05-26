package com.mh.restaurantchainreservation.feature.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mh.restaurantchainreservation.core.designsystem.tokens.LocalRestaurantPalette

data class PlaceOfferChip(
    val label: String,
    val icon: AmenityIconType? = null,
)

data class PlaceOfferChipCategory(
    val title: String,
    val chips: List<PlaceOfferChip>,
)

private val PlaceOfferChipShape = RoundedCornerShape(percent = 50)
private val PlaceOfferChipHorizontalPadding = 16.dp
private val PlaceOfferChipVerticalPadding = 10.dp
private val PlaceOfferChipIconSize = 18.dp

@Composable
fun PlaceOfferChip(
    chip: PlaceOfferChip,
    modifier: Modifier = Modifier,
) {
    val palette = LocalRestaurantPalette.current
    Row(
        modifier = modifier
            .clip(PlaceOfferChipShape)
            .background(palette.cardSurface)
            .border(width = 1.dp, color = palette.border, shape = PlaceOfferChipShape)
            .padding(
                horizontal = PlaceOfferChipHorizontalPadding,
                vertical = PlaceOfferChipVerticalPadding,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        chip.icon?.let { iconType ->
            Icon(
                imageVector = iconType.toImageVector(),
                contentDescription = null,
                tint = palette.foreground,
                modifier = Modifier.size(PlaceOfferChipIconSize),
            )
        }
        Text(
            text = chip.label,
            color = palette.foreground,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PlaceOfferChipCategorySection(
    category: PlaceOfferChipCategory,
    modifier: Modifier = Modifier,
) {
    if (category.chips.isEmpty()) return
    val palette = LocalRestaurantPalette.current
    Column(modifier = modifier.padding(bottom = 20.dp)) {
        Text(
            text = category.title,
            color = palette.foreground,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp),
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            category.chips.forEach { chip ->
                PlaceOfferChip(chip = chip)
            }
        }
    }
}

@Composable
fun PlaceOfferChipsContent(
    categories: List<PlaceOfferChipCategory>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        categories.forEach { category ->
            PlaceOfferChipCategorySection(category = category)
        }
    }
}
