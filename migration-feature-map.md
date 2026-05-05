# React Demo to Android Feature Mapping

This document maps the React demo implementation in `restaurantchain-reservation-ui-demo` to the Android Kotlin target modules in `restaurantchain-reservation`.

## Route and Feature Mapping

| React Route / Flow | Source Files | Android Module | Android Route ID | Notes |
|---|---|---|---|---|
| App bootstrap and shell | `src/main.tsx`, `src/app/App.tsx`, `src/app/AppLayout.tsx` | `:app`, `:core:navigation`, `:core:designsystem` | `app_shell` | Single-activity Compose host, adaptive chrome |
| Splash gate | `src/app/SplashGate.tsx` | `:feature:discover` | `splash_gate` | Startup handoff to default destination |
| Auth layout | `src/app/AuthLayout.tsx` | `:feature:auth` | `auth_root` | Full-screen flow without app tab chrome |
| Login/Register/Forgot | `src/app/pages/auth/*` | `:feature:auth` | `auth_login`, `auth_register`, `auth_forgot` | Form validations and localized copy |
| Discover root and subviews | `src/app/pages/discover/DiscoverPage.tsx`, `src/app/pages/discover/discover-page/*` | `:feature:discover` | `discover_home` | Main entry tab |
| Discover search modal | `src/app/pages/discover/DiscoverSearchModal.tsx` | `:feature:search` | `search_modal` | Where/When/Who flow |
| Discover search results + map/list sheet | `src/app/pages/discover/SearchResultsView.tsx`, `src/app/pages/discover/search-results/*` | `:feature:search` | `search_results` | Highest interaction complexity |
| Restaurant detail | `src/app/pages/detail/RestaurantDetailView.tsx` | `:feature:booking` | `restaurant_detail` | Contains booking entry points |
| Book table flow | `src/app/pages/booking/BookTableFlow.tsx` | `:feature:booking` | `booking_flow` | Multi-step reservation process |
| Saved/wishlist | `src/app/pages/discover/SavedListView.tsx`, `src/app/pages/discover/Wishlist*` | `:feature:wishlist` | `wishlist` | Includes quick-save behavior and sheets |
| Dining list/detail/enjoy | `src/app/pages/dining/DiningPage.tsx`, `src/app/pages/dining/dining-page/*` | `:feature:dining` | `dining_home`, `dining_detail`, `dining_enjoy` | Tabbed lists and booking lifecycle modals |
| Profile root and subpages | `src/app/pages/profile/ProfilePage.tsx`, `src/app/pages/profile/profile-page/profileNavigation.ts` | `:feature:profile` | `profile_root` | URL-segment mapped subpages |
| Notifications | `src/app/pages/profile/notification/*` | `:feature:notifications` | `notifications` | Inbox and deep-link handling |
| QR Pay | `src/app/pages/qrpay/QRPayPage.tsx`, `src/app/routeWrappers.tsx` | `:feature:qrpay` | `qrpay` | Full-screen app-level flow |

## Shared Design System Mapping

| React DS Area | Source Files | Android Target |
|---|---|---|
| Theme tokens and globals | `src/styles/theme.css`, `src/theme/appTheme.ts` | `:core:designsystem` token + theme packages |
| DS component exports | `src/app/components/ds/index.tsx` | `:core:designsystem.components` |
| Primitive overlays | `src/app/components/ds/Modal.tsx`, `BottomSheet.tsx`, `Drawer.tsx`, `Toast.tsx` | Compose wrappers using Material3 and custom primitives |
| Navigation chrome components | `src/app/app-layout/navigation.tsx`, `BottomNav.tsx` | Adaptive shell primitives in `:core:designsystem.navigation` |

## i18n Mapping

| React i18n Namespace | Android Resource Group |
|---|---|
| `common` | `core_i18n_common.xml` |
| `app` | `core_i18n_app.xml` |
| `auth` | `feature_auth_strings.xml` |
| `discover`, `discoverData` | `feature_discover_strings.xml` |
| `dining`, `diningData` | `feature_dining_strings.xml` |
| `booking` | `feature_booking_strings.xml` |
| `detail`, `detailData` | `feature_booking_detail_strings.xml` |
| `profile`, `profileData` | `feature_profile_strings.xml` |
| `shared`, `components`, `sections`, `news` | `core_i18n_shared.xml` and feature-specific files |

## Pixel-Parity Critical Components

1. Adaptive app chrome (bottom nav + floating center action on phone, sidebar on tablet).
2. Search map/list sheet interactions with smooth drag states.
3. Booking multi-step screen with bottom-sheet payment transition.
4. Wishlist save/quick-save confirmation and overlays.
5. Discover hero/search layouts and compact mode transitions.
6. Typography, spacing, radius, and elevation token parity.
