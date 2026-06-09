package com.nsicyber.composeboilerplatform.sharedlogic.core.database

private class AndroidDeviceSettingsDataSource : DeviceSettingsDataSource {
    override fun launchCount(): Int = 1
}

actual fun deviceSettingsDataSource(): DeviceSettingsDataSource = AndroidDeviceSettingsDataSource()
