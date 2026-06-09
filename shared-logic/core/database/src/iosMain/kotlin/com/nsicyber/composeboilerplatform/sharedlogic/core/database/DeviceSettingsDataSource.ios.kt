package com.nsicyber.composeboilerplatform.sharedlogic.core.database

private class IOSDeviceSettingsDataSource : DeviceSettingsDataSource {
    override fun launchCount(): Int = 1
}

actual fun deviceSettingsDataSource(): DeviceSettingsDataSource = IOSDeviceSettingsDataSource()
