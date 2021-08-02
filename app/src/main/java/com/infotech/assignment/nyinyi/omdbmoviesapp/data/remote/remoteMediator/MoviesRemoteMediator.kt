package com.infotech.assignment.nyinyi.omdbmoviesapp.data.remote.remoteMediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.infotech.assignment.nyinyi.omdbmoviesapp.BuildConfig
import com.infotech.assignment.nyinyi.omdbmoviesapp.data.local.db.AppDatabase
import com.infotech.assignment.nyinyi.omdbmoviesapp.data.local.entity.Movie
import com.infotech.assignment.nyinyi.omdbmoviesapp.data.local.entity.RemoteKeys
import com.infotech.assignment.nyinyi.omdbmoviesapp.data.remote.api.MoviesApi
import com.infotech.assignment.nyinyi.omdbmoviesapp.utils.STARTING_PAGE_INDEX
import retrofit2.HttpException
import java.io.IOException

@ExperimentalPagingApi
class MoviesRemoteMediator(
    private val service: MoviesApi,
    private val db: AppDatabase,
    private val search: String,
    private val type: String
) : RemoteMediator<Int, Movie>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Movie>): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: STARTING_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                if (prevKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                if (nextKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }
                nextKey
            }
        }

        try {

            val apiResponse = service.getMovies(BuildConfig.API_KEY, search, type, page)

            val moviesList = apiResponse.search
            val endOfPaginationReached = apiResponse.error.isNullOrEmpty().not()

            db.withTransaction {

                if (loadType == LoadType.REFRESH) {
                    db.remoteKeysDao.clearRemoteKeys()
                    db.moviesDao.clearMovies()
                }

                val prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1

                if (moviesList.isNullOrEmpty()) {
                    MediatorResult.Error(Throwable(apiResponse.error))
                } else {

                    val keys = moviesList?.map {
                        RemoteKeys(
                            it.imdbID,
                            prevKey = prevKey,
                            nextKey = nextKey
                        )
                    }
                    db.remoteKeysDao.insertMultipleKeys(
                        keys
                    )

                    if (apiResponse.error.isNullOrEmpty()) {
                        val itemList = moviesList.map {
                            it.copy(
                                querysearch =
                                search
                            )
                        }
                        db.moviesDao.insertMultipleMovies(itemList)
                    } else {
                        MediatorResult.Error(Throwable(apiResponse.error))
                    }
                }
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Movie>): RemoteKeys? {
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { movie ->
                db.remoteKeysDao.remoteKeysByMovieId(movie.imdbID)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Movie>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { movie ->
                db.remoteKeysDao.remoteKeysByMovieId(movie.imdbID)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, Movie>
    ): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.imdbID?.let { movieId ->
                db.remoteKeysDao.remoteKeysByMovieId(movieId)
            }
        }
    }
}
