package com.example.cinetrack.network

import android.content.Context
import android.content.SharedPreferences
import com.example.cinetrack.model.Movie
import com.example.cinetrack.model.MovieDetail
import org.json.JSONArray
import org.json.JSONObject

object WatchListRepo {
    private const val PREFS_NAME = "watchlist_prefs"
    private const val KEY_WATCHLIST = "watchlist"

    fun addMovie(context: Context, movie: MovieDetail){
        val prefs = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE)
        val current = getWatchlistJson(prefs)

        for (i in 0 until current.length()){
            if(current.getJSONObject(i).getInt("id") == movie.id) {
                return
            }
        }

        val movieJson = JSONObject().apply {
            put("id", movie.id)
            put("title", movie.title)
            put("overview", movie.overview)
            put("posterPath", movie.posterPath)
            put("rating", movie.rating)
            put("releaseDate", movie.releaseDate)
            put("watched", false)
        }

        current.put(movieJson)
        prefs.edit().putString(KEY_WATCHLIST, current.toString()).apply()
    }

    fun removeMovie(context: Context, movieId: Int){
        val prefs = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE)
        val current = getWatchlistJson(prefs)
        val updated = JSONArray()

        for (i in 0 until current.length()){
            val obj = current.getJSONObject(i)
            if (obj.getInt("id") != movieId){
                updated.put(obj)
            }
        }

        prefs.edit().putString(KEY_WATCHLIST, updated.toString()).apply()
    }

    fun isInWatchList(context: Context, movieId: Int): Boolean{
        val prefs = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE)
        val current = getWatchlistJson(prefs)

        for (i in 0 until current.length()){
            val obj = current.getJSONObject(i)
            if (obj.getInt("id") == movieId){
                return true
            }
        }

        return false
    }

    fun getWatchList(context: Context): List<MovieDetail>{
        val prefs = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE)
        val current = getWatchlistJson(prefs)
        val movies = mutableListOf<MovieDetail>()

        for (i in 0 until current.length()){
            val obj = current.getJSONObject(i)
            movies.add(
                MovieDetail(
                    id = obj.getInt("id"),
                    title = obj.getString("title"),
                    overview = obj.getString("overview"),
                    posterPath = obj.getString("posterPath"),
                    rating = obj.getDouble("rating"),
                    releaseDate = obj.getString("releaseDate"),
                    watched = obj.getBoolean("watched")
                )

            )
        }

        return movies
    }

    fun setWatched(context: Context, movieId: Int, watched: Boolean){
        val prefs = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE)
        val current = getWatchlistJson(prefs)

        for (i in 0 until current.length()){
            val obj = current.getJSONObject(i)
            if (obj.getInt("id") == movieId){
                obj.put("watched", watched)
                break
            }
        }

        prefs.edit().putString(KEY_WATCHLIST, current.toString()).apply()
    }

    private fun getWatchlistJson(prefs: SharedPreferences): JSONArray{
        val json = prefs.getString(KEY_WATCHLIST,"[]") ?:"[]"
        return JSONArray(json)
    }

}