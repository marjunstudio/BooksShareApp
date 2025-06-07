package to.msn.wings.booksshareapp.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object AddBook : Screen("add_book")
    object Profile : Screen("profile")
    object Search : Screen("search")
    object BookDetail : Screen("book_detail/{bookJson}") {
        fun createRoute(bookJson: String) = "book_detail/$bookJson"
    }
    object BookRecord : Screen("book_record/{bookId}") {
        fun createRoute(bookId: String) = "book_record/$bookId"
    }
} 