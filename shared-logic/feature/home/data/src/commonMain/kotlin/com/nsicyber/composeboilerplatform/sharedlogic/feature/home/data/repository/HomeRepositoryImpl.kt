package com.nsicyber.composeboilerplatform.sharedlogic.feature.home.data.repository

import com.nsicyber.composeboilerplatform.sharedlogic.core.common.PlatformInfoProvider
import com.nsicyber.composeboilerplatform.sharedlogic.core.common.platformInfoProvider
import com.nsicyber.composeboilerplatform.sharedlogic.core.database.DeviceSettingsDataSource
import com.nsicyber.composeboilerplatform.sharedlogic.core.database.deviceSettingsDataSource
import com.nsicyber.composeboilerplatform.sharedlogic.core.network.NetworkResource
import com.nsicyber.composeboilerplatform.sharedlogic.core.network.NetworkMonitor
import com.nsicyber.composeboilerplatform.sharedlogic.core.network.networkMonitor
import com.nsicyber.composeboilerplatform.sharedlogic.feature.home.data.remote.HomeRemoteDataSource
import com.nsicyber.composeboilerplatform.sharedlogic.feature.home.domain.model.HomeGreeting
import com.nsicyber.composeboilerplatform.sharedlogic.feature.home.domain.repository.HomeRepository

/**
 * Concrete repository that composes core providers for Home feature content.
 */
class HomeRepositoryImpl(
    private val networkMonitor: NetworkMonitor = networkMonitor(),
    private val settingsDataSource: DeviceSettingsDataSource = deviceSettingsDataSource(),
    private val platformInfoProvider: PlatformInfoProvider = platformInfoProvider(),
    private val homeRemoteDataSource: HomeRemoteDataSource = HomeRemoteDataSource()
) : HomeRepository {

    override fun greeting(): HomeGreeting {
        val networkState = if (networkMonitor.isOnline()) "online" else "offline"
        val launchCount = settingsDataSource.launchCount()
        val platformName = platformInfoProvider.platformName()

        return HomeGreeting(
            message = "Hello from $platformName. Launch #$launchCount, network: $networkState."
        )
    }

    /**
     * Optional preview call to demonstrate RemoteDataSource integration.
     */
    suspend fun remoteGreetingPreview(): NetworkResource<HomeGreeting> {
        return when (val remote = homeRemoteDataSource.fetchGreetingPrefix(enableNetworkCall = false)) {
            is NetworkResource.Success -> {
                NetworkResource.Success(
                    HomeGreeting("${remote.value} from remote datasource preview.")
                )
            }

            is NetworkResource.Error -> {
                NetworkResource.Error(
                    code = remote.code,
                    message = remote.message,
                    throwable = remote.throwable,
                    internalError = remote.internalError
                )
            }
        }
    }
}
