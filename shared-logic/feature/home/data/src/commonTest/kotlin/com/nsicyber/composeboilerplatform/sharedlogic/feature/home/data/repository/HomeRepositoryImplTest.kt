package com.nsicyber.composeboilerplatform.sharedlogic.feature.home.data.repository

import com.nsicyber.composeboilerplatform.sharedlogic.core.common.PlatformInfoProvider
import com.nsicyber.composeboilerplatform.sharedlogic.core.database.DeviceSettingsDataSource
import com.nsicyber.composeboilerplatform.sharedlogic.core.network.NetworkResource
import com.nsicyber.composeboilerplatform.sharedlogic.core.network.NetworkMonitor
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HomeRepositoryImplTest {

    @Test
    fun greeting_buildsExpectedMessage_whenOnline() {
        val repository = HomeRepositoryImpl(
            networkMonitor = FakeNetworkMonitor(isOnline = true),
            settingsDataSource = FakeSettingsDataSource(launchCount = 7),
            platformInfoProvider = FakePlatformInfoProvider(platformName = "TestOS 1.0")
        )

        val greeting = repository.greeting()

        assertEquals(
            "Hello from TestOS 1.0. Launch #7, network: online.",
            greeting.message
        )
    }

    @Test
    fun greeting_buildsExpectedMessage_whenOffline() {
        val repository = HomeRepositoryImpl(
            networkMonitor = FakeNetworkMonitor(isOnline = false),
            settingsDataSource = FakeSettingsDataSource(launchCount = 2),
            platformInfoProvider = FakePlatformInfoProvider(platformName = "OfflineOS")
        )

        val greeting = repository.greeting()

        assertEquals(
            "Hello from OfflineOS. Launch #2, network: offline.",
            greeting.message
        )
    }

    @Test
    fun remoteGreetingPreview_returnsSuccessPlaceholder() = runBlocking {
        val repository = HomeRepositoryImpl(
            networkMonitor = FakeNetworkMonitor(isOnline = true),
            settingsDataSource = FakeSettingsDataSource(launchCount = 1),
            platformInfoProvider = FakePlatformInfoProvider(platformName = "AnyOS")
        )

        val result = repository.remoteGreetingPreview()

        assertTrue(result is NetworkResource.Success)
        assertEquals("Hello from remote datasource preview.", result.value.message)
    }
}

private class FakeNetworkMonitor(
    private val isOnline: Boolean
) : NetworkMonitor {
    override fun isOnline(): Boolean = isOnline
}

private class FakeSettingsDataSource(
    private val launchCount: Int
) : DeviceSettingsDataSource {
    override fun launchCount(): Int = launchCount
}

private class FakePlatformInfoProvider(
    private val platformName: String
) : PlatformInfoProvider {
    override fun platformName(): String = platformName
}
