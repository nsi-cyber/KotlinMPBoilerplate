package com.nsicyber.composeboilerplatform.sharedlogic.feature.home.data.remote

import com.nsicyber.composeboilerplatform.sharedlogic.core.network.NetworkResource
import com.nsicyber.composeboilerplatform.sharedlogic.core.network.RemoteDataSource

/**
 * Home-specific remote datasource boilerplate.
 *
 * The endpoint call is intentionally optional for now and can be enabled when
 * project-level base URL and backend contracts are finalized.
 */
class HomeRemoteDataSource(
    private val remoteDataSource: RemoteDataSource = RemoteDataSource()
) {

    /**
     * Returns a greeting prefix from remote source or a local fallback placeholder.
     */
    suspend fun fetchGreetingPrefix(enableNetworkCall: Boolean = false): NetworkResource<String> {
        if (!enableNetworkCall) {
            return NetworkResource.Success("Hello")
        }

        return remoteDataSource.getAsResource<String, String>(
            url = "https://example.com"
        ) { _ ->
            "Hello"
        }
    }
}
