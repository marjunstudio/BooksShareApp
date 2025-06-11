package to.msn.wings.booksshareapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import to.msn.wings.booksshareapp.data.remote.VolumeInfo
import to.msn.wings.booksshareapp.navigation.Screen
import to.msn.wings.booksshareapp.ui.auth.AuthViewModel
import to.msn.wings.booksshareapp.ui.components.BottomNavigation
import to.msn.wings.booksshareapp.ui.components.TopBar
import to.msn.wings.booksshareapp.ui.detail.BookDetailScreen
import to.msn.wings.booksshareapp.ui.home.HomeScreen
import to.msn.wings.booksshareapp.ui.profile.ProfileScreen
import to.msn.wings.booksshareapp.ui.record.BookRecordScreen
import to.msn.wings.booksshareapp.ui.search.SearchScreen
import to.msn.wings.booksshareapp.ui.theme.BooksShareAppTheme
import java.net.URLDecoder
import java.net.URLEncoder

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BooksShareAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BooksShareApp()
                }
            }
        }
    }
}

@Composable
fun BooksShareApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val authViewModel: AuthViewModel = hiltViewModel()
    val currentUser by authViewModel.currentUser.collectAsState()

    // ボトムナビゲーションを表示するルートのリスト
    val showBottomNavRoutes = listOf(Screen.Home.route, Screen.Profile.route)
    val shouldShowBottomNav = currentRoute in showBottomNavRoutes

    Scaffold(
        topBar = { TopBar(navController = navController) },
        bottomBar = { 
            if (shouldShowBottomNav) {
                BottomNavigation(navController = navController)
            }
        },
        floatingActionButton = {
            // ホーム画面でのみFABを表示
            if (currentRoute == Screen.Home.route) {
                FloatingActionButton(
                    onClick = {
                        navController.navigate(Screen.Search.route)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "書籍を追加"
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route
            ) {
                composable(Screen.Home.route) {
                    HomeScreen(
                        onNavigateToSearch = {
                            navController.navigate(Screen.Search.route)
                        },
                        onNavigateToRecord = { bookId ->
                            navController.navigate(Screen.BookRecord.createRoute(bookId))
                        }
                    )
                }

                composable(Screen.Profile.route) {
                    ProfileScreen()
                }

                composable(Screen.Search.route) {
                    SearchScreen(
                        onNavigateToDetail = { bookJson ->
                            val encodedJson = URLEncoder.encode(bookJson, "UTF-8")
                            navController.navigate(Screen.BookDetail.createRoute(encodedJson))
                        }
                    )
                }

                composable(
                    route = Screen.BookDetail.route,
                    arguments = listOf(
                        navArgument("bookJson") {
                            type = NavType.StringType
                        }
                    )
                ) { backStackEntry ->
                    val bookJson = URLDecoder.decode(
                        backStackEntry.arguments?.getString("bookJson") ?: "",
                        "UTF-8"
                    )
                    val book = Gson().fromJson(bookJson, VolumeInfo::class.java)
                    BookDetailScreen(
                        book = book,
                        userId = currentUser?.uid ?: "",
                        onNavigateToHome = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        }
                    )
                }

                composable(
                    route = Screen.BookRecord.route,
                    arguments = listOf(
                        navArgument("bookId") {
                            type = NavType.StringType
                        }
                    )
                ) { backStackEntry ->
                    val bookId = backStackEntry.arguments?.getString("bookId") ?: return@composable
                    BookRecordScreen(
                        bookId = bookId,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}