package to.msn.wings.booksshareapp.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import to.msn.wings.booksshareapp.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    navController: NavController
) {
    val currentRoute = navController.currentBackStackEntry?.destination?.route
    val title = when (currentRoute) {
        Screen.Home.route -> "ホーム"
        Screen.Profile.route -> "マイページ"
        Screen.Search.route -> "書籍登録"
        else -> "BooksShare"
    }

    TopAppBar(
        title = { Text(text = title) }
    )
} 