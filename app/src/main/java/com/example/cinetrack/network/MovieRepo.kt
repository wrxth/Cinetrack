package com.example.cinetrack.network

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.example.cinetrack.BuildConfig
import com.example.cinetrack.model.Movie
import com.example.cinetrack.model.MovieDetail

object MovieRepo {
    fun fetchPopularMovies(context: Context, page: Int = 1,onResult: (List<Movie>) -> Unit) {
        val url = "https://api.themoviedb.org/3/movie/popular?api_key=${BuildConfig.TMDB_API_KEY}&language=en-US&page=$page"

        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                val results = response.getJSONArray("results")
                val movies = mutableListOf<Movie>()
                for (i in 0 until results.length()) {
                    val obj = results.getJSONObject(i)
                    movies.add(
                        Movie(
                            id = obj.getInt("id"),
                            title = obj.getString("title"),
                            overview = obj.getString("overview"),
                            posterPath = obj.getString("poster_path"),
                            rating = obj.getDouble("vote_average")
                        )
                    )
                }
                onResult(movies)
            },
            { error ->
                Log.e("TMDB", "Error: ${error.message}")
            }
        )

        VolleySingleton.getInstance(context).addToRequestQueue(request)
    }


    fun SearchMovies(context: Context, query: String, onResult: (List<Movie>) -> Unit) {
        val url = "https://api.themoviedb.org/3/search/movie?api_key=${BuildConfig.TMDB_API_KEY}&language=en-US&query=${query}&page=1"

        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                val results = response.getJSONArray("results")
                val movies = mutableListOf<Movie>()
                for (i in 0 until results.length()) {
                    val obj = results.getJSONObject(i)
                    movies.add(
                        Movie(
                            id = obj.getInt("id"),
                            title = obj.getString("title"),
                            overview = obj.getString("overview"),
                            posterPath = obj.optString("poster_path"),
                            rating = obj.getDouble("vote_average")
                        )
                    )
                }
                onResult(movies)
            },
            { error ->
                Log.e("TMDB", "Error: ${error.message}")
            }
        )

        VolleySingleton.getInstance(context).addToRequestQueue(request)
    }

    fun fetchMovieDetail(context: Context, movieId: Int, onResult: (MovieDetail) -> Unit) {
        val url = "https://api.themoviedb.org/3/movie/${movieId}?api_key=${BuildConfig.TMDB_API_KEY}&language=en-US"

        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                val movie = MovieDetail(
                    id = response.getInt("id"),
                    title = response.getString("title"),
                    overview = response.getString("overview"),
                    posterPath = response.optString("poster_path", ""),
                    rating = response.getDouble("vote_average"),
                    releaseDate = response.getString("release_date")
                )
                onResult(movie)
            },
            { error ->
                Log.e("TMDB", "Error: ${error.message}")
            }
        )

        VolleySingleton.getInstance(context).addToRequestQueue(request)
    }
}