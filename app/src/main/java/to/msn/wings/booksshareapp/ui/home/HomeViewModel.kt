package to.msn.wings.booksshareapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import to.msn.wings.booksshareapp.data.local.dao.BookDao
import to.msn.wings.booksshareapp.data.local.entity.BookEntity
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val bookDao: BookDao,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _books = MutableStateFlow<List<BookEntity>>(emptyList())
    val books: StateFlow<List<BookEntity>> = _books

    init {
        loadBooks()
    }

    private fun loadBooks() {
        viewModelScope.launch {
            auth.currentUser?.let { user ->
                bookDao.getAllBooks(user.uid).collectLatest { bookList ->
                    _books.value = bookList
                }
            } ?: run {
                // ログインしていない場合は空のリストを表示
                _books.value = emptyList()
            }
        }
    }
} 