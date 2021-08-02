package com.infotech.assignment.nyinyi.omdbmoviesapp.ui.movies.detail

import androidx.lifecycle.*
import com.infotech.assignment.nyinyi.omdbmoviesapp.data.repository.MovieRepository
import com.infotech.assignment.nyinyi.omdbmoviesapp.models.MovieDetailResponse
import com.infotech.assignment.nyinyi.omdbmoviesapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val selectedItemId = MutableLiveData<String>()
    private val detailData = MutableLiveData<Resource<MovieDetailResponse>>()

    val data: LiveData<Resource<MovieDetailResponse>>
        get() = detailData

    val movieResponse = liveData(Dispatchers.IO) {
        val result = selectedItemId.value?.let { repository.getMovieDetail(it) }
        emit(result)
    }

    private fun getMoviesDetail() = viewModelScope.launch {
        detailData.postValue(Resource.loading(null))
        try {
            selectedItemId.value?.let {
                repository.getMovieDetail(it).let { detailResponse ->
                    if (detailResponse.response) {
                        detailData.postValue(Resource.success(detailResponse))
                    } else {
                        detailData.postValue(Resource.error(detailResponse.error, null))
                    }
                }
            }
        }catch (e : Exception) {
            detailData.postValue(Resource.error(e.localizedMessage, null))
        }

    }

    fun selectItem(id: String) {
        selectedItemId.value = id
        getMoviesDetail()
    }

}