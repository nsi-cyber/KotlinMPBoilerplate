package com.nsicyber.composeboilerplatform.sharedlogic.core.common

/**
 * Supplies platform metadata for user-facing messages and diagnostics.
 */
interface PlatformInfoProvider {
    fun platformName(): String
}

/**
 * Returns the platform-specific implementation for the active target.
 */
expect fun platformInfoProvider(): PlatformInfoProvider
