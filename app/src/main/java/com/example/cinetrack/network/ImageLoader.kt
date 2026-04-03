package com.example.cinetrack.network

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.ImageView
import com.android.volley.toolbox.ImageRequest

object ImageLoader {

    fun loadImage(context: Context, imageView: ImageView, posterPath: String){
        val url = "https://image.tmdb.org/t/p/w500${posterPath}"

        val request = object : ImageRequest(
            url,
            {bitmap -> imageView.setImageBitmap(bitmap)},
        0,0,
            ImageView.ScaleType.CENTER_CROP,
            Bitmap.Config.ARGB_8888,
            {error ->
                Log.e("ImageLoader","Error loading image: ${error.message}")
            })
        {}

        VolleySingleton.getInstance(context).addToRequestQueue(request)
    }
}