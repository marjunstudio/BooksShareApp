package to.msn.wings.booksshareapp.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.flow.Flow
import to.msn.wings.booksshareapp.data.local.dao.BookDao
import to.msn.wings.booksshareapp.data.local.entity.BookEntity
import to.msn.wings.booksshareapp.data.remote.VolumeInfo
import java.time.LocalDate
import javax.inject.Inject

class BookRepository @Inject constructor(
    private val bookDao: BookDao
) {
    fun getAllBooks(userId: String): Flow<List<BookEntity>> {
        return bookDao.getAllBooks(userId)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun addBook(volumeInfo: VolumeInfo, userId: String) {
        val bookEntity = BookEntity(
            bookId = volumeInfo.id,
            userId = userId,
            title = volumeInfo.volumeInfo.title,
            authors = volumeInfo.volumeInfo.authors?.joinToString(", "),
            description = volumeInfo.volumeInfo.description,
            thumbnailUrl = volumeInfo.volumeInfo.imageLinks?.thumbnail?.replace("http:", "https:"),
            impressiveQuote = null,
            trigger = null,
            thoughts = null,
            regDate = LocalDate.now(),
            readStartDate = null,
            readEndDate = null
        )
        bookDao.insertBook(bookEntity)
    }

    suspend fun getBookById(bookId: String, userId: String): BookEntity? {
        return bookDao.getBookById(bookId, userId)
    }

    suspend fun deleteBook(book: BookEntity) {
        bookDao.deleteBook(book)
    }

    suspend fun updateBook(book: BookEntity) {
        bookDao.updateBook(book)
    }
} 