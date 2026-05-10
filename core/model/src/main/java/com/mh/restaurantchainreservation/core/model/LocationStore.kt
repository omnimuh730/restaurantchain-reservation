package com.mh.restaurantchainreservation.core.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserLocation(
    val name: String,
    val address: String,
    val lat: Double,
    val lng: Double,
)

object LocationStore {
    private val DefaultLocation = UserLocation(
        name = "Gangnam Station",
        address = "Gangnam-gu, Seoul",
        lat = 37.4980,
        lng = 127.0276,
    )

    private val _current = MutableStateFlow(DefaultLocation)
    val current: StateFlow<UserLocation> = _current.asStateFlow()

    fun select(location: UserLocation) {
        _current.value = location
    }
}
