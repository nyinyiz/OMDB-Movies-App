package com.infotech.assignment.nyinyi.omdbmoviesapp.data.repository

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.infotech.assignment.nyinyi.omdbmoviesapp.data.local.db.AppDatabase
import com.infotech.assignment.nyinyi.omdbmoviesapp.data.local.entity.Movie
import com.infotech.assignment.nyinyi.omdbmoviesapp.data.remote.api.MoviesApi
import com.infotech.assignment.nyinyi.omdbmoviesapp.data.remote.remoteMediator.MoviesRemoteMediator
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MovieRepository @Inject constructor(
    private val moviesApi: MoviesApi,
    private val db: AppDatabase
) {
    private val moviesDao = db.moviesDao

    @ExperimentalPagingApi
    fun getMovies(search: String, type: String): Flow<PagingData<Movie>> {
        Log.d("MovieRepository", "New query: $search")

        val pagingSourceFactory = { moviesDao.getMoviesByQuery(search) }

        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            remoteMediator = MoviesRemoteMediator(
                moviesApi,
                db,
                search,
                type
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow

    }

    companion object {
        private const val NETWORK_PAGE_SIZE = 10
    }
}
