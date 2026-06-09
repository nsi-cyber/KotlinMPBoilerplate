package com.nsicyber.composeboilerplatform.sharedlogic.feature.home.domain.repository

import com.nsicyber.composeboilerplatform.sharedlogic.feature.home.domain.model.HomeGreeting

/**
 * Domain contract for providing Home feature data.
 */
interface HomeRepository {
    fun greeting(): HomeGreeting
}
