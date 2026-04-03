package com.example.cinetrack.network

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.android.volley.toolbox.Volley.newRequestQueue

class VolleySingleton private constructor(context: Context){

    private val requestQueue: RequestQueue = newRequestQueue(context.applicationContext)

    companion object{
        @Volatile
        private var instance: VolleySingleton? = null

        fun getInstance( context: Context): VolleySingleton {
            return instance?: synchronized(this){
                instance ?: VolleySingleton(context).also { instance = it }
            }
        }
    }

    fun <T> addToRequestQueue(request: Request<T>){
        requestQueue.add(request)
    }
}