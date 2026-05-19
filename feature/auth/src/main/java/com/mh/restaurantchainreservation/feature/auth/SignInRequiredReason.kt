package com.mh.restaurantchainreservation.feature.auth

import androidx.annotation.StringRes
import com.mh.restaurantchainreservation.core.i18n.R as I18nR

enum class SignInRequiredReason(@StringRes val messageRes: Int) {
    Generic(I18nR.string.sign_in_required_message_generic),
    Profile(I18nR.string.sign_in_required_message_profile),
    Wishlist(I18nR.string.sign_in_required_message_wishlist),
    Dining(I18nR.string.sign_in_required_message_dining),
    QrPay(I18nR.string.sign_in_required_message_qr_pay),
    Booking(I18nR.string.sign_in_required_message_booking),
}
