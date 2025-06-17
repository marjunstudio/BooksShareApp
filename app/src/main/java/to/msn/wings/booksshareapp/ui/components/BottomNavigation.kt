package to.msn.wings.booksshareapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import to.msn.wings.booksshareapp.navigation.Screen

@Composable
fun BottomNavigation(
    navController: NavController
) {
    NavigationBar {
        val navBackStackEntry = navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry.value?.destination?.route

        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "ホーム") },
            label = { Text("ホーム") },
            selected = currentRoute == Screen.Home.route,
            onClick = {
                if (currentRoute != Screen.Home.route) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.DateRange, contentDescription = "読書記録") },
            label = { Text("読書記録") },
            selected = currentRoute == Screen.ReadingRecord.route,
            onClick = {
                if (currentRoute != Screen.ReadingRecord.route) {
                    navController.navigate(Screen.ReadingRecord.route) {
                        popUpTo(Screen.Home.route)
                    }
                }
            }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "マイページ") },
            label = { Text("マイページ") },
            selected = currentRoute == Screen.Profile.route,
            onClick = {
                if (currentRoute != Screen.Profile.route) {
                    navController.navigate(Screen.Profile.route) {
                        popUpTo(Screen.Home.route)
                    }
                }
            }
        )
    }
} 