package com.nsicyber.composeboilerplatform.sharedlogic.core.presentation.model

data class StateModel<T>(
    val loading: Boolean = true,
    val data: T? = null,
) {
    fun isVisible(): Boolean {
        return loading || (!loading && data != null)
    }
}


data class PaginationStateModel<T>(
    val loading: Boolean = false,
    val data: T? = null,
    val currentPage: Int = 0,
    val hasMore: Boolean = true,
) {
    fun isVisible(): Boolean {
        return !loading && data != null
    }
}