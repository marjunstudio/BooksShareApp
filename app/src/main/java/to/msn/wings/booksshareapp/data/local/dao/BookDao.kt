package to.msn.wings.booksshareapp.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import to.msn.wings.booksshareapp.data.local.entity.BookEntity

@Dao
interface BookDao {
    @Query("SELECT * FROM books WHERE userId = :userId ORDER BY readDate DESC")
    fun getAllBooks(userId: String): Flow<List<BookEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: BookEntity)

    @Delete
    suspend fun deleteBook(book: BookEntity)

    @Query("SELECT * FROM books WHERE bookId = :bookId AND userId = :userId")
    suspend fun getBookById(bookId: String, userId: String): BookEntity?
} 