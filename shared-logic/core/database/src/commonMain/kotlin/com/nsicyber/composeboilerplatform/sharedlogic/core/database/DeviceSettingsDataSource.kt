package com.nsicyber.composeboilerplatform.sharedlogic.core.database

/**
 * Minimal persistent settings API used by shared feature data modules.
 */
interface DeviceSettingsDataSource {
    fun launchCount(): Int
}

/**
 * Returns a platform-specific settings data source implementation.
 */
expect fun deviceSettingsDataSource(): DeviceSettingsDataSource
