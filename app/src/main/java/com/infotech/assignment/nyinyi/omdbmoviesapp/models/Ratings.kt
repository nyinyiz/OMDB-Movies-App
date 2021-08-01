package com.infotech.assignment.nyinyi.omdbmoviesapp.models

import com.google.gson.annotations.SerializedName

data class Ratings (

	@SerializedName("Source") val source : String,
	@SerializedName("Value") val value : String
)