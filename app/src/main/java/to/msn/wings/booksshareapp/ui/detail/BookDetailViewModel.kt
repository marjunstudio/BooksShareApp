package to.msn.wings.booksshareapp.ui.detail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import to.msn.wings.booksshareapp.data.remote.VolumeInfo
import to.msn.wings.booksshareapp.data.repository.BookRepository
import javax.inject.Inject

sealed class SaveBookStatus {
    object Success : SaveBookStatus()
    data class Error(val message: String) : SaveBookStatus()
}

@HiltViewModel
class BookDetailViewModel @Inject constructor(
    private val bookRepository: BookRepository
) : ViewModel() {

    private val _saveBookStatus = MutableStateFlow<SaveBookStatus?>(null)
    val saveBookStatus: StateFlow<SaveBookStatus?> = _saveBookStatus

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveBook(book: VolumeInfo, userId: String) {
        viewModelScope.launch {
            try {
                bookRepository.addBook(book, userId)
                _saveBookStatus.value = SaveBookStatus.Success
            } catch (e: Exception) {
                _saveBookStatus.value = SaveBookStatus.Error("書籍の保存に失敗しました: ${e.message}")
            }
        }
    }

    fun clearSaveStatus() {
        _saveBookStatus.value = null
    }
} 