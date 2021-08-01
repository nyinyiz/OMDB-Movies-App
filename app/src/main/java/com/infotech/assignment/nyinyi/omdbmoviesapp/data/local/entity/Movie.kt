package com.infotech.assignment.nyinyi.omdbmoviesapp.data.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "movie_table")
data class Movie(

    @PrimaryKey(autoGenerate = false)
    @SerializedName("imdbID") val imdbID: String,
    @SerializedName("querysearch") // technically mutable but fine for a demo
    @ColumnInfo(collate = ColumnInfo.NOCASE)
    val querysearch: String,
    @SerializedName("Title") val title: String,
    @SerializedName("Year") val year: Int,
    @SerializedName("Type") val type: String,
    @SerializedName("Poster") val poster: String
) : Parcelable
