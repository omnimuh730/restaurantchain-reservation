package com.mh.restaurantchainreservation.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mh.restaurantchainreservation.core.designsystem.theme.RestaurantTheme

private val PreviewTabs = listOf(
    BottomNavTab(BottomNavTabId.Discover, "Discover"),
    BottomNavTab(BottomNavTabId.Wishlist, "Wishlist"),
    BottomNavTab(BottomNavTabId.Dining, "Dining"),
    BottomNavTab(BottomNavTabId.Profile, "Profile"),
)

private val PreviewTabsKo = listOf(
    BottomNavTab(BottomNavTabId.Discover, "발견"),
    BottomNavTab(BottomNavTabId.Wishlist, "위시리스트"),
    BottomNavTab(BottomNavTabId.Dining, "다이닝"),
    BottomNavTab(BottomNavTabId.Profile, "프로필"),
)

@Preview(name = "Bottom Nav", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun BottomNavBarLightPreview() {
    RestaurantTheme {
        InteractivePreview(tabs = PreviewTabs)
    }
}

@Preview(name = "Bottom Nav · Korean", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun BottomNavBarKoreanPreview() {
    RestaurantTheme {
        InteractivePreview(tabs = PreviewTabsKo)
    }
}

@Preview(name = "Bottom Nav · Badged Profile", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun BottomNavBarBadgePreview() {
    RestaurantTheme {
        InteractivePreview(
            tabs = PreviewTabs,
            initialActive = BottomNavTabId.Discover,
            badgeCount = 7,
            showDot = false,
        )
    }
}

@Preview(name = "Bottom Nav · Alert Dot", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun BottomNavBarDotPreview() {
    RestaurantTheme {
        InteractivePreview(
            tabs = PreviewTabs,
            initialActive = BottomNavTabId.Wishlist,
            badgeCount = 0,
            showDot = true,
        )
    }
}

@Composable
private fun InteractivePreview(
    tabs: List<BottomNavTab>,
    initialActive: BottomNavTabId = BottomNavTabId.Discover,
    badgeCount: Int = 0,
    showDot: Boolean = false,
) {
    var active by remember { mutableStateOf(initialActive) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Active: ${active.name}",
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
        BottomNavBar(
            tabs = tabs,
            activeId = active,
            onTabSelect = { active = it },
            onQrPay = {},
            qrPayContentDescription = "QR Pay",
            profileBadgeCount = badgeCount,
            showProfileDot = showDot,
        )
    }
}
