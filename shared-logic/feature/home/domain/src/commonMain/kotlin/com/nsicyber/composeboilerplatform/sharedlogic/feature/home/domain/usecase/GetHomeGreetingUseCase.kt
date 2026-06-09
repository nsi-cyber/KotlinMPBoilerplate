package com.nsicyber.composeboilerplatform.sharedlogic.feature.home.domain.usecase

import com.nsicyber.composeboilerplatform.sharedlogic.feature.home.domain.model.HomeGreeting
import com.nsicyber.composeboilerplatform.sharedlogic.feature.home.domain.repository.HomeRepository

/**
 * Use case entry point for retrieving user-facing Home greeting content.
 */
class GetHomeGreetingUseCase(
    private val homeRepository: HomeRepository
) {
    operator fun invoke(): HomeGreeting = homeRepository.greeting()
}
