package com.mh.restaurantchainreservation.feature.profile.hub

data class PresetAvatar(val id: String, val src: String, val label: String)

val PresetAvatars: List<PresetAvatar> = listOf(
    PresetAvatar("a1", "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=240&h=240&fit=crop&crop=faces", "Classic"),
    PresetAvatar("a2", "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=240&h=240&fit=crop&crop=faces", "Sunny"),
    PresetAvatar("a3", "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=240&h=240&fit=crop&crop=faces", "Warm"),
    PresetAvatar("a4", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=240&h=240&fit=crop&crop=faces", "Sharp"),
    PresetAvatar("a5", "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=240&h=240&fit=crop&crop=faces", "Soft"),
    PresetAvatar("a6", "https://images.unsplash.com/photo-1531123897727-8f129e1688ce?w=240&h=240&fit=crop&crop=faces", "Bold"),
    PresetAvatar("a7", "https://images.unsplash.com/photo-1502685104226-ee32379fefbe?w=240&h=240&fit=crop&crop=faces", "Calm"),
    PresetAvatar("a8", "https://images.unsplash.com/photo-1463453091185-61582044d556?w=240&h=240&fit=crop&crop=faces", "Bright"),
)
