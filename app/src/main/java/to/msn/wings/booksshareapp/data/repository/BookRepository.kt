package to.msn.wings.booksshareapp.data.repository

import kotlinx.coroutines.flow.Flow
import to.msn.wings.booksshareapp.data.local.dao.BookDao
import to.msn.wings.booksshareapp.data.local.entity.BookEntity
import to.msn.wings.booksshareapp.data.remote.VolumeInfo
import java.util.Date
import javax.inject.Inject

class BookRepository @Inject constructor(
    private val bookDao: BookDao
) {
    fun getAllBooks(userId: String): Flow<List<BookEntity>> {
        return bookDao.getAllBooks(userId)
    }

    suspend fun addBook(volumeInfo: VolumeInfo, userId: String) {
        val bookEntity = BookEntity(
            bookId = volumeInfo.id,
            title = volumeInfo.volumeInfo.title,
            authors = volumeInfo.volumeInfo.authors?.joinToString(", "),
            thumbnailUrl = volumeInfo.volumeInfo.imageLinks?.thumbnail?.replace("http:", "https:"),
            readDate = Date(), // 現在の日付をデフォルトとして設定
            trigger = null,
            impressiveQuote = null,
            thoughts = null,
            userId = userId
        )
        bookDao.insertBook(bookEntity)
    }

    suspend fun getBookById(bookId: String, userId: String): BookEntity? {
        return bookDao.getBookById(bookId, userId)
    }

    suspend fun deleteBook(book: BookEntity) {
        bookDao.deleteBook(book)
    }
} 