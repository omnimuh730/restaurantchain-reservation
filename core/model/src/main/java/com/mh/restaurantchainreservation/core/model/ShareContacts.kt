package com.mh.restaurantchainreservation.core.model

data class ShareContact(
    val id: String,
    val name: String,
    val initials: String,
    val handle: String = "",
)

object ShareContacts {
    val all: List<ShareContact> = listOf(
        ShareContact("f1", "Sarah Kim", "SK", "@sarahkim"),
        ShareContact("f2", "Marcus Johnson", "MJ", "@marcusj"),
        ShareContact("f3", "Emma Chen", "EC", "@emmachen"),
        ShareContact("f4", "David Park", "DP", "@davidpark"),
        ShareContact("f5", "Olivia Tran", "OT", "@oliviat"),
        ShareContact("f6", "Mina Park", "MP", "@minapark"),
        ShareContact("f7", "Noah Williams", "NW", "@noahw"),
        ShareContact("f8", "Ryan O'Brien", "RO", "@ryanob"),
    )

    fun byId(id: String): ShareContact? = all.firstOrNull { it.id == id }
}
