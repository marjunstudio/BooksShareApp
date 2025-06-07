package to.msn.wings.booksshareapp.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import to.msn.wings.booksshareapp.R
import to.msn.wings.booksshareapp.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    navController: NavController
) {
    val currentRoute = navController.currentBackStackEntry?.destination?.route
    val titleResId = when (currentRoute) {
        Screen.Home.route -> R.string.nav_home
        Screen.Profile.route -> R.string.nav_myPage
        Screen.Search.route -> R.string.nav_search
        else -> R.string.app_name
    }

    TopAppBar(
        title = { Text(text = stringResource(titleResId)) }
    )
} 