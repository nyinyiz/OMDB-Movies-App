package com.infotech.assignment.nyinyi.omdbmoviesapp.data.remote.api

import androidx.lifecycle.LiveData
import com.infotech.assignment.nyinyi.omdbmoviesapp.models.MovieDetailResponse
import com.infotech.assignment.nyinyi.omdbmoviesapp.models.MoviesData
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query

interface MoviesApi {

    @GET("/")
    suspend fun getMovies(
        @Query("apikey") apiKey: String,
        @Query("s") search: String,
        @Query("type") type: String,
        @Query("page") page: Int,
    ): MoviesData

    @GET("/")
    suspend fun getMovieDetail(
        @Query("apikey") apiKey: String,
        @Query("i") imdbId: String
    ): MovieDetailResponse
}
