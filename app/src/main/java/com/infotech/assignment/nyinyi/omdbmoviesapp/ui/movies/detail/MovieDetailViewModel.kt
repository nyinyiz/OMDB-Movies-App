package com.infotech.assignment.nyinyi.omdbmoviesapp.ui.movies.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.infotech.assignment.nyinyi.omdbmoviesapp.data.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val selectedItemId = MutableLiveData<String>()

    val movieResponse = liveData(Dispatchers.IO) {
        val result = selectedItemId.value?.let { repository.getMovieDetail(it) }
        emit(result)
    }

    fun selectItem(id: String) {
        selectedItemId.value = id
    }


}