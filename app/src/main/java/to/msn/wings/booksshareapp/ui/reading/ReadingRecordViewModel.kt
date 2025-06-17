package to.msn.wings.booksshareapp.ui.reading

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import to.msn.wings.booksshareapp.data.local.entity.BookEntity
import to.msn.wings.booksshareapp.data.repository.BookRepository
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

data class CalendarDay(
    val day: Int,
    val isCurrentMonth: Boolean,
    val hasBook: Boolean
)

data class ReadingRecordState(
    val books: List<BookEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentDate: LocalDate = LocalDate.now(),
    val calendarDays: List<CalendarDay> = emptyList()
)

@HiltViewModel
class ReadingRecordViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val auth: FirebaseAuth
) : ViewModel() {
    private val _state = MutableStateFlow(ReadingRecordState())
    val state: StateFlow<ReadingRecordState> = _state

    init {
        loadBooks()
        updateCalendar()
    }

    private fun loadBooks() {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)
                auth.currentUser?.let { user ->
                    bookRepository.getAllBooks(user.uid).collect { books ->
                        _state.value = _state.value.copy(
                            books = books,
                            isLoading = false
                        )
                        updateCalendar()
                    }
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "書籍の読み込みに失敗しました: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun updateMonth(monthsToAdd: Int) {
        val newDate = _state.value.currentDate.plusMonths(monthsToAdd.toLong())
        _state.value = _state.value.copy(currentDate = newDate)
        updateCalendar()
    }

    private fun updateCalendar() {
        val currentDate = _state.value.currentDate
        val yearMonth = YearMonth.from(currentDate)
        val firstDayOfMonth = yearMonth.atDay(1)
        val lastDayOfMonth = yearMonth.atEndOfMonth()
        
        // 前月の日数を計算
        val previousMonth = yearMonth.minusMonths(1)
        val daysInPreviousMonth = previousMonth.lengthOfMonth()
        
        // 月初めの空白を計算（日曜日始まり）
        val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
        val offsetDays = if (firstDayOfWeek == 0) 7 else firstDayOfWeek
        
        val calendarDays = mutableListOf<CalendarDay>()
        
        // 前月の日付を追加
        for (i in (daysInPreviousMonth - offsetDays + 1)..daysInPreviousMonth) {
            calendarDays.add(CalendarDay(i, false, false))
        }
        
        // 当月の日付を追加
        for (day in 1..lastDayOfMonth.dayOfMonth) {
            val date = yearMonth.atDay(day)
            val hasBook = hasBookOnDate(date)
            calendarDays.add(CalendarDay(day, true, hasBook))
        }
        
        // 次月の日付を追加（6週間分になるように）
        val remainingDays = 42 - calendarDays.size
        for (day in 1..remainingDays) {
            calendarDays.add(CalendarDay(day, false, false))
        }
        
        _state.value = _state.value.copy(calendarDays = calendarDays)
    }

    private fun hasBookOnDate(date: LocalDate): Boolean {
        return _state.value.books.any { book ->
            book.readStartDate?.let { startDate ->
                book.readEndDate?.let { endDate ->
                    !date.isBefore(startDate) && !date.isAfter(endDate)
                } ?: false
            } ?: false
        }
    }
} 