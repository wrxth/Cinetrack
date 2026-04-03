package com.example.cinetrack.screens

import android.content.res.Configuration
import android.widget.ImageView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.cinetrack.model.Movie
import com.example.cinetrack.network.ImageLoader
import com.example.cinetrack.network.MovieRepo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.example.cinetrack.model.MovieDetail
import com.example.cinetrack.network.WatchListRepo

@Composable
fun HomeScreen(navController: NavController) {
    val movies = remember { mutableStateListOf<Movie>() }
    val context = LocalContext.current
    val landscape = isLandscape()

    var currentPage by remember { mutableStateOf(1) }
    var isLoadingMore by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val gridState = rememberLazyGridState()

    LaunchedEffect(Unit) {
        MovieRepo.fetchPopularMovies(context, page = 1) { fetchedMovies ->
            movies.addAll(fetchedMovies)
        }
    }

    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItem = if (landscape) {
                gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            } else {
                listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            }
            val totalItems = if (landscape) {
                gridState.layoutInfo.totalItemsCount
            } else {
                listState.layoutInfo.totalItemsCount
            }
            lastVisibleItem >= totalItems - 5 && totalItems > 0
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && !isLoadingMore) {
            isLoadingMore = true
            val nextPage = currentPage + 1
            MovieRepo.fetchPopularMovies(context, page = nextPage) { fetchedMovies ->
                movies.addAll(fetchedMovies)
                currentPage = nextPage
                isLoadingMore = false
            }
        }
    }

    if (landscape) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            state = gridState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(movies) { movie ->
                MovieItem(
                    movie = movie,
                    onClick = { navController.navigate("detail/${movie.id}") }
                )
            }
            if (isLoadingMore) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }
    } else {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(movies) { movie ->
                MovieItem(
                    movie = movie,
                    onClick = { navController.navigate("detail/${movie.id}") }
                )
            }
            if (isLoadingMore) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }
    }
}
@Composable
fun isLandscape(): Boolean {
    val configuration = LocalConfiguration.current
    return configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
}

@Composable
fun MovieItem(movie: Movie, onClick: () -> Unit, onAddToWatchlist: (() -> Unit)? = null) {
    val context = LocalContext.current
    val landscape = isLandscape()
    var isInWatchlist: Boolean by remember {
        mutableStateOf(WatchListRepo.isInWatchList(context, movie.id))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        if (landscape) {
            Column(modifier = Modifier.padding(8.dp)) {
                AndroidView(
                    factory = { ctx -> ImageView(ctx).apply {
                        scaleType = ImageView.ScaleType.CENTER_CROP
                    }},
                    update = { imageView ->
                        ImageLoader.loadImage(context, imageView, movie.posterPath)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "⭐ ${"%.1f".format(movie.rating)} / 10",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        } else {
            Row(modifier = Modifier.padding(8.dp)) {
                AndroidView(
                    factory = { ctx -> ImageView(ctx).apply {
                        scaleType = ImageView.ScaleType.CENTER_CROP
                    }},
                    update = { imageView ->
                        ImageLoader.loadImage(context, imageView, movie.posterPath)
                    },
                    modifier = Modifier
                        .width(80.dp)
                        .height(120.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterVertically)
                ) {
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "⭐ ${"%.1f".format(movie.rating)} / 10",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = movie.overview,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                    IconButton(
                        onClick = {
                            if (isInWatchlist) {
                                WatchListRepo.removeMovie(context, movie.id)
                                isInWatchlist = false
                            } else {
                                WatchListRepo.addMovie(
                                    context,
                                    MovieDetail(
                                        id = movie.id,
                                        title = movie.title,
                                        overview = movie.overview,
                                        posterPath = movie.posterPath,
                                        rating = movie.rating,
                                        releaseDate = ""
                                    )
                                )
                                isInWatchlist = true
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isInWatchlist) Icons.Default.Favorite
                            else Icons.Default.FavoriteBorder,
                            contentDescription = "Add to watchlist",
                            tint = if (isInWatchlist) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }


            }
        }
    }
}