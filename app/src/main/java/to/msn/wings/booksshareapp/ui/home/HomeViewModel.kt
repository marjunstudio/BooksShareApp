package to.msn.wings.booksshareapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import to.msn.wings.booksshareapp.data.local.entity.BookEntity
import to.msn.wings.booksshareapp.data.repository.BookRepository
import javax.inject.Inject

sealed class DeleteBookStatus {
    object Success : DeleteBookStatus()
    data class Error(val message: String) : DeleteBookStatus()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _sortOrder = MutableStateFlow(SortOrder.NEWEST_FIRST)
    private val _books = MutableStateFlow<List<BookEntity>>(emptyList())

    private val _deleteStatus = MutableStateFlow<DeleteBookStatus?>(null)
    val deleteStatus: StateFlow<DeleteBookStatus?> = _deleteStatus

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    val books: StateFlow<List<BookEntity>> = combine(_books, _sortOrder) { books, sortOrder ->
        when (sortOrder) {
            SortOrder.NEWEST_FIRST -> books.sortedByDescending { it.readDate }
            SortOrder.OLDEST_FIRST -> books.sortedBy { it.readDate }
            SortOrder.TITLE_ASC -> books.sortedBy { it.title }
            SortOrder.TITLE_DESC -> books.sortedByDescending { it.title }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        loadBooks()
    }

    private fun loadBooks() {
        viewModelScope.launch {
            auth.currentUser?.let { user ->
                bookRepository.getAllBooks(user.uid).collect { booksList ->
                    _isLoading.value = false
                    _books.value = booksList
                }
            }
        }
    }

    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
    }

    fun deleteBook(book: BookEntity) {
        viewModelScope.launch {
            try {
                bookRepository.deleteBook(book)
                _deleteStatus.value = DeleteBookStatus.Success
            } catch (e: Exception) {
                _deleteStatus.value = DeleteBookStatus.Error(e.message ?: "削除に失敗しました")
            }
        }
    }

    fun clearDeleteStatus() {
        _deleteStatus.value = null
    }
} 