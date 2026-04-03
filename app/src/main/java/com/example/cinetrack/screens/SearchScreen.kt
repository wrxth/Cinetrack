package com.example.cinetrack.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cinetrack.model.Movie
import com.example.cinetrack.network.MovieRepo

@Composable
fun SearchScreen(navController: NavController){
    val context = LocalContext.current
    val movies = remember { mutableStateListOf<Movie>() }
    var query by remember { mutableStateOf("") }
    var hasSearched by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)){

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically){
            TextField(
                value = query,
                onValueChange = {query = it},
                placeholder = {Text("Search movie")},
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (query.isNotBlank()){
                            hasSearched = true
                            MovieRepo.SearchMovies(context, query){ results ->
                                movies.clear()
                                movies.addAll(results)
                            }
                        }
                    }
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = {
                if (query.isNotBlank()){
                    hasSearched = true
                    MovieRepo.SearchMovies(context, query){ results ->
                        movies.clear()
                        movies.addAll(results)
                    }
                }
            }) {
                Text("Search")
            }
        }
        Spacer(modifier = Modifier.width(16.dp))

        when{
            !hasSearched -> {
                Text(
                    text = "search for a movie",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            movies.isEmpty() -> {
                Text(
                    text = "no results",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            else -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(movies) {movie ->
                        MovieItem(movie = movie, onClick = { navController.navigate("detail/${movie.id}")})


                    }
                }
            }
        }


    }

}