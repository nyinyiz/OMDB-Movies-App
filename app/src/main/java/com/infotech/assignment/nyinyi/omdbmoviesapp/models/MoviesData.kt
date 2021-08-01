package com.infotech.assignment.nyinyi.omdbmoviesapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.infotech.assignment.nyinyi.omdbmoviesapp.data.local.entity.Movie
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MoviesData(

    @SerializedName("Search") val search: List<Movie>,
    @SerializedName("totalResults") val totalResults: Int,
    @SerializedName("Response") val response: Boolean,
    @SerializedName("Error") val error: String
) : Parcelable
