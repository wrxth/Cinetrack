package com.example.cinetrack.screens

import android.content.Context
import android.content.Intent
import android.widget.ImageView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.cinetrack.model.MovieDetail
import com.example.cinetrack.network.ImageLoader
import com.example.cinetrack.network.MovieRepo
import com.example.cinetrack.network.WatchListRepo

@Composable
fun DetailScreen(movieId: Int, navController: NavController) {
    val context = LocalContext.current
    var movie by remember { mutableStateOf<MovieDetail?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isInWatchlist by remember { mutableStateOf(false) }

    LaunchedEffect(movieId) {
        MovieRepo.fetchMovieDetail(context, movieId) { fetchedMovie ->
            movie = fetchedMovie
            isLoading = false
            isInWatchlist = WatchListRepo.isInWatchList(context,movieId)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        // Back button
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
        }

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            movie?.let { m ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    // Poster
                    AndroidView(
                        factory = { ctx -> ImageView(ctx).apply {
                            scaleType = ImageView.ScaleType.CENTER_CROP
                        }},
                        update = { imageView ->
                            if (m.posterPath.isNotEmpty()) {
                                ImageLoader.loadImage(context, imageView, m.posterPath)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = m.title, style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "⭐ ${"%.1f".format(m.rating)} / 10", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Release date: ${m.releaseDate}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = m.overview, style = MaterialTheme.typography.bodyMedium)

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            movie?.let { m ->
                                if (isInWatchlist) {
                                    WatchListRepo.removeMovie(context, m.id)
                                    isInWatchlist = false
                                } else {
                                    WatchListRepo.addMovie(context, m)
                                    isInWatchlist = true
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isInWatchlist) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = if (isInWatchlist) Icons.Default.Delete else Icons.Default.Add,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isInWatchlist) "Remove from Watchlist" else "Add to Watchlist")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { shareMovie(context, m) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Share this movie")
                    }
                }
            }
        }
    }
}

fun shareMovie(context: Context, movie: MovieDetail) {
    val shareText = "Check out this movie: ${movie.title}\n" +
            "Rating: ${"%.1f".format(movie.rating)}/10\n" +
            "https://www.themoviedb.org/movie/${movie.id}"

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText)
    }

    context.startActivity(Intent.createChooser(intent, "Share via"))
}