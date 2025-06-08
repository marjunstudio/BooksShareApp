package to.msn.wings.booksshareapp.ui.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import to.msn.wings.booksshareapp.data.local.entity.BookEntity
import to.msn.wings.booksshareapp.data.repository.BookRepository
import javax.inject.Inject

@HiltViewModel
class BookRecordViewModel @Inject constructor(
    private val bookRepository: BookRepository
) : ViewModel() {
    private val _book = MutableStateFlow<BookEntity?>(null)
    val book: StateFlow<BookEntity?> = _book

    fun loadBook(bookId: String, userId: String) {
        viewModelScope.launch {
            _book.value = bookRepository.getBookById(bookId, userId)
        }
    }

    fun updateBook(updatedBook: BookEntity) {
        viewModelScope.launch {
            bookRepository.updateBook(updatedBook)
            _book.value = updatedBook
        }
    }
} 