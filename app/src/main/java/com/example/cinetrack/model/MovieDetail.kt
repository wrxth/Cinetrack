package com.example.cinetrack.model

data class MovieDetail(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String,
    val rating: Double,
    val releaseDate: String,
    val watched: Boolean = false
)
