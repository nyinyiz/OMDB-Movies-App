package com.infotech.assignment.nyinyi.omdbmoviesapp.utils

import androidx.paging.PagingData
import com.infotech.assignment.nyinyi.omdbmoviesapp.data.local.entity.Movie

sealed class UiAction {
    data class Search(val query: String) : UiAction()
    data class Scroll(val currentQuery: String) : UiAction()
}

data class UiState(
    val query: String = DEFAULT_QUERY,
    val lastQueryScrolled: String = DEFAULT_QUERY,
    val hasNotScrolledForCurrentSearch: Boolean = false,
    val pagingData: PagingData<Movie> = PagingData.empty()
)

public const val LAST_QUERY_SCROLLED: String = "last_query_scrolled"
public const val LAST_SEARCH_QUERY: String = "last_search_query"
public const val DEFAULT_QUERY = "Marvel"
public const val DEFAULT_TYPE = "movie"