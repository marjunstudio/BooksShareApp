package to.msn.wings.booksshareapp.data.model

data class AuthState(
    val isAuthenticated: Boolean = false,
    val userId: String? = null,
    val error: String? = null
) 