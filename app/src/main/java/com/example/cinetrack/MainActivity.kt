package com.example.cinetrack

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cinetrack.BuildConfig
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.example.cinetrack.network.SettingsRepo
import com.example.cinetrack.network.VolleySingleton
import com.example.cinetrack.screens.DetailScreen
import com.example.cinetrack.screens.HomeScreen
import com.example.cinetrack.screens.SearchScreen
import com.example.cinetrack.screens.SettingsScreen
import com.example.cinetrack.screens.WatchlistScreen
import com.example.cinetrack.ui.theme.CinetrackTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        Log.d("TEST", "onCreate reached")
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val context = LocalContext.current
            var darkMode by remember {
                mutableStateOf(SettingsRepo.getDarkMode(context))
            }
            CinetrackTheme(darkTheme = darkMode) {
                Surface(modifier = Modifier.fillMaxSize()){
                    AppNavigation(
                        darkMode = darkMode,
                        onDarkModeToggle = { enabled ->
                            darkMode = enabled
                            SettingsRepo.saveDarkMode(context, enabled)
                        }
                    )
                }
            }
        }


    }
}

@Composable
fun AppNavigation(darkMode: Boolean, onDarkModeToggle: (Boolean) -> Unit) {
    val navController = rememberNavController()

    Scaffold( bottomBar = {BottomNavigationBar(navController)}) { innerpadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerpadding)
        ){
            composable("home") { HomeScreen(navController = navController)}
            composable("search") { SearchScreen(navController = navController)}
            composable("detail/{movieId}") {backStackEntry ->
                val movieId = backStackEntry.arguments?.getString("movieId")?.toInt() ?:0
                DetailScreen(movieId = movieId, navController = navController)
            }

            composable("watchlist") { WatchlistScreen(navController) }
            composable("settings") {
                SettingsScreen(
                    darkMode = darkMode,
                    onDarkModeToggle = onDarkModeToggle
                )
            }
        }
    }
}
@Composable
fun BottomNavigationBar(navController: NavController){
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = { navController.navigate("home") },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = currentRoute == "search",
            onClick = { navController.navigate("search") },
            icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            label = { Text("Search") }
        )

        NavigationBarItem(
            selected = currentRoute == "watchlist",
            onClick = { navController.navigate("watchlist") },
            icon = { Icon(Icons.Default.Favorite, contentDescription = "Watchlist") },
            label = { Text("Watchlist") }
        )

        NavigationBarItem(
            selected = currentRoute == "settings",
            onClick = { navController.navigate("settings") },
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            label = { Text("Settings") }
        )
    }

}

