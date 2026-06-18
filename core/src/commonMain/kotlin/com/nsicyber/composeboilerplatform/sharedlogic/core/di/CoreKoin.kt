package com.nsicyber.composeboilerplatform.sharedlogic.core.di

import com.nsicyber.composeboilerplatform.sharedlogic.core.audioplayer.AudioRepository
import com.nsicyber.composeboilerplatform.sharedlogic.core.common.PlatformInfoProvider
import com.nsicyber.composeboilerplatform.sharedlogic.core.common.platformInfoProvider
import com.nsicyber.composeboilerplatform.sharedlogic.core.database.AppLaunchInfoDao
import com.nsicyber.composeboilerplatform.sharedlogic.core.database.CoreDatabase
import com.nsicyber.composeboilerplatform.sharedlogic.core.database.CoreDatabaseFactory
import com.nsicyber.composeboilerplatform.sharedlogic.core.database.coreDatabase
import com.nsicyber.composeboilerplatform.sharedlogic.core.network.ConnectivityManager
import com.nsicyber.composeboilerplatform.sharedlogic.core.network.HttpClientProvider
import com.nsicyber.composeboilerplatform.sharedlogic.core.network.NetworkMonitor
import com.nsicyber.composeboilerplatform.sharedlogic.core.network.RemoteDataSource
import com.nsicyber.composeboilerplatform.sharedlogic.core.network.connectivityManager
import com.nsicyber.composeboilerplatform.sharedlogic.core.network.networkMonitor
import io.ktor.client.HttpClient
import kotlinx.coroutines.runBlocking
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

/**
 * Base Koin module exposed by core package.
 *
 * Pass Android `applicationContext` when building on Android.
 * iOS callers can pass null.
 */
fun coreModule(
    appContext: Any? = null
): Module = module {
    // Core platform services
    single<PlatformInfoProvider> { platformInfoProvider() }
    single<NetworkMonitor> { networkMonitor() }
    single<ConnectivityManager> { connectivityManager() }

    // Core network stack
    single<HttpClient> { HttpClientProvider.client }
    single { RemoteDataSource(get()) }

    // Core audio stack
    single { AudioRepository() }

    // Core persistence stack
    single { CoreDatabaseFactory(appContext) }
    single<CoreDatabase> { runBlocking { coreDatabase(get()) } }
    single<AppLaunchInfoDao> { get<CoreDatabase>().appLaunchInfoDao() }
}

/**
 * Starts Koin with core module and optional extra modules.
 */
fun initCoreKoin(
    appContext: Any? = null,
    extraModules: List<Module> = emptyList(),
    appDeclaration: KoinAppDeclaration = {}
): KoinApplication {
    return startKoin {
        appDeclaration()
        modules(listOf(coreModule(appContext)) + extraModules)
    }
}
