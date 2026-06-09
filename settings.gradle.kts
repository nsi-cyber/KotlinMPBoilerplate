rootProject.name = "ComposeBoilerPlatform"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

include(":androidApp")
include(":shared-ui")
include(":shared-logic:core:common")
include(":shared-logic:core:presentation")
include(":shared-logic:core:network")
include(":shared-logic:core:database")
include(":shared-logic:feature:home:domain")
include(":shared-logic:feature:home:data")
include(":shared-logic:feature:home:presentation")