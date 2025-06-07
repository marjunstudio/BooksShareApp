package to.msn.wings.booksshareapp.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import to.msn.wings.booksshareapp.data.remote.GoogleBooksApi
import to.msn.wings.booksshareapp.data.remote.VolumeInfo
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val googleBooksApi: GoogleBooksApi
) : ViewModel() {

    private val _searchResults = MutableStateFlow<List<VolumeInfo>>(emptyList())
    val searchResults: StateFlow<List<VolumeInfo>> = _searchResults

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun searchBooks(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }

        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = googleBooksApi.searchBooks(query)
                _searchResults.value = response.items ?: emptyList()
            } catch (e: Exception) {
                _searchResults.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
} 