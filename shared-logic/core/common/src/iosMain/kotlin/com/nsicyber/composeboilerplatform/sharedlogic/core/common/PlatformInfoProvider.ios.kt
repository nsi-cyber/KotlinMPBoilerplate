package com.nsicyber.composeboilerplatform.sharedlogic.core.common

import platform.UIKit.UIDevice

private class IOSPlatformInfoProvider : PlatformInfoProvider {
    override fun platformName(): String =
        "${UIDevice.currentDevice.systemName()} ${UIDevice.currentDevice.systemVersion}"
}

actual fun platformInfoProvider(): PlatformInfoProvider = IOSPlatformInfoProvider()
