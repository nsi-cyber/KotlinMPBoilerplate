package com.nsicyber.composeboilerplatform

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform