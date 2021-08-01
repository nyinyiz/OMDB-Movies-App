package com.infotech.assignment.nyinyi.omdbmoviesapp.data.local.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.infotech.assignment.nyinyi.omdbmoviesapp.data.local.entity.Movie

@Dao
interface MoviesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMultipleMovies(list: List<Movie>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movie: Movie)

    @Update()
    suspend fun update(movie: Movie)

    @Query("SELECT * FROM movie_table")
    fun getMovies(): PagingSource<Int, Movie>

    @Query("SELECT * FROM movie_table WHERE imdbID = :id")
    fun getMovie(id: String): LiveData<Movie>

    @Query("SELECT * FROM movie_table WHERE imdbID = :id")
    suspend fun getMovieSync(id: String): Movie

    @Query("SELECT * FROM movie_table WHERE querysearch LIKE :search OR title LIKE :search")
    fun getMoviesByQuery(search: String): PagingSource<Int, Movie>

    @Query("DELETE FROM movie_table")
    suspend fun clearMovies()

    @Query("SELECT COUNT(imdbID) from movie_table")
    suspend fun count(): Int
}
