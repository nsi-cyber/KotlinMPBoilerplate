package com.nsicyber.composeboilerplatform.sharedlogic.core.network

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.android.Android

actual fun platformHttpClientEngine(): HttpClientEngine = Android.create()
