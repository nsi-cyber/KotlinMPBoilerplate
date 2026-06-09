package com.nsicyber.composeboilerplatform.sharedlogic.core.common

import android.os.Build

private class AndroidPlatformInfoProvider : PlatformInfoProvider {
    override fun platformName(): String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun platformInfoProvider(): PlatformInfoProvider = AndroidPlatformInfoProvider()
