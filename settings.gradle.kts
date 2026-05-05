pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Restaurantchain Reservation"
include(":app")
include(":core:designsystem")
include(":core:i18n")
include(":core:model")
include(":core:navigation")
include(":feature:auth")
include(":feature:discover")
include(":feature:search")
include(":feature:booking")
include(":feature:dining")
include(":feature:profile")
include(":feature:wishlist")
include(":feature:notifications")
include(":feature:qrpay")
 