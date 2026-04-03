package com.example.cinetrack.screens

import android.widget.ImageView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.cinetrack.model.MovieDetail
import com.example.cinetrack.network.ImageLoader
import com.example.cinetrack.network.WatchListRepo
@Composable
fun WatchlistScreen(navController: NavController) {
    val context = LocalContext.current
    val movies = remember { mutableStateListOf<MovieDetail>() }

    // Reload watchlist every time screen is opened
    LaunchedEffect(Unit) {
        movies.clear()
        movies.addAll(WatchListRepo.getWatchList(context))
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "My Watchlist",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (movies.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Your watchlist is empty.\nSearch for movies to add!",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(movies, key = { it.id }) { movie ->
                    WatchlistItem(
                        movie = movie,
                        onClick = { navController.navigate("detail/${movie.id}") },
                        onWatchedToggle = { watched ->
                            WatchListRepo.setWatched(context, movie.id, watched)
                            val index = movies.indexOf(movie)
                            if (index != -1) {
                                movies[index] = movie.copy(watched = watched)
                            }
                        },
                        onRemove = {
                            WatchListRepo.removeMovie(context, movie.id)
                            movies.remove(movie)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun WatchlistItem(
    movie: MovieDetail,
    onClick: () -> Unit,
    onWatchedToggle: (Boolean) -> Unit,
    onRemove: () -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (movie.watched)
                MaterialTheme.colorScheme.surfaceVariant
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Poster
            AndroidView(
                factory = { ctx -> ImageView(ctx).apply {
                    scaleType = ImageView.ScaleType.CENTER_CROP
                }},
                update = { imageView ->
                    if (movie.posterPath.isNotEmpty()) {
                        ImageLoader.loadImage(context, imageView, movie.posterPath)
                    }
                },
                modifier = Modifier
                    .width(60.dp)
                    .height(90.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Title and rating
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textDecoration = if (movie.watched) TextDecoration.LineThrough
                    else TextDecoration.None
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "⭐ ${"%.1f".format(movie.rating)} / 10",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (movie.watched) "✅ Watched" else "🎬 Not watched yet",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Actions
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Toggle watched
                IconButton(onClick = { onWatchedToggle(!movie.watched) }) {
                    Icon(
                        imageVector = if (movie.watched) Icons.Default.Refresh
                        else Icons.Default.Check,
                        contentDescription = if (movie.watched) "Mark unwatched" else "Mark watched"
                    )
                }
                // Remove
                IconButton(onClick = { onRemove() }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove from watchlist",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
